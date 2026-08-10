package com.honjaopseoyae.domain.course.service;

import java.util.List;

import com.honjaopseoyae.global.apipayload.exception.GeneralException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.honjaopseoyae.config.KakaoMobilityClient;
import com.honjaopseoyae.domain.course.dto.response.KakaoDirectionsResponseDto;
import com.honjaopseoyae.domain.course.dto.response.RouteResponseDto;
import com.honjaopseoyae.domain.course.entity.mapping.CoursePlace;
import com.honjaopseoyae.domain.course.repository.CoursePlaceRepository;
import com.honjaopseoyae.domain.place.entity.Place;
import com.honjaopseoyae.global.apipayload.domain.CourseErrorStatus;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RouteService {

    private final CoursePlaceRepository coursePlaceRepository;
    private final KakaoMobilityClient kakaoMobilityClient;

    public RouteResponseDto getRoute(Long courseId, Long originPlaceId,
                                     Long destinationPlaceId, String priority) {

        if (originPlaceId.equals(destinationPlaceId)) {
            throw new GeneralException(CourseErrorStatus.SAME_ORIGIN_DESTINATION);
        }

        List<CoursePlace> coursePlaces = coursePlaceRepository.findAllByCourseId(courseId);
        Place origin = findPlaceIn(coursePlaces, originPlaceId);
        Place destination = findPlaceIn(coursePlaces, destinationPlaceId);

        KakaoDirectionsResponseDto response = kakaoMobilityClient.getDirectionsWithPath(
                origin.getLng(), origin.getLat(),
                destination.getLng(), destination.getLat(),
                priority
        );

        if (response == null || response.getRoutes() == null || response.getRoutes().isEmpty()) {
            throw new GeneralException(CourseErrorStatus.ROUTE_NOT_FOUND);
        }

        KakaoDirectionsResponseDto.Route route = response.getRoutes().get(0);
        if (route.getResultCode() != 0) {
            throw new GeneralException(CourseErrorStatus.ROUTE_NOT_FOUND);
        }

        return RouteResponseDto.of(originPlaceId, destinationPlaceId, route);
    }

    private Place findPlaceIn(List<CoursePlace> coursePlaces, Long placeId) {
        return coursePlaces.stream()
                .map(CoursePlace::getPlace)
                .filter(p -> p.getId().equals(placeId))
                .findFirst()
                .orElseThrow(() -> new GeneralException(CourseErrorStatus.INVALID_COURSE_PLACE));
    }
}