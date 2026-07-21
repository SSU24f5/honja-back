package com.honjaopseoyae.domain.course.service;

import java.util.List;

import com.honjaopseoyae.domain.course.dto.request.BetweenPlacesRecommendRequestDto;
import com.honjaopseoyae.domain.course.dto.request.CourseOrderRecommendRequestDto;
import com.honjaopseoyae.domain.course.dto.response.CourseOrderRecommendResponseDto;
import com.honjaopseoyae.domain.place.dto.response.TourCommonResponseDto;

public interface RecommendService {
	CourseOrderRecommendResponseDto recommend(Long userId, CourseOrderRecommendRequestDto request);

	List<TourCommonResponseDto> recommendBetween(BetweenPlacesRecommendRequestDto request, String type);
}

