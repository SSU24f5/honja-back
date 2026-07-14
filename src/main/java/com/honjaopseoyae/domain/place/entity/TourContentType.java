package com.honjaopseoyae.domain.place.entity;

import java.util.Arrays;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TourContentType {

	TOURIST_ATTRACTION("12", "NA"),
	CULTURAL_FACILITY("14", "VE"),
	FESTIVAL("15", "EV"),
	TRAVEL_COURSE("25", "TC"),
	LEISURE_SPORTS("28", "LS"),
	ACCOMMODATION("32", "AC"),
	SHOPPING("38", "SH"),
	RESTAURANT("39", "FD");

	private final String contentTypeId;
	private final String lclsSystm1;

	public static String toLclsSystm1(String contentTypeId) {
		return Arrays.stream(values())
			.filter(type -> type.contentTypeId.equals(contentTypeId))
			.findFirst()
			.map(TourContentType::getLclsSystm1)
			.orElseThrow(() ->
				new IllegalArgumentException(
					"지원하지 않는 contentTypeId: " + contentTypeId
				)
			);
	}
}