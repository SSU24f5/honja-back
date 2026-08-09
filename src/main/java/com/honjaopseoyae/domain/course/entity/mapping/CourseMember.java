package com.honjaopseoyae.domain.course.entity.mapping;

import com.honjaopseoyae.domain.course.entity.Course;
import com.honjaopseoyae.domain.course.entity.enums.CourseRole;
import com.honjaopseoyae.domain.course.entity.enums.InviteStatus;
import com.honjaopseoyae.domain.user.entity.User;
import com.honjaopseoyae.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "course_member",
	uniqueConstraints = {
		@UniqueConstraint(columnNames = {"course_id", "user_id"})
	})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CourseMember extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "course_id", nullable = false)
	private Course course;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private CourseRole role;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private InviteStatus status;

	@Builder
	public CourseMember(Course course, User user, CourseRole role, InviteStatus status) {
		this.course = course;
		this.user = user;
		this.role = role;
		this.status = status;
	}

	public void accept() {
		this.status = InviteStatus.ACCEPTED;
	}

	public void reject() {
		this.status = InviteStatus.REJECTED;
	}
}