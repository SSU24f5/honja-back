package com.honjaopseoyae.place.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.honjaopseoyae.domain.place.entity.Place;
import com.honjaopseoyae.place.dto.common.TourApiCommonResponse;
import com.honjaopseoyae.place.dto.request.AreaBaseTourRequestDto;
import com.honjaopseoyae.place.dto.request.PlaceDetailRequestDto;
import com.honjaopseoyae.place.dto.response.DetailAccessibilityDto;
import com.honjaopseoyae.place.dto.response.PetDetailResponseDto;
import com.honjaopseoyae.place.converter.TourPlaceConverter;
import com.honjaopseoyae.place.dto.response.TourCommonResponseDto;
import com.honjaopseoyae.place.dto.response.TourPlaceDto;
import com.honjaopseoyae.place.repository.PlaceRepository;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class TourApiServiceImpl implements TourApiService {
	private final WebClient webClient;
	private final ObjectMapper objectMapper;

	@Value("${tour-api.service-key}")
	private String serviceKey;
	private final PlaceRepository placeRepository;
	// 국문 관광정보 서비스 기본 URL
	private final String BASE_URL = "https://apis.data.go.kr/B551011";


	// 무장애 여행 장소 단건 조회 api
	@Override
	public TourApiCommonResponse<List<DetailAccessibilityDto>> getBarrierFreeInfo(Long contentId){
		ParameterizedTypeReference<TourApiCommonResponse<List<DetailAccessibilityDto>>> typeRef =
			new ParameterizedTypeReference<>() {};

		PlaceDetailRequestDto requestDto = PlaceDetailRequestDto.builder()
			.contentId(contentId.toString())
			.mobileApp("HonjaOpseoYae")
			.build();

		try {
			TourApiCommonResponse<List<DetailAccessibilityDto>> response = webClient.get()
				.uri(uriBuilder -> uriBuilder
					.path("/KorWithService2/detailWithTour2")
					.queryParam("serviceKey", serviceKey)
					.queryParam("numOfRows", requestDto.getNumOfRows())
					.queryParam("pageNo", requestDto.getPageNo())
					.queryParam("MobileOS", requestDto.getMobileOS())
					.queryParam("MobileApp", requestDto.getMobileApp())
					.queryParam("_type", requestDto.getType())
					.queryParam("contentId", requestDto.getContentId())
					.build())
				.retrieve()
				.bodyToMono(typeRef)
				.block();

			log.info("TourAPI 상세 요청 성공 - contentId: {}", contentId);
			return response;

		} catch (Exception e) {
			log.error("TourAPI 상세 요청 중 통신/파싱 에러 발생 (contentId: {}) : ", contentId, e);
			return new TourApiCommonResponse<>();
		}
	}

	@Override
	public TourApiCommonResponse<List<PetDetailResponseDto>> getPetDetailInfo(Long contentId) {
		ParameterizedTypeReference<TourApiCommonResponse<List<PetDetailResponseDto>>> typeRef =
			new ParameterizedTypeReference<>() {};

		PlaceDetailRequestDto requestDto = PlaceDetailRequestDto.builder()
			.contentId(contentId.toString())
			.mobileApp("HonjaOpseoYae")
			.build();

		try {
			TourApiCommonResponse<List<PetDetailResponseDto>> response = webClient.get()
				.uri(uriBuilder -> uriBuilder
					.path("/KorWithService2/detailWithTour2") // 필요시 해당 API의 엔드포인트 경로로 수정하세요
					.queryParam("serviceKey", serviceKey)
					.queryParam("numOfRows", requestDto.getNumOfRows())
					.queryParam("pageNo", requestDto.getPageNo())
					.queryParam("MobileOS", requestDto.getMobileOS())
					.queryParam("MobileApp", requestDto.getMobileApp())
					.queryParam("_type", requestDto.getType())
					.queryParam("contentId", requestDto.getContentId())
					.build())
				.retrieve()
				.bodyToMono(typeRef)
				.block();

			log.info("TourAPI 반려동물 상세 요청 성공 - contentId: {}", contentId);
			return response;

		} catch (Exception e) {
			log.error("TourAPI 반려동물 상세 요청 중 통신/파싱 에러 발생 (contentId: {}) : ", contentId, e);
			return new TourApiCommonResponse<>();
		}
	}

	//  반려동물 전체 조회 - (다른점은 IcIsystm1 이 필수인지 아닌지)
	@Override
	public List<TourCommonResponseDto> getPetPlaceFromTourAPI() {
		ParameterizedTypeReference<TourApiCommonResponse<List<TourPlaceDto>>> typeRef =
			new ParameterizedTypeReference<>() {};

		AreaBaseTourRequestDto requestDto = AreaBaseTourRequestDto.builder()
			.numOfRows(400)
			.build();
		try {
			TourApiCommonResponse<List<TourPlaceDto>> response = webClient.get()
				.uri(uriBuilder -> uriBuilder
					.path("/KorPetTourService2/areaBasedList2")
					.queryParam("serviceKey", serviceKey)
					.queryParam("numOfRows", requestDto.getNumOfRows())
					.queryParam("pageNo", requestDto.getPageNo())
					.queryParam("MobileOS", requestDto.getMobileOS())
					.queryParam("MobileApp", requestDto.getMobileApp())
					.queryParam("_type", requestDto.get_type())
					.queryParam("LDongRegnCd", requestDto.getLDongRegnCd())
					.queryParam("lclsSystm1", requestDto.getLclsSystm1())
					.build())
				.retrieve()
				.bodyToMono(typeRef)
				.block();

			log.info("TourAPI 요청 성공 - 총 개수: {}",
				(response != null && response.getResponse().getBody() != null)
					? response.getResponse().getBody().getTotalCount() : 0);

			if (response != null && response.getResponse() != null
				&& response.getResponse().getBody() != null
				&& response.getResponse().getBody().getItems() != null) {
				List<TourPlaceDto> items = response.getResponse().getBody().getItems().getItem();
				return TourPlaceConverter.toTourCommonResponseDtoList(items);
			}
			return new ArrayList<>();

		} catch (Exception e) {
			log.error("TourAPI 외부 요청 중 통신/파싱 에러 발생 : ", e);
			return new ArrayList<>();
		}
	}


	// 이거는 그냥 조회 잘 되는지 확인 여부차 만들어놓은거기도 하고 ...
	@Override
	public List<TourCommonResponseDto> getBarrierFreePlaceFromTourAPI() {
		ParameterizedTypeReference<TourApiCommonResponse<List<TourPlaceDto>>> typeRef =
			new ParameterizedTypeReference<>() {};

		AreaBaseTourRequestDto requestDto = AreaBaseTourRequestDto.builder()
			.numOfRows(111)
			.lclsSystm1("NA")
			.build();
		try {
			TourApiCommonResponse<List<TourPlaceDto>> response = webClient.get()
				.uri(uriBuilder -> uriBuilder
					.path("/KorWithService2/areaBasedList2")
					.queryParam("serviceKey", serviceKey)
					.queryParam("numOfRows", requestDto.getNumOfRows())
					.queryParam("pageNo", requestDto.getPageNo())
					.queryParam("MobileOS", requestDto.getMobileOS())
					.queryParam("MobileApp", requestDto.getMobileApp())
					.queryParam("_type", requestDto.get_type())
					.queryParam("LDongRegnCd", requestDto.getLDongRegnCd())
					.queryParam("lclsSystm1", requestDto.getLclsSystm1())
					.build())
				.retrieve()
				.bodyToMono(typeRef)
				.block();

			log.info("TourAPI 요청 성공 - 총 개수: {}",
				(response != null && response.getResponse().getBody() != null)
					? response.getResponse().getBody().getTotalCount() : 0);

			if (response != null && response.getResponse() != null
				&& response.getResponse().getBody() != null
				&& response.getResponse().getBody().getItems() != null) {
				List<TourPlaceDto> items = response.getResponse().getBody().getItems().getItem();
				return TourPlaceConverter.toTourCommonResponseDtoList(items);
			}
			return new ArrayList<>();

		} catch (Exception e) {
			log.error("TourAPI 외부 요청 중 통신/파싱 에러 발생 : ", e);
			return new ArrayList<>();
		}
	}

	@Transactional
	@Override
	public void syncTourPlacesWithApi() {
		log.info("=== 관광데이터 로컬 DB 동기화 배치 시작 ===");

		List<String> categories = List.of("NA", "A01", "C01", "EX", "HS", "");
		List<TourPlaceDto> apiTotalItems = new ArrayList<>(); // 독립된 알맹이 DTO로 타입 변경

		for (String category : categories) {
			TourApiCommonResponse<List<TourPlaceDto>> response = fetchFromTourAPI(category);

			if (response != null && response.getResponse() != null
				&& response.getResponse().getBody() != null
				&& response.getResponse().getBody().getItems() != null) {

				// 캐스팅 경고 방지를 곁들여 데이터 추출
				List<TourPlaceDto> items = (List<TourPlaceDto>) response.getResponse().getBody().getItems().getItem();
				if (items != null) {
					apiTotalItems.addAll(items);
				}
			}
		}

		if (apiTotalItems.isEmpty()) {
			log.warn("API로부터 가져온 데이터가 없습니다. 배치를 종료합니다.");
			return;
		}

		log.info("API 수집 완료. 총 항목 수: {}개. 로컬 DB와 비교를 시작합니다.", apiTotalItems.size());

		List<String> apiContentIds = apiTotalItems.stream()
			.map(TourPlaceDto::getContentid)
			.filter(Objects::nonNull)
			.collect(Collectors.toList());

		Map<String, Place> localPlaceMap = placeRepository.findAllByContentIdIn(apiContentIds).stream()
			.collect(Collectors.toMap(Place::getContentId, Function.identity(), (existing, replacement) -> existing));

		List<Place> saveList = new ArrayList<>();

		for (TourPlaceDto dto : apiTotalItems) {
			if (dto.getContentid() == null) continue;

			Place localPlace = localPlaceMap.get(dto.getContentid());

			double apiMapx = parseDouble(dto.getMapx());
			double apiMapy = parseDouble(dto.getMapy());
			String apiImage = dto.getFirstimage();
			Integer apiContentType = parseContentType(dto.getContenttypeid());
			
			boolean isBarrierFree = true;
			boolean isPetPlace = (localPlace != null) && localPlace.isPetPlace();

			if (localPlace == null) {
				Place newPlace = convertToPlace(dto, isPetPlace, isBarrierFree);
				saveList.add(newPlace);
			} else {
				// 비교 로직: 기존 값과 달라진 경우에만 업데이트 수행
				if (localPlace.getMapx() != apiMapx ||
					localPlace.getMapy() != apiMapy ||
					!Objects.equals(localPlace.getImage(), apiImage) ||
					!Objects.equals(localPlace.getContentType(), apiContentType) ||
					localPlace.isBarrierFree() != isBarrierFree ||
					localPlace.isPetPlace() != isPetPlace) {
					
					localPlace.update(apiMapx, apiMapy, apiImage, isPetPlace, isBarrierFree, apiContentType);
					saveList.add(localPlace);
				}
			}
		}

		if (!saveList.isEmpty()) {
			placeRepository.saveAll(saveList);
			log.info("로컬 DB 동기화 완료! 추가/수정된 데이터 수: {}개", saveList.size());
		} else {
			log.info("변경사항이 없습니다. 로컬 DB가 최신 상태입니다.");
		}
	}


	private TourApiCommonResponse<List<TourPlaceDto>> fetchFromTourAPI(String category) {
		ParameterizedTypeReference<TourApiCommonResponse<List<TourPlaceDto>>> typeRef =
			new ParameterizedTypeReference<>() {};

		AreaBaseTourRequestDto requestDto = AreaBaseTourRequestDto.builder()
			.numOfRows(500)
			.lclsSystm1(category)
			.build();
		try {
			return webClient.get()
				.uri(uriBuilder -> uriBuilder
					.path("/KorWithService2/areaBasedList2")
					.queryParam("serviceKey", serviceKey)
					.queryParam("numOfRows", requestDto.getNumOfRows())
					.queryParam("pageNo", requestDto.getPageNo())
					.queryParam("MobileOS", requestDto.getMobileOS())
					.queryParam("MobileApp", requestDto.getMobileApp())
					.queryParam("_type", requestDto.get_type())
					.queryParam("LDongRegnCd", requestDto.getLDongRegnCd())
					.queryParam("lclsSystm1", requestDto.getLclsSystm1())
					.build())
				.retrieve()
				.bodyToMono(typeRef)
				.block();
		} catch (Exception e) {
			log.error("TourAPI 외부 요청 중 에러 발생 (Category: {}) : ", category, e);
			return null;
		}
	}

	private Place convertToPlace(TourPlaceDto dto, boolean isPetPlace, boolean isBarrierFree) {
		return Place.builder()
			.contentId(dto.getContentid())
			.contentType(parseContentType(dto.getContenttypeid()))
			.mapx(parseDouble(dto.getMapx()))
			.mapy(parseDouble(dto.getMapy()))
			.image(dto.getFirstimage())
			.petPlace(isPetPlace)
			.barrierFree(isBarrierFree)
			.build();
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
}
