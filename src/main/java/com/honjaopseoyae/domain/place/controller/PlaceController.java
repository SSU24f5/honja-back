package com.honjaopseoyae.domain.place.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.honjaopseoyae.global.apipayload.ApiResponse;
import com.honjaopseoyae.domain.place.dto.common.TourApiCommonResponse;
import com.honjaopseoyae.domain.place.dto.response.DetailAccessibilityDto;
import com.honjaopseoyae.domain.place.dto.response.PetDetailResponseDto;
import com.honjaopseoyae.domain.place.dto.response.TourCommonResponseDto;
import com.honjaopseoyae.domain.place.service.PlaceService;
import com.honjaopseoyae.domain.place.service.TourApiService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/tour")
@RequiredArgsConstructor
public class PlaceController {
	private final PlaceService placeService;
	private final TourApiService tourApiService;

	// 무장애 단건 상세 조회 api
	@GetMapping("/barrier-free/{contentId}")
	public ApiResponse<TourApiCommonResponse<List<DetailAccessibilityDto>>> getBarrierFreeInfo(@PathVariable Long contentId) {
		TourApiCommonResponse<List<DetailAccessibilityDto>> response = tourApiService.getBarrierFreeInfo(contentId);
		return ApiResponse.onSuccess(response);
	}

	// 반려동물 단건 상세 조회 api
	@GetMapping("/pet-friendly/{contentId}")
	public ApiResponse<TourApiCommonResponse<List<PetDetailResponseDto>>> getPetDetailInfo(@PathVariable Long contentId) {
		TourApiCommonResponse<List<PetDetailResponseDto>> response = tourApiService.getPetDetailInfo(contentId);
		return ApiResponse.onSuccess(response);
	}

	// 무장애 전체 조회 api - 쓸 일 없을 듯
	@GetMapping("/barrier-free")
	public ApiResponse<List<TourCommonResponseDto>> getBarrierFreePlace() {
		List<TourCommonResponseDto> response = tourApiService.getBarrierFreePlaceFromTourAPI();
		return ApiResponse.onSuccess(response);
	}

	// 반려동물 전체 조회 api - 쓸 일 없을 듯
	@GetMapping("/pet-friendly")
	public ApiResponse<List<TourCommonResponseDto>> getPetPlace() {
		List<TourCommonResponseDto> response = tourApiService.getPetPlaceFromTourAPI();
		return ApiResponse.onSuccess(response);
	}

	/** TourAPI KorService2/searchKeyword2 기반 키워드 검색 */
	@GetMapping("/search/general")
	public ApiResponse<List<TourCommonResponseDto>> searchPlaces(
		@RequestParam String keyword,
		@RequestParam(defaultValue = "1") Integer pageNo,
		@RequestParam(defaultValue = "10") Integer numOfRows
	) {
		List<TourCommonResponseDto> response =
			tourApiService.searchCommonPlacesByKeyword(keyword, pageNo, numOfRows);
		return ApiResponse.onSuccess(response);
	}

	@GetMapping("/search/barrier-free")
	public ApiResponse<List<TourCommonResponseDto>> searchBarrierFreePlaces(
		@RequestParam String keyword,
		@RequestParam(defaultValue = "1") Integer pageNo,
		@RequestParam(defaultValue = "10") Integer numOfRows
	) {
		return ApiResponse.onSuccess(
			tourApiService.searchBarrierFreePlacesByKeyword(keyword, pageNo, numOfRows));
	}

	@GetMapping("/search/pet")
	public ApiResponse<List<TourCommonResponseDto>> searchPetPlaces(
		@RequestParam String keyword,
		@RequestParam(defaultValue = "1") Integer pageNo,
		@RequestParam(defaultValue = "10") Integer numOfRows
	) {
		return ApiResponse.onSuccess(
			tourApiService.searchPetPlacesByKeyword(keyword, pageNo, numOfRows));
	}
}

