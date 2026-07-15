package com.honjaopseoyae.domain.course.service;

import com.honjaopseoyae.domain.course.dto.request.CourseOrderRecommendRequestDto;
import com.honjaopseoyae.domain.course.dto.response.CourseOrderRecommendResponseDto;

public interface RecommendService {
	CourseOrderRecommendResponseDto recommend(Long userId, CourseOrderRecommendRequestDto request);
}
