package com.honjaopseoyae.domain.course.controller;

import com.honjaopseoyae.domain.course.dto.request.CourseCreateRequestDto;
import com.honjaopseoyae.domain.course.dto.request.CourseUpdateRequestDto;
import com.honjaopseoyae.domain.course.dto.response.CourseCreateResponseDto;
import com.honjaopseoyae.domain.course.dto.response.CourseDetailResponseDto;
import com.honjaopseoyae.domain.course.dto.response.CourseListResponseDto;
import com.honjaopseoyae.domain.course.dto.response.CourseUpdateResponseDto;
import com.honjaopseoyae.domain.course.service.CourseService;
import com.honjaopseoyae.domain.user.entity.User;
import com.honjaopseoyae.global.apipayload.ApiResponse;
import com.honjaopseoyae.global.security.PrincipalDetails;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @GetMapping
    public ApiResponse<List<CourseListResponseDto>> getMyCourses(@AuthenticationPrincipal PrincipalDetails pd) {
        List<CourseListResponseDto> response = courseService.getMyCourses(pd.getUserId());
        return ApiResponse.onSuccess(response);
    }

    @GetMapping("/{courseId}")
    public ApiResponse<CourseDetailResponseDto> getCourseDetail(@PathVariable Long courseId, @AuthenticationPrincipal PrincipalDetails pd) {
        CourseDetailResponseDto response = courseService.getCourseDetail(courseId, pd.getUserId());
        return ApiResponse.onSuccess(response);
    }

    @PostMapping
    public ApiResponse<CourseCreateResponseDto> createCourse(@RequestBody @Valid CourseCreateRequestDto requestDto, @AuthenticationPrincipal PrincipalDetails pd) {

        CourseCreateResponseDto response = courseService.createCourse(requestDto, pd.getUserId());
        return ApiResponse.onSuccess(response);
    }

    @PutMapping
    public ApiResponse<CourseUpdateResponseDto> updateCourse(@RequestBody @Valid CourseUpdateRequestDto requestDto, @AuthenticationPrincipal PrincipalDetails pd) {
        CourseUpdateResponseDto response = courseService.updateCourse(requestDto, pd.getUserId());
        return ApiResponse.onSuccess(response);
    }

    @DeleteMapping("/{courseId}")
    public ApiResponse<String> deleteCourse(
            @PathVariable Long courseId,
            @AuthenticationPrincipal PrincipalDetails pd) {
        courseService.deleteCourse(courseId, pd.getUserId());
        return ApiResponse.onSuccess("코스가 성공적으로 삭제되었습니다.");
    }

    @PostMapping("/invitations")
    public ApiResponse<String> inviteMember(
            @RequestBody @Valid com.honjaopseoyae.domain.course.dto.request.CourseInviteRequestDto requestDto,
            @AuthenticationPrincipal PrincipalDetails pd) {
        courseService.inviteMemberByEmail(requestDto, pd.getUserId());
        return ApiResponse.onSuccess("초대 요청이 성공적으로 발송되었습니다.");
    }

    @GetMapping("/invitations")
    public ApiResponse<List<com.honjaopseoyae.domain.course.dto.response.CourseInvitationResponseDto>> getMyInvitations(
            @AuthenticationPrincipal PrincipalDetails pd) {
        List<com.honjaopseoyae.domain.course.dto.response.CourseInvitationResponseDto> response = courseService.getMyInvitations(pd.getUserId());
        return ApiResponse.onSuccess(response);
    }

    @PostMapping("/invitations/{courseMemberId}/accept")
    public ApiResponse<String> acceptInvitation(
            @PathVariable Long courseMemberId,
            @AuthenticationPrincipal PrincipalDetails pd) {
        courseService.acceptInvitation(courseMemberId, pd.getUserId());
        return ApiResponse.onSuccess("초대를 성공적으로 수락했습니다.");
    }

    @PostMapping("/invitations/{courseMemberId}/reject")
    public ApiResponse<String> rejectInvitation(
            @PathVariable Long courseMemberId,
            @AuthenticationPrincipal PrincipalDetails pd) {
        courseService.rejectInvitation(courseMemberId, pd.getUserId());
        return ApiResponse.onSuccess("초대를 거절했습니다.");
    }
}
