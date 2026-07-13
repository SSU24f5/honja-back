package com.honjaopseoyae.domain.course.dto.response;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

import com.honjaopseoyae.domain.course.entity.Course;
import com.honjaopseoyae.domain.course.entity.OrderType;
import com.honjaopseoyae.domain.course.entity.mapping.CoursePlace;

@Getter
@Builder
@AllArgsConstructor
public class CourseUpdateResponseDto {

	private Long courseId;
	private List<CourseDateResult> dates;

	public static CourseUpdateResponseDto of(
		Course course,
		List<CourseDateResult> dates
	) {
		return CourseUpdateResponseDto.builder()
			.courseId(course.getId())
			.dates(dates)
			.build();
	}

	@Getter
	@Builder
	@AllArgsConstructor
	public static class CourseDateResult {

		private LocalDate date;
		private List<CoursePlaceResult> places;

		public static CourseDateResult of(
			LocalDate date,
			List<CoursePlaceResult> places
		) {
			return CourseDateResult.builder()
				.date(date)
				.places(places)
				.build();
		}
	}

	@Getter
	@Builder
	@AllArgsConstructor
	public static class CoursePlaceResult {

		private Long coursePlaceId;
		private Long placeId;
		private Integer order;
		private OrderType orderType;
		private String distance;
		private String timeTaken;

		public static CoursePlaceResult from(CoursePlace coursePlace) {
			return CoursePlaceResult.builder()
				.coursePlaceId(coursePlace.getId())
				.placeId(coursePlace.getPlace().getId())
				.order(coursePlace.getSortOrder().intValue())
				.orderType(coursePlace.getOrderType())
				.distance(coursePlace.getDistance())
				.timeTaken(coursePlace.getTimeTaken())
				.build();
		}
	}
}
