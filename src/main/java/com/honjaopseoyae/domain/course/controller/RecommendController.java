package com.honjaopseoyae.domain.course.controller;

import java.util.List;

import com.honjaopseoyae.domain.course.dto.request.BetweenPlacesRecommendRequestDto;
import com.honjaopseoyae.domain.course.dto.request.CourseOrderRecommendRequestDto;
import com.honjaopseoyae.domain.course.dto.response.CourseOrderRecommendResponseDto;
import com.honjaopseoyae.domain.course.service.RecommendService;
import com.honjaopseoyae.domain.place.dto.response.TourCommonResponseDto;
import com.honjaopseoyae.domain.user.entity.User;
import com.honjaopseoyae.global.apipayload.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/recommendation")
@RequiredArgsConstructor
public class RecommendController {

	private final RecommendService recommendService;

	@PostMapping("/course-order")
	public ResponseEntity<CourseOrderRecommendResponseDto> recommendOrder(
		@AuthenticationPrincipal User user, @Valid @RequestBody CourseOrderRecommendRequestDto request) {
		CourseOrderRecommendResponseDto response = recommendService.recommend(user.getId(), request);
		return ResponseEntity.ok(response);
	}

	@PostMapping("/between/barrier-free")
	public ApiResponse<List<TourCommonResponseDto>> recommendBetweenBarrierFree(
		@Valid @RequestBody BetweenPlacesRecommendRequestDto request
	) {
		List<TourCommonResponseDto> response = recommendService.recommendBetween(request, "barrier-free");
		return ApiResponse.onSuccess(response);
	}

	@PostMapping("/between/pet-friendly")
	public ApiResponse<List<TourCommonResponseDto>> recommendBetweenPetFriendly(
		@Valid @RequestBody BetweenPlacesRecommendRequestDto request
	) {
		List<TourCommonResponseDto> response = recommendService.recommendBetween(request, "pet-friendly");
		return ApiResponse.onSuccess(response);
	}

	@PostMapping("/between/general")
	public ApiResponse<List<TourCommonResponseDto>> recommendBetweenGeneral(
		@Valid @RequestBody BetweenPlacesRecommendRequestDto request
	) {
		List<TourCommonResponseDto> response = recommendService.recommendBetween(request, "general");
		return ApiResponse.onSuccess(response);
	}
}
