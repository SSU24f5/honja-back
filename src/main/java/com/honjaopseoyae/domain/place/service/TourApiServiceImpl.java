package com.honjaopseoyae.domain.place.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.honjaopseoyae.domain.place.client.TourApiClient;
import com.honjaopseoyae.domain.place.converter.TourPlaceConverter;
import com.honjaopseoyae.domain.place.dto.common.TourApiCommonResponse;
import com.honjaopseoyae.domain.place.dto.request.AreaBaseTourRequestDto;
import com.honjaopseoyae.domain.place.dto.request.PlaceDetailRequestDto;
import com.honjaopseoyae.domain.place.dto.response.DetailAccessibilityDto;
import com.honjaopseoyae.domain.place.dto.response.PetDetailResponseDto;
import com.honjaopseoyae.domain.place.dto.response.TourCommonResponseDto;
import com.honjaopseoyae.domain.place.dto.response.TourPlaceDto;
import com.honjaopseoyae.domain.place.entity.Place;
import com.honjaopseoyae.domain.place.repository.PlaceRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TourApiServiceImpl implements TourApiService {

	private static final String COMMON_PATH = "/KorService2/areaBasedList2";
	private static final String PET_PATH = "/KorPetTourService2/areaBasedList2";
	private static final String BARRIER_FREE_PATH = "/KorWithService2/areaBasedList2";
	private static final String DETAIL_PATH = "/KorWithService2/detailWithTour2";

	private final TourApiClient tourApiClient;
	private final PlaceRepository placeRepository;

	@Override
	public TourApiCommonResponse<List<DetailAccessibilityDto>> getBarrierFreeInfo(Long contentId) {
		PlaceDetailRequestDto requestDto = createDetailRequest(contentId);

		ParameterizedTypeReference<TourApiCommonResponse<List<DetailAccessibilityDto>>> typeRef =
			new ParameterizedTypeReference<>() {};

		return tourApiClient.getDetail(DETAIL_PATH, requestDto, typeRef);
	}

	@Override
	public TourApiCommonResponse<List<PetDetailResponseDto>> getPetDetailInfo(Long contentId) {
		PlaceDetailRequestDto requestDto = createDetailRequest(contentId);

		ParameterizedTypeReference<TourApiCommonResponse<List<PetDetailResponseDto>>> typeRef =
			new ParameterizedTypeReference<>() {};

		return tourApiClient.getDetail(DETAIL_PATH, requestDto, typeRef);
	}

	@Override
	public List<TourCommonResponseDto> getPetPlaceFromTourAPI() {
		AreaBaseTourRequestDto requestDto = AreaBaseTourRequestDto.builder()
			.numOfRows(400)
			.build();

		return fetchPlaces(PET_PATH, requestDto);
	}

	@Override
	public List<TourCommonResponseDto> getCommonPlaceFromTourAPI() {
		AreaBaseTourRequestDto requestDto = AreaBaseTourRequestDto.builder()
			.numOfRows(1130)
			.build();

		return fetchPlaces(COMMON_PATH, requestDto);
	}

	@Override
	public List<TourCommonResponseDto> getPetPlaceByCategoryFromTourAPI(String contentTypeId) {
		AreaBaseTourRequestDto requestDto = AreaBaseTourRequestDto.builder()
			.numOfRows(400)
			.contentTypeId(contentTypeId)
			.build();

		return fetchPlaces(PET_PATH, requestDto);
	}

	@Override
	public List<TourCommonResponseDto> getBarrierFreePlaceByCategoryFromTourAPI(String contentTypeId) {
		AreaBaseTourRequestDto requestDto = AreaBaseTourRequestDto.builder()
			.numOfRows(111)
			.lclsSystm1("NA")
			.contentTypeId(contentTypeId)
			.build();

		return fetchPlaces(BARRIER_FREE_PATH, requestDto);
	}

	@Override
	public List<TourCommonResponseDto> getCommonPlaceByCategoryFromTourAPI(String contentTypeId) {
		AreaBaseTourRequestDto requestDto = AreaBaseTourRequestDto.builder()
			.numOfRows(400)
			.contentTypeId(contentTypeId)
			.build();

		return fetchPlaces(COMMON_PATH, requestDto);
	}

	@Override
	public List<TourCommonResponseDto> getBarrierFreePlaceFromTourAPI() {
		AreaBaseTourRequestDto requestDto = AreaBaseTourRequestDto.builder()
			.numOfRows(111)
			.lclsSystm1("NA")
			.build();

		return fetchPlaces(BARRIER_FREE_PATH, requestDto);
	}

	@Transactional
	@Override
	public void syncTourPlacesWithApi() {
		log.info("=== 관광데이터 로컬 DB 동기화 배치 시작 ===");

		List<String> categories = List.of("NA", "A01", "C01", "EX", "HS", "");
		List<TourPlaceDto> apiTotalItems = new ArrayList<>();

		for (String category : categories) {
			AreaBaseTourRequestDto requestDto = AreaBaseTourRequestDto.builder()
				.numOfRows(500)
				.lclsSystm1(category)
				.build();

			TourApiCommonResponse<List<TourPlaceDto>> response =
				tourApiClient.getPlaces(BARRIER_FREE_PATH, requestDto);

			apiTotalItems.addAll(extractItems(response));
		}

		if (apiTotalItems.isEmpty()) {
			log.warn("API로부터 가져온 데이터가 없습니다. 배치를 종료합니다.");
			return;
		}

		log.info("API 수집 완료. 총 항목 수: {}개. 로컬 DB와 비교를 시작합니다.", apiTotalItems.size());

		List<String> apiContentIds = apiTotalItems.stream()
			.map(TourPlaceDto::getContentid)
			.filter(Objects::nonNull)
			.distinct()
			.toList();

		Map<String, Place> localPlaceMap = placeRepository.findAllByContentIdIn(apiContentIds).stream()
			.collect(Collectors.toMap(
				Place::getContentId,
				Function.identity(),
				(existing, replacement) -> existing
			));

		List<Place> saveList = new ArrayList<>();

		for (TourPlaceDto dto : apiTotalItems) {
			if (dto.getContentid() == null) {
				continue;
			}

			Place localPlace = localPlaceMap.get(dto.getContentid());

			if (localPlace == null) {
				saveList.add(dto.toEntity(false, true));
				continue;
			}

			updatePlaceIfChanged(localPlace, dto, saveList);
		}

		if (saveList.isEmpty()) {
			log.info("변경사항이 없습니다. 로컬 DB가 최신 상태입니다.");
			return;
		}

		placeRepository.saveAll(saveList);
		log.info("로컬 DB 동기화 완료! 추가/수정된 데이터 수: {}개", saveList.size());
	}

	private List<TourCommonResponseDto> fetchPlaces(String path, AreaBaseTourRequestDto requestDto) {
		TourApiCommonResponse<List<TourPlaceDto>> response =
			tourApiClient.getPlaces(path, requestDto);

		List<TourPlaceDto> items = extractItems(response);
		return TourPlaceConverter.toTourCommonResponseDtoList(items);
	}

	private List<TourPlaceDto> extractItems(TourApiCommonResponse<List<TourPlaceDto>> response) {
		if (response == null
			|| response.getResponse() == null
			|| response.getResponse().getBody() == null
			|| response.getResponse().getBody().getItems() == null
			|| response.getResponse().getBody().getItems().getItem() == null) {
			return List.of();
		}

		return response.getResponse().getBody().getItems().getItem();
	}

	private void updatePlaceIfChanged(Place place, TourPlaceDto dto, List<Place> saveList) {
		double mapx = parseDouble(dto.getMapx());
		double mapy = parseDouble(dto.getMapy());
		String image = dto.getFirstimage();
		Integer contentType = parseContentType(dto.getContenttypeid());

		boolean isPetPlace = place.isPetPlace();
		boolean isBarrierFree = true;

		if (!isPlaceChanged(place, mapx, mapy, image, contentType, isPetPlace, isBarrierFree)) {
			return;
		}

		place.update(mapx, mapy, image, isPetPlace, isBarrierFree, contentType);
		saveList.add(place);
	}

	private boolean isPlaceChanged(
		Place place,
		double mapx,
		double mapy,
		String image,
		Integer contentType,
		boolean isPetPlace,
		boolean isBarrierFree
	) {
		return Double.compare(place.getMapx(), mapx) != 0
			|| Double.compare(place.getMapy(), mapy) != 0
			|| !Objects.equals(place.getImage(), image)
			|| !Objects.equals(place.getContentType(), contentType)
			|| place.isPetPlace() != isPetPlace
			|| place.isBarrierFree() != isBarrierFree;
	}

	private PlaceDetailRequestDto createDetailRequest(Long contentId) {
		return PlaceDetailRequestDto.builder()
			.contentId(contentId.toString())
			.mobileApp("HonjaOpseoYae")
			.build();
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
}