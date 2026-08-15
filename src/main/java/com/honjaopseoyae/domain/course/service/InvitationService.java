package com.honjaopseoyae.domain.course.service;

import com.honjaopseoyae.domain.course.dto.request.SendInvitationRequestDto;
import com.honjaopseoyae.domain.course.dto.response.CourseInvitationResponseDto;
import com.honjaopseoyae.domain.user.entity.User;

import java.util.List;

public interface InvitationService {
    List<CourseInvitationResponseDto> getReceivedInvitations(User user);
    List<CourseInvitationResponseDto> getSentInvitations(User user);
    void sendInvitations(Long courseId, SendInvitationRequestDto dto, User sender);
    void acceptInvitation(Long courseMemberId, User user);
    void rejectInvitation(Long courseMemberId, User user);
    void cancelInvitation(Long courseMemberId, User user);
}