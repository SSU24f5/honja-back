package com.honjaopseoyae.domain.place.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

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

    //장소 가져오기
    @PostMapping("/sync")
    public ApiResponse<String> syncTourPlaces() {
        tourApiService.syncTourPlacesWithApi();
        return ApiResponse.onSuccess("동기화 완료");
    }
}

