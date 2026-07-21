package com.honjaopseoyae.domain.course.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.honjaopseoyae.domain.course.entity.enums.CourseRole;
import com.honjaopseoyae.domain.course.entity.enums.InviteStatus;
import com.honjaopseoyae.domain.course.entity.mapping.CourseMember;

public interface CourseMemberRepository extends JpaRepository<CourseMember, Long> {
	List<CourseMember> findAllByUserIdAndStatus(Long userId, InviteStatus status);

	Optional<CourseMember> findByCourseIdAndUserIdAndStatus(
		Long courseId,
		Long userId,
		InviteStatus status
	);

	Optional<CourseMember> findByCourseIdAndUserIdAndRoleAndStatus(Long courseId, Long userId, CourseRole role, InviteStatus status);
}
