package com.honjaopseoyae.domain.place.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.honjaopseoyae.domain.place.client.TourApiClient;
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
@Transactional(readOnly = true)
public class LocationServiceImpl implements LocationService {

	private static final String PET_LOCATION_PATH = "/KorPetTourService2/locationBasedList2";
	private static final String BARRIER_FREE_LOCATION_PATH = "/KorWithService2/locationBasedList2";

	private static final String COMMON_PATH = "/KorService2/locationBasedList2";


	private final TourApiClient tourApiClient;

	@Override
	public List<TourCommonResponseDto> getLocationBasedPetPlace(UserLocationDto dto) {
		LocationBasedRequestDto requestDto = createLocationRequest(dto);

		return fetchLocationPlaces(PET_LOCATION_PATH, requestDto);
	}

	@Override
	public List<TourCommonResponseDto> getLocationBasedBarrierFreePlace(UserLocationDto dto) {
		LocationBasedRequestDto requestDto = createLocationRequest(dto);

		return fetchLocationPlaces(BARRIER_FREE_LOCATION_PATH, requestDto);
	}

	private List<TourCommonResponseDto> fetchLocationPlaces(
		String path,
		LocationBasedRequestDto requestDto
	) {
		TourApiCommonResponse<List<TourPlaceDto>> response =
			tourApiClient.getLocationPlaces(path, requestDto);

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

	private LocationBasedRequestDto createLocationRequest(UserLocationDto dto) {
		return LocationBasedRequestDto.builder()
			.mapx(dto.getMapx())
			.mapy(dto.getMapy())
			.radius("10000")
			.numOfRows(400)
			.build();
	}

	@Override
	public List<TourCommonResponseDto> getCommonPlaceByCategoryFromTourAPI(UserLocationDto dto) {
		LocationBasedRequestDto locationDto = createLocationRequest(dto);

		return fetchLocationPlaces(COMMON_PATH, locationDto);
	}

}