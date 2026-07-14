package com.honjaopseoyae.domain.place.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class TourPlacesResponseDto {
	// 위치 기반 + 카테고리 조회 시 (locationService) List<TourCommonResponseDto>에 itemCount를 더한 응답 dto

	private int count;
	private List<TourCommonResponseDto> items;

	public static TourPlacesResponseDto from(
		List<TourCommonResponseDto> items
	) {
		return TourPlacesResponseDto.builder()
			.count(items.size())
			.items(items)
			.build();
	}
}