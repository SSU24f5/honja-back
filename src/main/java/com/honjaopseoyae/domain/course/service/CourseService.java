package com.honjaopseoyae.domain.course.service;

import com.honjaopseoyae.domain.course.dto.request.CourseCreateRequestDto;
import com.honjaopseoyae.domain.course.dto.request.CourseUpdateRequestDto;
import com.honjaopseoyae.domain.course.dto.response.CourseCreateResponseDto;
import com.honjaopseoyae.domain.course.dto.response.CourseDetailResponseDto;
import com.honjaopseoyae.domain.course.dto.response.CourseListResponseDto;
import com.honjaopseoyae.domain.course.dto.response.CourseUpdateResponseDto;

import java.util.List;

public interface CourseService {

	void deleteCourse(Long courseId, Long userId);

	CourseCreateResponseDto createCourse(CourseCreateRequestDto requestDto, Long userId);

	CourseUpdateResponseDto updateCourse(CourseUpdateRequestDto requestDto, Long userId);

	CourseDetailResponseDto getCourseDetail(Long courseId, Long userId);

	List<CourseListResponseDto> getMyCourses(Long userId);

	void inviteMemberByEmail(com.honjaopseoyae.domain.course.dto.request.CourseInviteRequestDto requestDto, Long currentUserId);

	List<com.honjaopseoyae.domain.course.dto.response.CourseInvitationResponseDto> getMyInvitations(Long currentUserId);

	void acceptInvitation(Long courseMemberId, Long currentUserId);

	void rejectInvitation(Long courseMemberId, Long currentUserId);
}
