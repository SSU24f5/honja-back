package com.honjaopseoyae.domain.course.dto.response;

import com.honjaopseoyae.domain.course.entity.enums.InviteStatus;

import java.time.LocalDateTime;

public record CourseInvitationResponseDto(
        Long courseMemberId,
        Long courseId,
        String courseName,
        String courseDescription,
        String counterpartNickname,   // 받은목록=코스주인, 보낸목록=초대받은사람
        String counterpartEmail,
        LocalDateTime createdAt,
        InviteStatus status
) {}