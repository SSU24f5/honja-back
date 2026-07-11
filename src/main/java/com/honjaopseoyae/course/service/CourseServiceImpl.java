package com.honjaopseoyae.course.service;

import com.honjaopseoyae.config.KakaoMobilityClient;
import com.honjaopseoyae.course.dto.request.CourseCreateRequestDto;
import com.honjaopseoyae.course.dto.request.CourseUpdateRequestDto;
import com.honjaopseoyae.course.dto.response.CourseCreateResponseDto;
import com.honjaopseoyae.course.dto.response.CourseDetailResponseDto;
import com.honjaopseoyae.course.dto.response.CourseUpdateResponseDto;
import com.honjaopseoyae.course.repository.CoursePlaceRepository;
import com.honjaopseoyae.course.repository.CourseRepository;
import com.honjaopseoyae.domain.course.entity.Course;
import com.honjaopseoyae.domain.course.entity.mapping.CoursePlace;
import com.honjaopseoyae.domain.place.entity.Place;
import com.honjaopseoyae.domain.user.entity.User;
import com.honjaopseoyae.global.apipayload.domain.CourseErrorStatus;
import com.honjaopseoyae.global.apipayload.exception.GeneralException;
import com.honjaopseoyae.member.repository.UserRepository;
import com.honjaopseoyae.place.repository.PlaceRepository;
import com.honjaopseoyae.place.repository.TourPlaceRepository;
import com.honjaopseoyae.place.service.TourApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final CoursePlaceRepository coursePlaceRepository;
    private final PlaceRepository placeRepository;
    private final TourApiService tourApiService;
    private final KakaoMobilityClient kakaoMobilityClient;
    private final TourPlaceRepository tourPlaceRepository;

    @Transactional(readOnly = true)
    @Override
    public CourseDetailResponseDto getCourseDetail(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new GeneralException(CourseErrorStatus.COURSE_NOT_FOUND));

        List<CoursePlace> coursePlaces = coursePlaceRepository.findAllByCourseId(courseId);
        coursePlaces.sort(Comparator.comparing(CoursePlace::getSortOrder));

        List<CourseDetailResponseDto.CoursePlaceItem> placeItems = coursePlaces.stream()
                .map(cp -> {
                    Place place = cp.getPlace();
                    String title = null;
                    String addr1 = null;
                    String addr2 = null;

                    if (place.getContentId() != null) {
                        var tourPlaceOpt = tourPlaceRepository.findById(place.getContentId());
                        if (tourPlaceOpt.isPresent()) {
                            var tourPlace = tourPlaceOpt.get();
                            title = tourPlace.getTitle();
                            addr1 = tourPlace.getAddr1();
                            addr2 = tourPlace.getAddr2();
                        }
                    }

                    return CourseDetailResponseDto.CoursePlaceItem.builder()
                            .coursePlaceId(cp.getId())
                            .placeId(place.getId())
                            .order(cp.getSortOrder().intValue())
                            .distance(cp.getDistance())
                            .timeTaken(cp.getTimeTaken())
                            .contentId(place.getContentId())
                            .title(title)
                            .addr1(addr1)
                            .addr2(addr2)
                            .mapx(place.getMapx())
                            .mapy(place.getMapy())
                            .image(place.getImage())
                            .petPlace(place.isPetPlace())
                            .barrierFree(place.isBarrierFree())
                            .build();
                })
                .collect(Collectors.toList());

        return CourseDetailResponseDto.builder()
                .courseId(course.getId())
                .name(course.getName())
                .description(course.getDescription())
                .isPublic(course.isPublic())
                .isExternal(course.isExternal())
                .startDate(course.getStartDate())
                .endDate(course.getEndDate())
                .courseType(course.getCourseType())
                .places(placeItems)
                .build();
    }

    @Transactional
    @Override
    public CourseCreateResponseDto createCourse(CourseCreateRequestDto requestDto) {
        User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new GeneralException(CourseErrorStatus.USER_NOT_FOUND));

        Course course = Course.builder()
                .user(user)
                .name(requestDto.getName())
                .description(requestDto.getDescription())
                .isPublic(requestDto.getIsPublic())
                .isExternal(requestDto.getIsExternal())
                .startDate(requestDto.getStartDate())
                .endDate(requestDto.getEndDate())
                .courseType(requestDto.getCourseType())
                .build();

        Course savedCourse = courseRepository.save(course);
        return CourseCreateResponseDto.from(savedCourse);
    }

    @Transactional
    @Override
    public CourseUpdateResponseDto updateCourse(CourseUpdateRequestDto requestDto) {
        Course course = courseRepository.findById(requestDto.getCourseId())
                .orElseThrow(() -> new GeneralException(CourseErrorStatus.COURSE_NOT_FOUND));

        List<CoursePlace> existingCoursePlaces = coursePlaceRepository.findAllByCourseId(course.getId());

        // 1. 삭제할 CoursePlace 제거
        deleteRemovedPlaces(existingCoursePlaces, requestDto.getPlaces());

        // 2. 신규 생성 및 기존 항목 순서 업데이트
        List<CoursePlace> updatedCoursePlaces = new ArrayList<>();
        for (CourseUpdateRequestDto.CoursePlaceItem item : requestDto.getPlaces()) {
            updatedCoursePlaces.add(processCoursePlaceItem(course, item, existingCoursePlaces));
        }

        // 3. 결과 반환
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

    private CoursePlace processCoursePlaceItem(Course course, CourseUpdateRequestDto.CoursePlaceItem item, List<CoursePlace> existingPlaces) {
        if (item.isNewItem()) {
            Place place = getOrCreatePlace(item);
            CoursePlace newCoursePlace = CoursePlace.builder()
                    .course(course)
                    .place(place)
                    .sortOrder((long) item.getOrder())
                    .build();
            return coursePlaceRepository.save(newCoursePlace);
        } else {
            CoursePlace existing = existingPlaces.stream()
                    .filter(cp -> cp.getId().equals(item.getCoursePlaceId()))
                    .findFirst()
                    .orElseThrow(() -> new GeneralException(CourseErrorStatus.COURSE_NOT_FOUND));

            existing.updateSortOrder((long) item.getOrder());
            return coursePlaceRepository.save(existing);
        }
    }

    private void updateCoursePlaceRouteInfos(List<CoursePlace> coursePlaces) {
        coursePlaces.sort(Comparator.comparing(CoursePlace::getSortOrder));

        CoursePlace previous = null;
        for (CoursePlace current : coursePlaces) {
            if (previous == null) {
                current.updateRouteInfo(null, null);
            } else {
                KakaoMobilityClient.RouteSummary summary = kakaoMobilityClient.getRouteSummary(
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
            }

            previous = current;
        }

        coursePlaceRepository.saveAll(coursePlaces);
    }

    private Place getOrCreatePlace(CourseUpdateRequestDto.CoursePlaceItem item) {
        // 1. placeId가 전달된 경우: 기존 저장되어 있는 장소 조회 (없으면 예외 발생)
        if (item.getPlaceId() != null) {
            return placeRepository.findById(item.getPlaceId())
                    .orElseThrow(() -> new GeneralException(CourseErrorStatus.PLACE_NOT_FOUND));
        }

        // 2. placeId가 없고 contentId가 전달된 경우: 로컬 DB 조회 후 없으면 신규 저장
        if (item.getContentId() != null && !item.getContentId().trim().isEmpty()) {
            List<Place> dbPlaces = placeRepository.findAllByContentId(item.getContentId());
            if (!dbPlaces.isEmpty()) {
                return dbPlaces.get(0);
            }

            // 3. 로컬 DB에 없으면 TourAPI 단건 호출 및 위경도 값을 기반으로 DB 생성
            Place place = fetchAndSavePlaceFromApi(item);
            if (place != null) {
                return place;
            }
        }

        throw new GeneralException(CourseErrorStatus.PLACE_NOT_FOUND);
    }


    private Place fetchAndSavePlaceFromApi(CourseUpdateRequestDto.CoursePlaceItem item) {
        try {
            if (Boolean.TRUE.equals(item.getIsBarrierFree())) {
                tourApiService.getBarrierFreeInfo(Long.parseLong(item.getContentId()));
            }
            if (Boolean.TRUE.equals(item.getIsPetPlace())) {
                tourApiService.getPetDetailInfo(Long.parseLong(item.getContentId()));
            }

            Place newPlace = Place.builder()
                    .contentId(item.getContentId())
                    .petPlace(Boolean.TRUE.equals(item.getIsPetPlace()))
                    .barrierFree(Boolean.TRUE.equals(item.getIsBarrierFree()))
                    .mapx(parseDouble(item.getMapx()))
                    .mapy(parseDouble(item.getMapy()))
                    .build();

            return placeRepository.save(newPlace);
        } catch (Exception e) {
            return null;
        }
    }

    private CourseUpdateResponseDto buildUpdateResponse(Course course, List<CoursePlace> updatedPlaces) {
        updatedPlaces.sort(Comparator.comparing(CoursePlace::getSortOrder));

        List<CourseUpdateResponseDto.CoursePlaceResult> placeResults = updatedPlaces.stream()
                .map(cp -> CourseUpdateResponseDto.CoursePlaceResult.builder()
                        .coursePlaceId(cp.getId())
                        .placeId(cp.getPlace().getId())
                        .order(cp.getSortOrder().intValue())
                        .distance(cp.getDistance())
                        .timeTaken(cp.getTimeTaken())
                        .build())
                .collect(Collectors.toList());

        return CourseUpdateResponseDto.builder()
                .courseId(course.getId())
                .places(placeResults)
                .build();
    }

    @Transactional
    @Override
    public void deleteCourse(Long courseId, Long userId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new GeneralException(CourseErrorStatus.COURSE_NOT_FOUND));

        if (course.getUser() == null || !course.getUser().getId().equals(userId)) {
            throw new GeneralException(CourseErrorStatus.COURSE_NOT_WRITER);
        }

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

    private String toKilometers(int distanceMeters) {
        return String.format(Locale.US, "%.1f", distanceMeters / 1000.0);
    }

    private String toMinutes(int durationSeconds) {
        return String.valueOf((int) Math.ceil(durationSeconds / 60.0));
    }
}
