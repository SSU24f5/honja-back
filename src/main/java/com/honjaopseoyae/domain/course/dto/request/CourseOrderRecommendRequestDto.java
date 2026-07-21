package com.honjaopseoyae.domain.course.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CourseOrderRecommendRequestDto {
	@NotNull
	private Long courseId;

	@NotNull
	private LocalDate date;
}
