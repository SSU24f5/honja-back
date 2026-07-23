package com.honjaopseoyae.domain.course.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BetweenPlacesRecommendRequestDto {
	@NotNull
	private Long startCoursePlaceId;

	@NotNull
	private Long endCoursePlaceId;
}
