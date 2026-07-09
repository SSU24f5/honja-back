package com.honjaopseoyae.course.dto.request;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class CourseUpdateRequestDto {

	@NotNull
	private Long courseId;

	@NotEmpty
	@Valid
	private List<CoursePlaceItem> places;

	@Getter
	@NoArgsConstructor
	public static class CoursePlaceItem {

		// 기존 항목 수정 시 사용 (order 변경)
		private Long coursePlaceId;

		// 신규 추가 시 사용
		private Long placeId;

		@NotNull
		private Integer order;

		private String contentId;
		private Boolean isPetPlace;
		private Boolean isBarrierFree;
		private String mapx;
		private String mapy;

		public boolean isNewItem() {
			return coursePlaceId == null;
		}
	}
}

