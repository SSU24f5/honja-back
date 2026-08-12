package com.honjaopseoyae.domain.course.dto.response;

import com.honjaopseoyae.domain.course.entity.enums.OrderType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class CourseOrderRecommendResponseDto {

	private Long courseId;
	private LocalDate date;
	private List<CoursePlaceItem> places;

	public static CourseOrderRecommendResponseDto of(Long courseId, LocalDate date, List<CoursePlaceItem> places) {
		return CourseOrderRecommendResponseDto.builder()
			.courseId(courseId)
			.date(date)
			.places(places)
			.build();
	}

	@Getter
	@Builder
	public static class CoursePlaceItem {

		private Long coursePlaceId;
		private Long placeId;
		private int order;
		private OrderType orderType;
		private String distance;
		private String timeTaken;

		private String contentId;
		private String title;
		private double mapx;
		private double mapy;
		private boolean petPlace;
		private boolean barrierFree;
	}
}
