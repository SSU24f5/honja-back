package com.honjaopseoyae.course.service;

import com.honjaopseoyae.course.dto.request.CourseCreateRequestDto;
import com.honjaopseoyae.course.dto.request.CourseUpdateRequestDto;
import com.honjaopseoyae.course.dto.response.CourseResponseDto;
import com.honjaopseoyae.course.dto.response.CourseUpdateResponseDto;

public interface CourseService {

	void deleteCourse(Long courseId, Long userId);

	CourseResponseDto createCourse(CourseCreateRequestDto requestDto);

	CourseUpdateResponseDto updateCourse(CourseUpdateRequestDto requestDto);
}
