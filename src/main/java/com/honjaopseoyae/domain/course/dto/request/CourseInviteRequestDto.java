package com.honjaopseoyae.domain.course.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CourseInviteRequestDto {

	@NotNull(message = "courseId is required.")
	private Long courseId;

	@NotBlank(message = "email is required.")
	@Email(message = "Invalid email format.")
	private String email;
}
