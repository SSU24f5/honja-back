package com.honjaopseoyae.domain.course.dto.response;

import java.time.LocalDate;
import java.util.List;

import com.honjaopseoyae.domain.course.entity.Course;
import com.honjaopseoyae.domain.course.entity.enums.CourseType;
import com.honjaopseoyae.domain.course.entity.enums.OrderType;
import com.honjaopseoyae.domain.course.entity.mapping.CoursePlace;
import com.honjaopseoyae.domain.place.entity.Place;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CourseDetailResponseDto {

	private Long courseId;
	private String name;
	private String description;
	private boolean isPublic;
	private LocalDate startDate;
	private LocalDate endDate;
	private CourseType courseType;

	private List<CourseDateItem> dates;

	public static CourseDetailResponseDto of(
		Course course,
		List<CourseDateItem> dates
	) {
		return CourseDetailResponseDto.builder()
			.courseId(course.getId())
			.name(course.getName())
			.description(course.getDescription())
			.isPublic(course.isPublic())
			.startDate(course.getStartDate())
			.endDate(course.getEndDate())
			.courseType(course.getCourseType())
			.dates(dates)
			.build();
	}

	@Getter
	@Builder
	public static class CourseDateItem {

		private LocalDate date;
		private List<CoursePlaceItem> places;

		public static CourseDateItem of(
			LocalDate date,
			List<CoursePlaceItem> places
		) {
			return CourseDateItem.builder()
				.date(date)
				.places(places)
				.build();
		}
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
		private double mapx;
		private double mapy;
		private boolean petPlace;
		private boolean barrierFree;

		public static CoursePlaceItem from(CoursePlace coursePlace) {
			Place place = coursePlace.getPlace();

			return CoursePlaceItem.builder()
				.coursePlaceId(coursePlace.getId())
				.placeId(place.getId())
				.order(coursePlace.getSortOrder().intValue())
				.orderType(coursePlace.getOrderType())
				.distance(coursePlace.getDistance())
				.timeTaken(coursePlace.getTimeTaken())
				.contentId(place.getContentId())
				.mapx(place.getMapx())
				.mapy(place.getMapy())
				.petPlace(place.isPetPlace())
				.barrierFree(place.isBarrierFree())
				.build();
		}
	}
}
