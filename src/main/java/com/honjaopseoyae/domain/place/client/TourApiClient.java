package com.honjaopseoyae.domain.place.client;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.honjaopseoyae.domain.place.dto.common.TourApiCommonResponse;
import com.honjaopseoyae.domain.place.dto.request.AreaBaseTourRequestDto;
import com.honjaopseoyae.domain.place.dto.request.LocationBasedRequestDto;
import com.honjaopseoyae.domain.place.dto.request.PlaceDetailRequestDto;
import com.honjaopseoyae.domain.place.dto.response.TourPlaceDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class TourApiClient {

	private final WebClient tourApiWebClient;

	@Value("${tour-api.service-key}")
	private String serviceKey;

	private static final ParameterizedTypeReference<
		TourApiCommonResponse<List<TourPlaceDto>>
		> TOUR_PLACE_TYPE = new ParameterizedTypeReference<>() {};


	public TourApiCommonResponse<List<TourPlaceDto>> getPlaces(
		String path,
		AreaBaseTourRequestDto request
	) {
		try {
			return tourApiWebClient.get()
				.uri(uriBuilder -> {

					var builder = uriBuilder
						.path(path)
						.queryParam("serviceKey", serviceKey)
						.queryParam("numOfRows", request.getNumOfRows())
						.queryParam("pageNo", request.getPageNo())
						.queryParam("MobileOS", request.getMobileOS())
						.queryParam("MobileApp", request.getMobileApp())
						.queryParam("_type", request.get_type())
						.queryParam("LDongRegnCd", request.getLDongRegnCd())
						.queryParam("lclsSystm1", request.getLclsSystm1());

					if (request.getAreaCode() != null) {
						builder.queryParam("areaCode", request.getAreaCode());
					}

					if (request.getContentTypeId() != null) {
						builder.queryParam("contentTypeId", request.getContentTypeId());
					}

					return builder.build();
				}).retrieve().bodyToMono(TOUR_PLACE_TYPE)
				.block();

		} catch (Exception e) {
			log.error("TourAPI 장소 요청 실패 - path: {}", path, e);
			return null;
		}
	}


	public <T> TourApiCommonResponse<List<T>> getDetail(
		String path,
		PlaceDetailRequestDto request,
		ParameterizedTypeReference<
			TourApiCommonResponse<List<T>>
			> typeReference
	) {
		try {
			return tourApiWebClient.get()
				.uri(uriBuilder -> uriBuilder
					.path(path)
					.queryParam("serviceKey", serviceKey)
					.queryParam("numOfRows", request.getNumOfRows())
					.queryParam("pageNo", request.getPageNo())
					.queryParam("MobileOS", request.getMobileOS())
					.queryParam("MobileApp", request.getMobileApp())
					.queryParam("_type", request.getType())
					.queryParam("contentId", request.getContentId())
					.build())
				.retrieve()
				.bodyToMono(typeReference)
				.block();

		} catch (Exception e) {
			log.error(
				"TourAPI 상세 요청 실패 - path: {}, contentId: {}",
				path,
				request.getContentId(),
				e
			);

			return null;
		}
	}

	public TourApiCommonResponse<List<TourPlaceDto>> getLocationPlaces(
		String path,
		LocationBasedRequestDto request
	) {
		try {
			return tourApiWebClient.get()
				.uri(uriBuilder -> {
					var builder = uriBuilder
						.path(path)
						.queryParam("serviceKey", serviceKey)
						.queryParam("numOfRows", request.getNumOfRows())
						.queryParam("pageNo", request.getPageNo())
						.queryParam("MobileOS", request.getMobileOS())
						.queryParam("MobileApp", request.getMobileApp())
						.queryParam("_type", request.get_type())
						.queryParam("mapX", request.getMapx())
						.queryParam("mapY", request.getMapy())
						.queryParam("radius", request.getRadius())
						.queryParam("LDongRegnCd", request.getLDongRegnCd());

					if (request.getLclsSystm1() != null) {
						builder.queryParam("lclsSystm1", request.getLclsSystm1());
					}

					if (request.getContentTypeId() != null) {
						builder.queryParam("contentTypeId", request.getContentTypeId());
					}

					return builder.build();
				})
				.retrieve()
				.bodyToMono(TOUR_PLACE_TYPE)
				.block();

		} catch (Exception e) {
			log.error("TourAPI 위치 기반 요청 실패 - path: {}", path, e);
			return null;
		}
	}

	public TourApiCommonResponse<List<TourPlaceDto>> getKeywordPlaces(
		String path,
		com.honjaopseoyae.domain.place.dto.request.KeywordSearchTourRequestDto request
	) {
		try {
			return tourApiWebClient.get()
				.uri(uriBuilder -> {
					var builder = uriBuilder
						.path(path)
						.queryParam("serviceKey", serviceKey)
						.queryParam("keyword", request.getKeyword())
						.queryParam("numOfRows", request.getNumOfRows())
						.queryParam("pageNo", request.getPageNo())
						.queryParam("MobileOS", request.getMobileOS())
						.queryParam("MobileApp", request.getMobileApp())
						.queryParam("_type", request.get_type());

					if (request.getAreaCode() != null) {
						builder.queryParam("areaCode", request.getAreaCode());
					}
					if (request.getArrange() != null) {
						builder.queryParam("arrange", request.getArrange());
					}
					if (request.getSigunguCode() != null) {
						builder.queryParam("sigunguCode", request.getSigunguCode());
					}
					if (request.getCat1() != null) {
						builder.queryParam("cat1", request.getCat1());
					}
					if (request.getCat2() != null) {
						builder.queryParam("cat2", request.getCat2());
					}
					if (request.getCat3() != null) {
						builder.queryParam("cat3", request.getCat3());
					}
					if (request.getLDongRegnCd() != null) {
						builder.queryParam("lDongRegnCd", request.getLDongRegnCd());
					}
					if (request.getLDongSignguCd() != null) {
						builder.queryParam("lDongSignguCd", request.getLDongSignguCd());
					}
					if (request.getLclsSystm1() != null) {
						builder.queryParam("lclsSystm1", request.getLclsSystm1());
					}

					return builder.build();
				})
				.retrieve()
				.bodyToMono(TOUR_PLACE_TYPE)
				.block();

		} catch (org.springframework.web.reactive.function.client.WebClientResponseException e) {
			log.error("TourAPI keyword search failed - path: {}, keyword: {}, response: {}",
				path, request.getKeyword(), e.getResponseBodyAsString(), e);
			return null;
		} catch (Exception e) {
			log.error("TourAPI 키워드 검색 요청 실패 - path: {}, keyword: {}", path, request.getKeyword(), e);
			return null;
		}
	}
}
