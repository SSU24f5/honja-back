package com.honjaopseoyae.domain.course.dto.response;

import java.time.LocalDateTime;

import com.honjaopseoyae.domain.course.entity.enums.InviteStatus;
import com.honjaopseoyae.domain.course.entity.mapping.CourseMember;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseInvitationResponseDto {

	private Long courseMemberId;
	private Long courseId;
	private String courseName;
	private String courseDescription;
	private String inviterName;
	private String inviterEmail;
	private LocalDateTime createdAt;
	private InviteStatus status;

	public static CourseInvitationResponseDto from(CourseMember member, String inviterName, String inviterEmail) {
		return CourseInvitationResponseDto.builder()
			.courseMemberId(member.getId())
			.courseId(member.getCourse().getId())
			.courseName(member.getCourse().getName())
			.courseDescription(member.getCourse().getDescription())
			.inviterName(inviterName)
			.inviterEmail(inviterEmail)
			.createdAt(member.getCreatedAt())
			.status(member.getStatus())
			.build();
	}
}
