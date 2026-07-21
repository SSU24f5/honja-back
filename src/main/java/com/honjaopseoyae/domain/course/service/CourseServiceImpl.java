package com.honjaopseoyae.domain.course.service;

import com.honjaopseoyae.common.UserReader;
import com.honjaopseoyae.config.KakaoLocalClient;
import com.honjaopseoyae.config.KakaoMobilityClient;
import com.honjaopseoyae.domain.course.dto.request.CourseCreateRequestDto;
import com.honjaopseoyae.domain.course.dto.request.CourseUpdateRequestDto;
import com.honjaopseoyae.domain.course.dto.response.CourseCreateResponseDto;
import com.honjaopseoyae.domain.course.dto.response.CourseDetailResponseDto;
import com.honjaopseoyae.domain.course.dto.response.CourseListResponseDto;
import com.honjaopseoyae.domain.course.dto.response.CourseUpdateResponseDto;
import com.honjaopseoyae.domain.course.entity.enums.CourseRole;
import com.honjaopseoyae.domain.course.entity.enums.InviteStatus;
import com.honjaopseoyae.domain.course.entity.mapping.CourseMember;
import com.honjaopseoyae.domain.course.repository.CourseMemberRepository;
import com.honjaopseoyae.domain.course.repository.CoursePlaceRepository;
import com.honjaopseoyae.domain.course.repository.CourseRepository;
import com.honjaopseoyae.domain.course.entity.Course;
import com.honjaopseoyae.domain.course.entity.mapping.CoursePlace;
import com.honjaopseoyae.domain.place.entity.Place;
import com.honjaopseoyae.domain.user.entity.User;
import com.honjaopseoyae.global.apipayload.domain.CourseErrorStatus;
import com.honjaopseoyae.global.apipayload.exception.GeneralException;
import com.honjaopseoyae.domain.place.entity.PlaceType;
import com.honjaopseoyae.domain.place.repository.PlaceRepository;
import com.honjaopseoyae.domain.place.service.TourApiService;
import com.honjaopseoyae.domain.course.support.CourseFinder;
import java.util.TreeMap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import com.honjaopseoyae.domain.course.dto.request.CourseInviteRequestDto;
import com.honjaopseoyae.domain.course.dto.response.CourseInvitationResponseDto;
import com.honjaopseoyae.domain.user.repository.UserRepository;
import com.honjaopseoyae.global.apipayload.domain.UserErrorStatus;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CourseServiceImpl implements CourseService {
    private final CourseFinder courseFinder;
    private final CourseRepository courseRepository;
    private final CourseMemberRepository courseMemberRepository;
    private final UserReader userReader;
    private final CoursePlaceRepository coursePlaceRepository;
    private final PlaceRepository placeRepository;
    private final TourApiService tourApiService;
    private final KakaoMobilityClient kakaoMobilityClient;
    private final KakaoLocalClient kakaoLocalClient;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    @Override
    public CourseDetailResponseDto getCourseDetail(Long courseId, Long userId) {
        Course course = courseFinder.findById(courseId);

        List<CoursePlace> coursePlaces =
            coursePlaceRepository.findAllByCourseId(courseId);

        List<CourseDetailResponseDto.CourseDateItem> dateItems =
            coursePlaces.stream()
                .collect(Collectors.groupingBy(CoursePlace::getDate, TreeMap::new, Collectors.toList()))
                .entrySet()
                .stream()
                .map(entry ->
                    CourseDetailResponseDto.CourseDateItem.of(
                        entry.getKey(),
                        entry.getValue()
                            .stream()
                            .sorted(Comparator.comparing(
                                CoursePlace::getSortOrder
                            ))
                            .map(CourseDetailResponseDto.CoursePlaceItem::from)
                            .toList()
                    )
                )
                .toList();

        return CourseDetailResponseDto.of(course, dateItems);
    }

    @Transactional(readOnly = true)
    @Override
    public List<CourseListResponseDto> getMyCourses(Long userId) {
        userReader.getById(userId);

        return courseMemberRepository.findAllByUserIdAndStatus(userId, InviteStatus.ACCEPTED)
            .stream()
            .map(CourseMember::getCourse)
            .sorted(Comparator.comparing(Course::getCreatedAt).reversed())
            .map(CourseListResponseDto::from)
            .toList();
    }

    @Transactional
    @Override
    public CourseCreateResponseDto createCourse(CourseCreateRequestDto requestDto, Long userId) {
        User user = userReader.getById(userId);
        Course course = CourseCreateRequestDto.toEntity(requestDto);
        Course savedCourse = courseRepository.save(course);

        CourseMember owner = CourseMember.builder()
            .course(savedCourse)
            .user(user)
            .role(CourseRole.OWNER)
            .status(InviteStatus.ACCEPTED)
            .build();

        courseMemberRepository.save(owner);

        return CourseCreateResponseDto.from(savedCourse);
    }

    @Transactional
    @Override
    public CourseUpdateResponseDto updateCourse(CourseUpdateRequestDto requestDto, Long userId) {
        Course course = courseFinder.findById(requestDto.getCourseId());

        courseMemberRepository.findByCourseIdAndUserIdAndStatus(course.getId(), userId, InviteStatus.ACCEPTED)
            .orElseThrow(() -> new GeneralException(CourseErrorStatus.COURSE_NOT_WRITER));

        List<CoursePlace> existingCoursePlaces =
            coursePlaceRepository.findAllByCourseId(course.getId());

        List<CourseUpdateRequestDto.CoursePlaceItem> incomingPlaces =
            requestDto.getDates().stream()
                .flatMap(dateItem -> dateItem.getPlaces().stream())
                .toList();

        deleteRemovedPlaces(existingCoursePlaces, incomingPlaces);

        List<CoursePlace> updatedCoursePlaces =
            requestDto.getDates().stream()
                .flatMap(dateItem ->
                    dateItem.getPlaces().stream()
                        .map(item -> processCoursePlaceItem(
                            course,
                            dateItem.getDate(),
                            item,
                            existingCoursePlaces
                        ))
                )
                .toList();

        updateCoursePlaceRouteInfos(updatedCoursePlaces);

        return buildUpdateResponse(course, updatedCoursePlaces);
    }

    private void deleteRemovedPlaces(List<CoursePlace> existingPlaces, List<CourseUpdateRequestDto.CoursePlaceItem> incomingPlaces) {
        Set<Long> incomingIds = incomingPlaces.stream()
                .map(CourseUpdateRequestDto.CoursePlaceItem::getCoursePlaceId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        List<CoursePlace> toDelete = existingPlaces.stream()
                .filter(cp -> !incomingIds.contains(cp.getId()))
                .collect(Collectors.toList());

        coursePlaceRepository.deleteAll(toDelete);
    }

    private CoursePlace processCoursePlaceItem(
        Course course,
        LocalDate date,
        CourseUpdateRequestDto.CoursePlaceItem item,
        List<CoursePlace> existingPlaces
    ) {
        if (item.isNewItem()) {
            Place place = getOrCreatePlace(item);

            CoursePlace coursePlace = CoursePlace.builder()
                .course(course)
                .place(place)
                .date(date)
                .sortOrder(item.getOrder().longValue())
                .orderType(item.getOrderType())
                .build();

            return coursePlaceRepository.save(coursePlace);
        }

        CoursePlace existing = existingPlaces.stream()
            .filter(cp -> cp.getId().equals(item.getCoursePlaceId()))
            .findFirst()
            .orElseThrow(() ->
                new GeneralException(
                    CourseErrorStatus.COURSE_PLACE_NOT_FOUND
                ));

        existing.updateSortOrder(item.getOrder().longValue(), item.getOrderType());
        existing.updateDate(date);

        return existing;
    }

    private void updateCoursePlaceRouteInfos(List<CoursePlace> coursePlaces) {
        coursePlaces.stream()
            .collect(Collectors.groupingBy(
                CoursePlace::getDate,
                TreeMap::new,
                Collectors.toList()
            ))
            .values()
            .forEach(dateCoursePlaces -> {
                dateCoursePlaces.sort(
                    Comparator.comparing(CoursePlace::getSortOrder)
                );

                CoursePlace previous = null;
                for (CoursePlace current : dateCoursePlaces) {
                    if (previous == null) {
                        current.updateRouteInfo(null, null);
                        previous = current;
                        continue;
                    }

                    KakaoMobilityClient.RouteSummary summary =
                        kakaoMobilityClient.getRouteSummary(
                            previous.getPlace().getMapx(),
                            previous.getPlace().getMapy(),
                            current.getPlace().getMapx(),
                            current.getPlace().getMapy()
                        );

                    if (summary == null) {
                        current.updateRouteInfo(null, null);
                    } else {
                        current.updateRouteInfo(
                            toKilometers(summary.distanceMeters()),
                            toMinutes(summary.durationSeconds())
                        );
                    }

                    previous = current;
                }
            });

        coursePlaceRepository.saveAll(coursePlaces);
    }

    private Place getOrCreatePlace(
        CourseUpdateRequestDto.CoursePlaceItem item
    ) {
        // 이미 저장된 Place
        if (item.getPlaceId() != null) {
            return placeRepository.findById(item.getPlaceId())
                .orElseThrow(() ->
                    new GeneralException(
                        CourseErrorStatus.PLACE_NOT_FOUND
                    ));
        }

        return switch (item.getPlaceType()) {
            case TOUR_PLACE -> getOrCreateTourPlace(item);
            case INDIVIDUAL_PLACE -> createIndividualPlace(item);
        };
    }

    private Place getOrCreateTourPlace(
        CourseUpdateRequestDto.CoursePlaceItem item
    ) {
        if (item.getContentId() == null || item.getContentId().isBlank()) {
            throw new GeneralException(
                CourseErrorStatus.PLACE_NOT_FOUND
            );
        }

        return placeRepository.findByContentId(item.getContentId())
            .orElseGet(() -> fetchAndSavePlaceFromApi(item));
    }

    private Place createIndividualPlace(
        CourseUpdateRequestDto.CoursePlaceItem item
    ) {
        double rawMapx = parseDouble(item.getMapx());
        double rawMapy = parseDouble(item.getMapy());

        return placeRepository.findByMapxAndMapyAndPlaceType(rawMapx, rawMapy, PlaceType.INDIVIDUAL_PLACE)
            .orElseGet(() -> placeRepository.save(
                Place.builder()
                    .mapx(rawMapx)
                    .mapy(rawMapy)
                    .tourMapx(rawMapx)
                    .tourMapy(rawMapy)
                    .image(item.getImage())
                    .contentType(parseContentType(item.getContentTypeId()))
                    .cat3(item.getCat3())
                    .placeType(PlaceType.INDIVIDUAL_PLACE)
                    .petPlace(false)
                    .barrierFree(false)
                    .build()
            ));
    }


    private Place fetchAndSavePlaceFromApi(CourseUpdateRequestDto.CoursePlaceItem item) {
        try {
            if (Boolean.TRUE.equals(item.getIsBarrierFree())) {
                tourApiService.getBarrierFreeInfo(Long.parseLong(item.getContentId()));
            }
            if (Boolean.TRUE.equals(item.getIsPetPlace())) {
                tourApiService.getPetDetailInfo(Long.parseLong(item.getContentId()));
            }

            double rawMapx = parseDouble(item.getMapx());
            double rawMapy = parseDouble(item.getMapy());
            double kakaoMapx = rawMapx;
            double kakaoMapy = rawMapy;

            if (item.getTitle() != null && !item.getTitle().isBlank() && kakaoLocalClient != null) {
                KakaoLocalClient.RoadCoordinate road = kakaoLocalClient.searchKeyword(item.getTitle());
                if (road != null) {
                    kakaoMapx = road.x();
                    kakaoMapy = road.y();
                }
            }

            Place newPlace = Place.builder()
                    .contentId(item.getContentId())
                    .contentType(parseContentType(item.getContentTypeId()))
                    .cat3(item.getCat3())
                    .image(item.getImage())
                    .placeType(item.getPlaceType() != null ? item.getPlaceType() : PlaceType.TOUR_PLACE)
                    .petPlace(Boolean.TRUE.equals(item.getIsPetPlace()))
                    .barrierFree(Boolean.TRUE.equals(item.getIsBarrierFree()))
                    .mapx(kakaoMapx)
                    .mapy(kakaoMapy)
                    .tourMapx(rawMapx)
                    .tourMapy(rawMapy)
                    .build();

            return placeRepository.save(newPlace);
        } catch (Exception e) {
            return null;
        }
    }

    private CourseUpdateResponseDto buildUpdateResponse(
        Course course,
        List<CoursePlace> updatedPlaces
    ) {
        List<CourseUpdateResponseDto.CourseDateResult> dateResults =
            updatedPlaces.stream()
                .collect(Collectors.groupingBy(
                    CoursePlace::getDate,
                    TreeMap::new,
                    Collectors.toList()
                ))
                .entrySet()
                .stream()
                .map(entry ->
                    CourseUpdateResponseDto.CourseDateResult.of(
                        entry.getKey(),
                        entry.getValue()
                            .stream()
                            .sorted(Comparator.comparing(
                                CoursePlace::getSortOrder
                            ))
                            .map(
                                CourseUpdateResponseDto
                                    .CoursePlaceResult::from
                            )
                            .toList()
                    )
                )
                .toList();

        return CourseUpdateResponseDto.of(course, dateResults);
    }

    @Transactional
    @Override
    public void deleteCourse(Long courseId, Long userId) {
        Course course = courseFinder.findById(courseId);

        courseMemberRepository.findByCourseIdAndUserIdAndRoleAndStatus(
                courseId,
                userId,
                CourseRole.OWNER,
                InviteStatus.ACCEPTED
            )
            .orElseThrow(() ->
                new GeneralException(CourseErrorStatus.COURSE_NOT_WRITER));

        courseRepository.delete(course);
    }

    private double parseDouble(String value) {
        if (value == null || value.trim().isEmpty()) {
            return 0.0;
        }
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private Integer parseContentType(String contentTypeId) {
        if (contentTypeId == null || contentTypeId.trim().isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(contentTypeId.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String toKilometers(int distanceMeters) {
        return String.format(Locale.US, "%.1f", distanceMeters / 1000.0);
    }

    private String toMinutes(int durationSeconds) {
        return String.valueOf((int) Math.ceil(durationSeconds / 60.0));
    }

    @Override
    public void inviteMemberByEmail(CourseInviteRequestDto requestDto, Long currentUserId) {
        Course course = courseFinder.findById(requestDto.getCourseId());

        courseMemberRepository.findByCourseIdAndUserIdAndStatus(course.getId(), currentUserId, InviteStatus.ACCEPTED)
            .orElseThrow(() -> new GeneralException(CourseErrorStatus.COURSE_ACCESS_DENIED));

        User targetUser = userRepository.findByEmail(requestDto.getEmail())
            .orElseThrow(() -> new GeneralException(UserErrorStatus.USER_NOT_FOUND));

        if (targetUser.getId().equals(currentUserId)) {
            throw new GeneralException(CourseErrorStatus.CANNOT_INVITE_SELF);
        }

        courseMemberRepository.findByCourseIdAndUserId(course.getId(), targetUser.getId())
            .ifPresent(cm -> {
                throw new GeneralException(CourseErrorStatus.ALREADY_INVITED_OR_MEMBER);
            });

        CourseMember invitation = CourseMember.builder()
            .course(course)
            .user(targetUser)
            .role(CourseRole.MEMBER)
            .status(InviteStatus.PENDING)
            .build();

        courseMemberRepository.save(invitation);
    }

    @Transactional(readOnly = true)
    @Override
    public List<CourseInvitationResponseDto> getMyInvitations(Long currentUserId) {
        List<CourseMember> pendingMemberships = courseMemberRepository.findAllByUserIdAndStatus(currentUserId, InviteStatus.PENDING);

        return pendingMemberships.stream()
            .map(member -> {
                CourseMember owner = member.getCourse().getMembers().stream()
                    .filter(m -> m.getRole() == CourseRole.OWNER)
                    .findFirst()
                    .orElse(null);

                String inviterName = (owner != null && owner.getUser() != null) ? owner.getUser().getNickname() : null;
                String inviterEmail = (owner != null && owner.getUser() != null) ? owner.getUser().getEmail() : null;

                return CourseInvitationResponseDto.from(member, inviterName, inviterEmail);
            })
            .collect(Collectors.toList());
    }

    @Override
    public void acceptInvitation(Long courseMemberId, Long currentUserId) {
        CourseMember courseMember = courseMemberRepository.findById(courseMemberId)
            .orElseThrow(() -> new GeneralException(CourseErrorStatus.INVITATION_NOT_FOUND));

        if (!courseMember.getUser().getId().equals(currentUserId)) {
            throw new GeneralException(CourseErrorStatus.COURSE_ACCESS_DENIED);
        }

        if (courseMember.getStatus() != InviteStatus.PENDING) {
            throw new GeneralException(CourseErrorStatus.INVALID_INVITATION_STATUS);
        }

        courseMember.accept();
    }

    @Override
    public void rejectInvitation(Long courseMemberId, Long currentUserId) {
        CourseMember courseMember = courseMemberRepository.findById(courseMemberId)
            .orElseThrow(() -> new GeneralException(CourseErrorStatus.INVITATION_NOT_FOUND));

        if (!courseMember.getUser().getId().equals(currentUserId)) {
            throw new GeneralException(CourseErrorStatus.COURSE_ACCESS_DENIED);
        }

        if (courseMember.getStatus() != InviteStatus.PENDING) {
            throw new GeneralException(CourseErrorStatus.INVALID_INVITATION_STATUS);
        }

        courseMember.reject();
    }
}
