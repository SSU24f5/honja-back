package com.honjaopseoyae.domain.course.support;

import org.springframework.stereotype.Component;

import com.honjaopseoyae.domain.course.entity.enums.InviteStatus;
import com.honjaopseoyae.domain.course.entity.mapping.CourseMember;
import com.honjaopseoyae.domain.course.repository.CourseMemberRepository;
import com.honjaopseoyae.global.apipayload.domain.CourseErrorStatus;
import com.honjaopseoyae.global.apipayload.exception.GeneralException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CourseMemberValidator {

	private final CourseMemberRepository repository;

	public CourseMember validateExistingCourseMember(Long courseMemberId) {
		return repository.findById(courseMemberId)
			.orElseThrow(() ->
				new GeneralException(CourseErrorStatus.COURSE_NOT_FOUND));
	}

	public Void validateCourseAccess(Long courseId, Long currentUserId) {
		repository.findByCourseIdAndUserIdAndStatus(courseId, currentUserId, InviteStatus.ACCEPTED)
			.orElseThrow(() -> new GeneralException(CourseErrorStatus.COURSE_ACCESS_DENIED));
		return null;
	}
}