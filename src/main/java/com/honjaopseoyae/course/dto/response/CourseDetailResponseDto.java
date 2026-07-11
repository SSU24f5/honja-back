package com.honjaopseoyae.course.dto.response;

import java.time.LocalDate;
import java.util.List;

import com.honjaopseoyae.domain.course.CourseType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CourseDetailResponseDto {

	private Long courseId;
	private String name;
	private String description;
	private boolean isPublic;
	private boolean isExternal;
	private LocalDate startDate;
	private LocalDate endDate;
	private CourseType courseType;
	private List<CoursePlaceItem> places;

	@Getter
	@Builder
	public static class CoursePlaceItem {
		private Long coursePlaceId;
		private Long placeId;
		private int order;
		private String distance;
		private String timeTaken;
		
		// 장소 상세 정보
		private String contentId;
		private String title;
		private String addr1;
		private String addr2;
		private double mapx;
		private double mapy;
		private String image;
		private boolean petPlace;
		private boolean barrierFree;
	}
}
