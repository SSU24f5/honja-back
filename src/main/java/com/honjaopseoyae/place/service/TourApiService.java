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
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.reactive.function.client.WebClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.honjaopseoyae.domain.place.entity.TourPlace;
import com.honjaopseoyae.place.dto.common.TourApiCommonResponse;
import com.honjaopseoyae.place.dto.request.BarrierFreeDetailRequestDto;
import com.honjaopseoyae.place.dto.request.WithAreaBaseTourRequestDto;
import com.honjaopseoyae.place.dto.response.DetailAccessibilityDto;
import com.honjaopseoyae.place.dto.response.TourCommonResponseDto;
import com.honjaopseoyae.place.dto.response.TourPlaceDto;
import com.honjaopseoyae.place.repository.TourApiRepository;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class TourApiService {
	private final WebClient webClient;
	private final ObjectMapper objectMapper;

	@Value("${tour-api.service-key}")
	private String serviceKey;
	private final TourApiRepository tourApiRepository;
	// 국문 관광정보 서비스 기본 URL
	private final String BASE_URL = "https://apis.data.go.kr/B551011";


	public TourApiCommonResponse<List<DetailAccessibilityDto>> getBarrierFreeInfo(Long contentId){
		ParameterizedTypeReference<TourApiCommonResponse<List<DetailAccessibilityDto>>> typeRef =
			new ParameterizedTypeReference<>() {};

		BarrierFreeDetailRequestDto requestDto = BarrierFreeDetailRequestDto.builder()
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


	// 이거는 그냥 조회 잘 되는지 확인 여부차 만들어놓은거기도 하고 ...
	public TourApiCommonResponse<List<TourPlaceDto>> getBarrierFreePlaceFromTourAPI() {
		ParameterizedTypeReference<TourApiCommonResponse<List<TourPlaceDto>>> typeRef =
			new ParameterizedTypeReference<>() {};

		WithAreaBaseTourRequestDto requestDto = WithAreaBaseTourRequestDto.builder()
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
			return response;

		} catch (Exception e) {
			log.error("TourAPI 외부 요청 중 통신/파싱 에러 발생 : ", e);
			return new TourApiCommonResponse<>();
		}
	}

	@Transactional
	public void syncTourPlacesWithApi() {
		log.info("=== 관광데이터 로컬 DB 동기화 배치 시작 ===");

		List<String> categories = List.of("NA", "A01", "C01", "EX", "HS", "");
		List<TourPlaceDto> apiTotalItems = new ArrayList<>(); // 독맆된 알맹이 DTO로 타입 변경

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

		Map<String, TourPlace> localPlaceMap = tourApiRepository.findAllByContentIdIn(apiContentIds).stream()
			.collect(Collectors.toMap(TourPlace::getContentId, Function.identity()));

		List<TourPlace> saveList = new ArrayList<>();

		for (TourPlaceDto dto : apiTotalItems) {
			if (dto.getContentid() == null) continue;

			TourPlace localPlace = localPlaceMap.get(dto.getContentid());

			if (localPlace == null) {
				TourPlace newPlace = convertToEntity(dto);
				saveList.add(newPlace);
			} else {
				if (!Objects.equals(localPlace.getModifiedTime(), dto.getModifiedtime())) {
					TourPlace updatedPlace = convertToEntity(dto);
					saveList.add(updatedPlace);
				}
			}
		}

		if (!saveList.isEmpty()) {
			tourApiRepository.saveAll(saveList);
			log.info("로컬 DB 동기화 완료! 추가/수정된 데이터 수: {}개", saveList.size());
		} else {
			log.info("변경사항이 없습니다. 로컬 DB가 최신 상태입니다.");
		}
	}

	/**
	 * 카테고리별 공통 껍데기 반환 메서드 수정
	 */
	private TourApiCommonResponse<List<TourPlaceDto>> fetchFromTourAPI(String category) {
		ParameterizedTypeReference<TourApiCommonResponse<List<TourPlaceDto>>> typeRef =
			new ParameterizedTypeReference<>() {};

		WithAreaBaseTourRequestDto requestDto = WithAreaBaseTourRequestDto.builder()
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

	/**
	 * 변환 매퍼 파라미터 타입 수정
	 */
	private TourPlace convertToEntity(TourPlaceDto dto) {
		return TourPlace.builder()
			.contentId(dto.getContentid())
			.contentTypeId(dto.getContenttypeid())
			.title(dto.getTitle())
			.createdTime(dto.getCreatedtime())
			.modifiedTime(dto.getModifiedtime())
			.tel(dto.getTel())
			.zipcode(dto.getZipcode())
			.addr1(dto.getAddr1())
			.addr2(dto.getAddr2())
			.areaCode(dto.getAreacode())
			.sigunguCode(dto.getSigungucode())
			.cat1(dto.getCat1())
			.cat2(dto.getCat2())
			.cat3(dto.getCat3())
			.mapX(dto.getMapx())
			.mapY(dto.getMapy())
			.mLevel(dto.getMlevel())
			.firstImage(dto.getFirstimage())
			.firstImage2(dto.getFirstimage2())
			.cpyrhtDivCd(dto.getCpyrhtDivCd())
			.lDongRegnCd(dto.getLDongRegnCd())
			.lDongSignguCd(dto.getLDongSignguCd())
			.lclsSystm1(dto.getLclsSystm1())
			.lclsSystm2(dto.getLclsSystm2())
			.lclsSystm3(dto.getLclsSystm3())
			.build();
	}
}
