package com.honjaopseoyae.course.service;

import com.honjaopseoyae.course.dto.request.CourseCreateRequestDto;
import com.honjaopseoyae.course.dto.request.CourseUpdateRequestDto;
import com.honjaopseoyae.course.dto.response.CourseCreateResponseDto;
import com.honjaopseoyae.course.dto.response.CourseDetailResponseDto;
import com.honjaopseoyae.course.dto.response.CourseUpdateResponseDto;

public interface CourseService {

	void deleteCourse(Long courseId, Long userId);

	CourseCreateResponseDto createCourse(CourseCreateRequestDto requestDto);

	CourseUpdateResponseDto updateCourse(CourseUpdateRequestDto requestDto);

	CourseDetailResponseDto getCourseDetail(Long courseId);
}
