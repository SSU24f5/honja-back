package com.honjaopseoyae.domain.place.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import com.honjaopseoyae.domain.place.converter.TourPlaceConverter;
import com.honjaopseoyae.domain.place.dto.common.TourApiCommonResponse;
import com.honjaopseoyae.domain.place.dto.request.LocationBasedRequestDto;
import com.honjaopseoyae.domain.place.dto.request.UserLocationDto;
import com.honjaopseoyae.domain.place.dto.response.TourCommonResponseDto;
import com.honjaopseoyae.domain.place.dto.response.TourPlaceDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class LocationServiceImpl implements LocationService {
	private final WebClient webClient;

	@Value("${tour-api.service-key}")
	private String serviceKey;

	@Override
	public List<TourCommonResponseDto> getLocationBasedPetPlace(UserLocationDto dto) {
		ParameterizedTypeReference<TourApiCommonResponse<List<TourPlaceDto>>> typeRef =
			new ParameterizedTypeReference<>() {};

		LocationBasedRequestDto requestDto = LocationBasedRequestDto.builder()
			.mapx(dto.getMapx())
			.mapy(dto.getMapy())
			.radius("10000")
			.build();

		try {
			TourApiCommonResponse<List<TourPlaceDto>> response = webClient.get()
				.uri(uriBuilder -> uriBuilder
					.path("/KorPetTourService2/locationBasedList2")
					.queryParam("serviceKey", serviceKey)
					.queryParam("numOfRows", requestDto.getNumOfRows())
					.queryParam("pageNo", requestDto.getPageNo())
					.queryParam("MobileOS", requestDto.getMobileOS())
					.queryParam("MobileApp", requestDto.getMobileApp())
					.queryParam("_type", requestDto.get_type())
					.queryParam("LDongRegnCd", requestDto.getLDongRegnCd())
					.queryParam("lclsSystm1", requestDto.getLclsSystm1())
					.queryParam("mapX", requestDto.getMapx())
					.queryParam("mapY", requestDto.getMapy())
					.queryParam("radius", requestDto.getRadius())
					.build())
				.retrieve()
				.bodyToMono(typeRef)
				.block();

			log.info("TourAPI 위치 기반 반려동물 요청 성공 - 총 개수: {}",
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
			log.error("TourAPI 위치 기반 반려동물 요청 중 통신/파싱 에러 발생 : ", e);
			return new ArrayList<>();
		}
	}

	@Override
	public List<TourCommonResponseDto> getLocationBasedBarrierFreePlace(UserLocationDto dto) {
		ParameterizedTypeReference<TourApiCommonResponse<List<TourPlaceDto>>> typeRef =
			new ParameterizedTypeReference<>() {};

		LocationBasedRequestDto requestDto = LocationBasedRequestDto.builder()
			.mapx(dto.getMapx())
			.mapy(dto.getMapy())
			.radius("10000")
			.build();

		try {
			TourApiCommonResponse<List<TourPlaceDto>> response = webClient.get()
				.uri(uriBuilder -> uriBuilder
					.path("/KorWithService2/locationBasedList2")
					.queryParam("serviceKey", serviceKey)
					.queryParam("numOfRows", requestDto.getNumOfRows())
					.queryParam("pageNo", requestDto.getPageNo())
					.queryParam("MobileOS", requestDto.getMobileOS())
					.queryParam("MobileApp", requestDto.getMobileApp())
					.queryParam("_type", requestDto.get_type())
					.queryParam("LDongRegnCd", requestDto.getLDongRegnCd())
					.queryParam("lclsSystm1", requestDto.getLclsSystm1())
					.queryParam("mapX", requestDto.getMapx())
					.queryParam("mapY", requestDto.getMapy())
					.queryParam("radius", requestDto.getRadius())
					.build())
				.retrieve()
				.bodyToMono(typeRef)
				.block();

			log.info("TourAPI 위치 기반 무장애 요청 성공 - 총 개수: {}",
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
			log.error("TourAPI 위치 기반 무장애 요청 중 통신/파싱 에러 발생 : ", e);
			return new ArrayList<>();
		}
	}
}
