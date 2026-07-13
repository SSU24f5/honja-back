package com.honjaopseoyae.course.dto.request;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

import com.honjaopseoyae.course.entity.OrderType;
import com.honjaopseoyae.place.entity.PlaceType;

@Getter
@NoArgsConstructor
public class CourseUpdateRequestDto {

	@NotNull
	private Long courseId;

	@NotEmpty
	@Valid
	private List<CourseDateItem> dates;

	@Getter
	@NoArgsConstructor
	public static class CourseDateItem {

		@NotNull
		private LocalDate date;

		@NotEmpty
		@Valid
		private List<CoursePlaceItem> places;
	}

	@Getter
	@NoArgsConstructor
	public static class CoursePlaceItem {

		// 기존 CoursePlace 수정 시
		private Long coursePlaceId;

		// 신규 장소 추가 시
		private Long placeId;

		@NotNull
		private Integer order;

		private OrderType orderType;

		private String contentId;
		private Boolean isPetPlace;
		private Boolean isBarrierFree;
		private String mapx;
		private String mapy;
		@NotNull
		private PlaceType placeType;

		public boolean isNewItem() {
			return coursePlaceId == null;
		}
	}
}

