package com.honjaopseoyae.domain.place.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.honjaopseoyae.domain.place.dto.response.TourPlacesResponseDto;
import com.honjaopseoyae.global.apipayload.ApiResponse;
import com.honjaopseoyae.domain.place.dto.request.UserLocationDto;
import com.honjaopseoyae.domain.place.dto.response.TourCommonResponseDto;
import com.honjaopseoyae.domain.place.service.LocationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/location")
@RequiredArgsConstructor
public class LocationController {
	private final LocationService locationService;

	// 위치 기반 무장애 전체 조회 api
	@PostMapping("/barrier-free")
	public ApiResponse<TourPlacesResponseDto> getLocationBasedBarrierFreePlace(@RequestBody UserLocationDto dto) {
		List<TourCommonResponseDto> response = locationService.getLocationBasedBarrierFreePlace(dto);
		return ApiResponse.onSuccess(TourPlacesResponseDto.from(response));
	}

	// 위치 기반 반려동물 전체 조회 api
	@PostMapping("/pet-friendly")
	public ApiResponse<TourPlacesResponseDto> getLocationBasedPetPlace(
		@RequestBody UserLocationDto dto
	) {
		List<TourCommonResponseDto> response =
			locationService.getLocationBasedPetPlace(dto);

		return ApiResponse.onSuccess(TourPlacesResponseDto.from(response));
	}


	@PostMapping("/common")
	public ApiResponse<TourPlacesResponseDto> getCommonPlaces(
		@RequestBody UserLocationDto requestDto
	) {
		List<TourCommonResponseDto> response = locationService.getCommonPlaceByCategoryFromTourAPI(requestDto);
		return ApiResponse.onSuccess(TourPlacesResponseDto.from(response));
	}
}
