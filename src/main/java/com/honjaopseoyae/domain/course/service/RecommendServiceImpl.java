package com.honjaopseoyae.domain.course.service;

import com.honjaopseoyae.config.KakaoMobilityClient;
import com.honjaopseoyae.config.KakaoMobilityClient.RouteSummary;
import com.honjaopseoyae.domain.course.algorithm.DistanceCalculator;
import com.honjaopseoyae.domain.course.algorithm.RouteRecommendEngine;
import com.honjaopseoyae.domain.course.dto.request.CourseOrderRecommendRequestDto;
import com.honjaopseoyae.domain.course.dto.response.CourseOrderRecommendResponseDto;
import com.honjaopseoyae.domain.course.dto.response.CourseOrderRecommendResponseDto.CoursePlaceItem;
import com.honjaopseoyae.domain.course.entity.Course;
import com.honjaopseoyae.domain.course.entity.mapping.CoursePlace;
import com.honjaopseoyae.domain.course.repository.CoursePlaceRepository;
import com.honjaopseoyae.domain.course.repository.CourseRepository;
import com.honjaopseoyae.domain.course.support.CourseFinder;
import com.honjaopseoyae.domain.place.entity.Place;
import com.honjaopseoyae.global.apipayload.domain.CourseErrorStatus;
import com.honjaopseoyae.global.apipayload.domain.UserErrorStatus;
import com.honjaopseoyae.global.apipayload.exception.GeneralException;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RecommendServiceImpl implements RecommendService {

	private static final double WALK_SPEED_KMH = 4.0;
	private final CourseFinder courseFinder;
	private final CourseRepository courseRepository;
	private final CoursePlaceRepository coursePlaceRepository;
	private final RouteRecommendEngine routeRecommendEngine;
	private final KakaoMobilityClient kakaoMobilityClient;

	// 조회 전용. 엔티티 변경 메서드를 호출하지 않으므로 readOnly로 명시해 실수로도 flush 안 되게 막음
	@Transactional(readOnly = true)
	public CourseOrderRecommendResponseDto recommend(Long userId, CourseOrderRecommendRequestDto request) {

		Course course = courseFinder.findById(request.getCourseId());

		validateOwner(course, userId);

		List<CoursePlace> coursePlaces = coursePlaceRepository.findByCourseIdAndDate(request.getCourseId(), request.getDate());

		if (coursePlaces.isEmpty()) {
			throw new GeneralException(CourseErrorStatus.COURSE_NOT_FOUND);
		}

		// 1) 순서 탐색 (Haversine 기반, 엔티티 변경 없음)
		List<CoursePlace> recommendedRoute = routeRecommendEngine.recommend(coursePlaces);

		// 2) 인접 구간 거리/시간 계산 (카카오 API, 로컬 변수로만 보관)
		Map<Integer, RouteSegment> segmentByIndex = calculateSegments(recommendedRoute);

		// 3) 계산된 값만으로 DTO 조립 (엔티티는 전혀 건드리지 않음)
		List<CoursePlaceItem> items = buildItems(recommendedRoute, segmentByIndex);

		return CourseOrderRecommendResponseDto.of(request.getCourseId(), request.getDate(), items);
	}

	private void validateOwner(Course course, Long userId) {
		if (!course.getUser().getId().equals(userId)) {
			throw new GeneralException(UserErrorStatus.INVALID_USER);
		}
	}

	private Map<Integer, RouteSegment> calculateSegments(List<CoursePlace> orderedPlaces) {

		Map<Integer, RouteSegment> result = new HashMap<>();
		result.put(0, new RouteSegment("0", "0"));

		if (orderedPlaces.size() < 2) {
			return result;
		}

		List<Mono<IndexedSummary>> calls = new ArrayList<>();

		for (int i = 1; i < orderedPlaces.size(); i++) {
			Place from = orderedPlaces.get(i - 1).getPlace();
			Place to = orderedPlaces.get(i).getPlace();
			int index = i;

			Mono<IndexedSummary> call = kakaoMobilityClient
				.getRouteSummaryAsync(from.getMapx(), from.getMapy(), to.getMapx(), to.getMapy())
				.map(summary -> new IndexedSummary(index, summary))
				.defaultIfEmpty(new IndexedSummary(index, null));

			calls.add(call);
		}

		List<IndexedSummary> results = Flux.merge(calls).collectList().block();

		for (IndexedSummary result1 : results) {
			int i = result1.index();
			RouteSummary summary = result1.summary();

			if (summary != null) {
				result.put(i, new RouteSegment(
					formatDistance(summary.distanceMeters()),
					formatDuration(summary.durationSeconds())));
			} else {
				Place prev = orderedPlaces.get(i - 1).getPlace();
				Place curr = orderedPlaces.get(i).getPlace();
				double meters = DistanceCalculator.calculate(
					prev.getMapy(), prev.getMapx(), curr.getMapy(), curr.getMapx());
				result.put(i, new RouteSegment(formatDistance(meters), formatFallbackTime(meters)));
			}
		}

		return result;
	}

	private List<CoursePlaceItem> buildItems(List<CoursePlace> orderedPlaces, Map<Integer, RouteSegment> segments) {

		List<CoursePlaceItem> items = new ArrayList<>();

		for (int i = 0; i < orderedPlaces.size(); i++) {
			CoursePlace coursePlace = orderedPlaces.get(i);
			Place place = coursePlace.getPlace();
			RouteSegment segment = segments.get(i);

			items.add(CoursePlaceItem.builder()
				.coursePlaceId(coursePlace.getId())
				.placeId(place.getId())
				.order(i)
				.orderType(coursePlace.getOrderType())
				.distance(segment.distance())
				.timeTaken(segment.timeTaken())
				.contentId(place.getContentId())
				.mapx(place.getMapx())
				.mapy(place.getMapy())
				.petPlace(place.isPetPlace())
				.barrierFree(place.isBarrierFree())
				.build());
		}

		return items;
	}

	private String formatDistance(double meters) {
		if (meters < 1000) {
			return Math.round(meters) + "m";
		}
		return String.format("%.1fkm", meters / 1000);
	}

	private String formatDuration(int seconds) {
		long minutes = Math.max(1, Math.round(seconds / 60.0));
		return minutes + "분";
	}

	private String formatFallbackTime(double meters) {
		double hours = (meters / 1000) / WALK_SPEED_KMH;
		long minutes = Math.max(1, Math.round(hours * 60));
		return minutes + "분";
	}

	private record RouteSegment(String distance, String timeTaken) {
	}

	private record IndexedSummary(int index, RouteSummary summary) {
	}
}