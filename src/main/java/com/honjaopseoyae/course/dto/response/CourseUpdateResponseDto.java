package com.honjaopseoyae.course.dto.response;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class CourseUpdateResponseDto {
	private Long courseId;
	private List<CoursePlaceResult> places;

	@Getter
	@Builder
	@AllArgsConstructor
	public static class CoursePlaceResult {
		private Long coursePlaceId;
		private Long placeId;
		private Integer order;
		private String distance;
		private String timeTaken;
	}
}
