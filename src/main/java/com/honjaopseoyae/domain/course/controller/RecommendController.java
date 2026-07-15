package com.honjaopseoyae.domain.course.controller;


import com.honjaopseoyae.domain.course.dto.request.CourseOrderRecommendRequestDto;
import com.honjaopseoyae.domain.course.dto.response.CourseOrderRecommendResponseDto;
import com.honjaopseoyae.domain.course.service.RecommendService;
import com.honjaopseoyae.domain.user.entity.User;
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
}
