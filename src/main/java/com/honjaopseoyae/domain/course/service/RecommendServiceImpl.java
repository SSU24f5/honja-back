package com.honjaopseoyae.domain.course.service;

import com.honjaopseoyae.config.KakaoMobilityClient;
import com.honjaopseoyae.config.KakaoMobilityClient.RouteSummary;
import com.honjaopseoyae.domain.course.algorithm.DistanceCalculator;
import com.honjaopseoyae.domain.course.algorithm.RouteRecommendEngine;
import com.honjaopseoyae.domain.course.algorithm.PlaceCategory;
import com.honjaopseoyae.domain.course.dto.request.BetweenPlacesRecommendRequestDto;
import com.honjaopseoyae.domain.course.dto.request.CourseOrderRecommendRequestDto;
import com.honjaopseoyae.domain.course.dto.response.CourseOrderRecommendResponseDto;
import com.honjaopseoyae.domain.course.dto.response.CourseOrderRecommendResponseDto.CoursePlaceItem;
import com.honjaopseoyae.domain.course.entity.Course;
import com.honjaopseoyae.domain.course.entity.enums.CourseRole;
import com.honjaopseoyae.domain.course.entity.enums.InviteStatus;
import com.honjaopseoyae.domain.course.entity.mapping.CourseMember;
import com.honjaopseoyae.domain.course.entity.mapping.CoursePlace;
import com.honjaopseoyae.domain.course.repository.CourseMemberRepository;
import com.honjaopseoyae.domain.course.repository.CoursePlaceRepository;
import com.honjaopseoyae.domain.course.repository.CourseRepository;
import com.honjaopseoyae.domain.course.support.CourseFinder;
import com.honjaopseoyae.domain.place.entity.Place;
import com.honjaopseoyae.domain.place.repository.PlaceRepository;
import com.honjaopseoyae.domain.place.client.TourApiClient;
import com.honjaopseoyae.domain.place.dto.response.TourCommonResponseDto;
import com.honjaopseoyae.domain.place.dto.response.TourPlaceDto;
import com.honjaopseoyae.domain.place.dto.request.LocationBasedRequestDto;
import com.honjaopseoyae.domain.place.dto.common.TourApiCommonResponse;
import com.honjaopseoyae.domain.place.converter.TourPlaceConverter;
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
	private final CourseMemberRepository courseMemberRepository;
	private final CoursePlaceRepository coursePlaceRepository;
	private final RouteRecommendEngine routeRecommendEngine;
	private final KakaoMobilityClient kakaoMobilityClient;
	private final PlaceRepository placeRepository;
	private final TourApiClient tourApiClient;

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
		courseMemberRepository
			.findByCourseIdAndUserIdAndRoleAndStatus(
				course.getId(),
				userId,
				CourseRole.OWNER,
				InviteStatus.ACCEPTED
			)
			.orElseThrow(() -> new GeneralException(UserErrorStatus.INVALID_USER));
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
				.title(place.getTitle())
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

	@Transactional(readOnly = true)
	@Override
	public List<TourCommonResponseDto> recommendBetween(BetweenPlacesRecommendRequestDto request, String type) {
		CoursePlace startCoursePlace = coursePlaceRepository.findById(request.getStartCoursePlaceId())
			.orElseThrow(() -> new GeneralException(CourseErrorStatus.COURSE_PLACE_NOT_FOUND));
		CoursePlace endCoursePlace = coursePlaceRepository.findById(request.getEndCoursePlaceId())
			.orElseThrow(() -> new GeneralException(CourseErrorStatus.COURSE_PLACE_NOT_FOUND));

		Place startPlace = startCoursePlace.getPlace();
		Place endPlace = endCoursePlace.getPlace();
		if (startPlace == null || endPlace == null) {
			throw new GeneralException(CourseErrorStatus.PLACE_NOT_FOUND);
		}

		double midx = (startPlace.getMapx() + endPlace.getMapx()) / 2.0;
		double midy = (startPlace.getMapy() + endPlace.getMapy()) / 2.0;

		double distanceBetween = DistanceCalculator.calculate(
			startPlace.getMapy(), startPlace.getMapx(), endPlace.getMapy(), endPlace.getMapx());

		String path = switch (type) {
			case "barrier-free" -> "/KorWithService2/locationBasedList2";
			case "pet-friendly" -> "/KorPetTourService2/locationBasedList2";
			default -> "/KorService2/locationBasedList2";
		};

		int currentRadius = (int) Math.min(20000.0, Math.max(1000.0, distanceBetween / 2.0));
		List<TourPlaceDto> items = new ArrayList<>();
		int attemptRadius = currentRadius;
		int lastRadius = 0;

		for (int attempt = 1; attempt <= 3; attempt++) {
			if (attemptRadius == lastRadius) {
				break;
			}
			lastRadius = attemptRadius;

			LocationBasedRequestDto requestDto = LocationBasedRequestDto.builder()
				.mapx(String.valueOf(midx))
				.mapy(String.valueOf(midy))
				.radius(String.valueOf(attemptRadius))
				.numOfRows(200)
				.build();

			TourApiCommonResponse<List<TourPlaceDto>> apiResponse = tourApiClient.getLocationPlaces(path, requestDto);
			items = extractTourItems(apiResponse);

			if (items != null && !items.isEmpty()) {
				break;
			}

			// Increase radius for next attempt
			attemptRadius = (int) Math.min(20000.0, attemptRadius * 1.5);
		}

		if (items == null || items.isEmpty()) {
			return new ArrayList<>();
		}

		PlaceCategory startCat = PlaceCategory.from(startPlace);
		PlaceCategory endCat = PlaceCategory.from(endPlace);

		List<ScoredPlace> scoredPlaces = new ArrayList<>();

		// Max detour: distanceBetween or at least 3km (to allow decent options for close places)
		double maxDetour = Math.max(distanceBetween * 1.0, 3000.0);
		double maxAllowedDist = Math.max(distanceBetween * 1.5, 4000.0);

		for (TourPlaceDto item : items) {
			if (item.getContentid() == null) {
				continue;
			}

			// Skip start and end places
			if (item.getContentid().equals(startPlace.getContentId()) || item.getContentid().equals(endPlace.getContentId())) {
				continue;
			}

			double itemX = parseDouble(item.getMapx());
			double itemY = parseDouble(item.getMapy());
			if (itemX == 0.0 || itemY == 0.0) {
				continue;
			}

			double distSP = DistanceCalculator.calculate(startPlace.getMapy(), startPlace.getMapx(), itemY, itemX);
			double distPE = DistanceCalculator.calculate(itemY, itemX, endPlace.getMapy(), endPlace.getMapx());
			double detour = distSP + distPE - distanceBetween;

			if (detour > maxDetour || distSP > maxAllowedDist || distPE > maxAllowedDist) {
				continue;
			}

			Integer candContentType = parseContentType(item.getContenttypeid());
			Place tempPlace = Place.builder()
				.contentType(candContentType)
				.cat3(item.getCat3())
				.build();
			PlaceCategory candCat = PlaceCategory.from(tempPlace);

			// 숙박(HOTEL) 카테고리는 중간 추천에서 완전히 배제
			if (candCat == PlaceCategory.HOTEL) {
				continue;
			}

			double categoryScore = 0.0;

			// 1. Base Category Preference
			switch (candCat) {
				case TOUR -> categoryScore += 3.0;
				case CAFE -> categoryScore += 3.0;
				case RESTAURANT -> categoryScore += 2.0;
				case SHOPPING -> categoryScore += 2.0;
				case ETC -> categoryScore += 1.0;
				default -> {}
			}

			// 2. Diversity Penalty
			if (candCat == startCat) {
				categoryScore -= 3.0;
			}
			if (candCat == endCat) {
				categoryScore -= 3.0;
			}

			// 3. Flow/Synergy Bonus (활동 vs 음식점/카페 매칭)
			boolean startIsFood = (startCat == PlaceCategory.RESTAURANT || startCat == PlaceCategory.CAFE);
			boolean endIsFood = (endCat == PlaceCategory.RESTAURANT || endCat == PlaceCategory.CAFE);
			boolean startIsActivity = (startCat == PlaceCategory.TOUR || startCat == PlaceCategory.SHOPPING);
			boolean endIsActivity = (endCat == PlaceCategory.TOUR || endCat == PlaceCategory.SHOPPING);

			if (startIsFood && endIsFood) {
				if (candCat == PlaceCategory.TOUR || candCat == PlaceCategory.SHOPPING) {
					categoryScore += 3.0;
				}
			} else if (startIsActivity && endIsActivity) {
				if (candCat == PlaceCategory.CAFE || candCat == PlaceCategory.RESTAURANT) {
					categoryScore += 3.0;
				}
			} else if ((startIsActivity && endIsFood) || (startIsFood && endIsActivity)) {
				if (candCat == PlaceCategory.TOUR || candCat == PlaceCategory.CAFE) {
					categoryScore += 1.5;
				}
			}

			double detourKm = detour / 1000.0;
			double finalScore = categoryScore - detourKm;

			scoredPlaces.add(new ScoredPlace(item, finalScore));
		}

		scoredPlaces.sort((a, b) -> Double.compare(b.score(), a.score()));

		List<TourPlaceDto> recommendedDtos = scoredPlaces.stream()
			.limit(3)
			.map(ScoredPlace::dto)
			.toList();

		return TourPlaceConverter.toTourCommonResponseDtoList(recommendedDtos);
	}

	private List<TourPlaceDto> extractTourItems(TourApiCommonResponse<List<TourPlaceDto>> response) {
		if (response == null
			|| response.getResponse() == null
			|| response.getResponse().getBody() == null
			|| response.getResponse().getBody().getItems() == null
			|| response.getResponse().getBody().getItems().getItem() == null) {
			return List.of();
		}
		return response.getResponse().getBody().getItems().getItem();
	}

	private double parseDouble(String value) {
		if (value == null || value.isBlank()) {
			return 0.0;
		}
		try {
			return Double.parseDouble(value.trim());
		} catch (NumberFormatException e) {
			return 0.0;
		}
	}

	private Integer parseContentType(String contentTypeId) {
		if (contentTypeId == null || contentTypeId.isBlank()) {
			return null;
		}
		try {
			return Integer.parseInt(contentTypeId.trim());
		} catch (NumberFormatException e) {
			return null;
		}
	}

	private record ScoredPlace(TourPlaceDto dto, double score) {
	}
}
