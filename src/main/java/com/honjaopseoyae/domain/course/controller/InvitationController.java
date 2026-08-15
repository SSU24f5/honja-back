package com.honjaopseoyae.domain.course.controller;

import com.honjaopseoyae.domain.course.dto.request.SendInvitationRequestDto;
import com.honjaopseoyae.domain.course.dto.response.CourseInvitationResponseDto;
import com.honjaopseoyae.domain.course.service.InvitationService;
import com.honjaopseoyae.domain.user.entity.User;
import com.honjaopseoyae.global.apipayload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class InvitationController {

    private final InvitationService invitationService;

    @GetMapping("/invitations/received")
    public ApiResponse<List<CourseInvitationResponseDto>> getReceivedInvitations(
            @AuthenticationPrincipal User user) {
        return ApiResponse.onSuccess(invitationService.getReceivedInvitations(user));
    }

    @GetMapping("/invitations/sent")
    public ApiResponse<List<CourseInvitationResponseDto>> getSentInvitations(
            @AuthenticationPrincipal User user) {
        return ApiResponse.onSuccess(invitationService.getSentInvitations(user));
    }

    @PostMapping("/invitations/courses/{courseId}")
    public ApiResponse<Void> sendInvitations(
            @PathVariable Long courseId,
            @RequestBody SendInvitationRequestDto dto,
            @AuthenticationPrincipal User user) {
        invitationService.sendInvitations(courseId, dto, user);
        return ApiResponse.onSuccess(null);
    }

    @PostMapping("/invitations/{invitationId}/accept")
    public ApiResponse<Void> acceptInvitation(
            @PathVariable Long invitationId,
            @AuthenticationPrincipal User user) {
        invitationService.acceptInvitation(invitationId, user);
        return ApiResponse.onSuccess(null);
    }

    @PostMapping("/invitations/{invitationId}/reject")
    public ApiResponse<Void> rejectInvitation(
            @PathVariable Long invitationId,
            @AuthenticationPrincipal User user) {
        invitationService.rejectInvitation(invitationId, user);
        return ApiResponse.onSuccess(null);
    }

    @DeleteMapping("/invitations/{invitationId}")
    public ApiResponse<Void> cancelInvitation(
            @PathVariable Long invitationId,
            @AuthenticationPrincipal User user) {
        invitationService.cancelInvitation(invitationId, user);
        return ApiResponse.onSuccess(null);
    }
}