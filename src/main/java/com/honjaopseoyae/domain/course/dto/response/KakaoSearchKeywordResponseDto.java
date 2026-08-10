package com.honjaopseoyae.domain.course.dto.response;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class KakaoSearchKeywordResponseDto {

	private List<Document> documents;

	@Getter
	@ToString
	@NoArgsConstructor
	public static class Document {
		private String id;
		private String place_name;
		private String address_name;
		private String road_address_name;
		private String x; // 경도 (Longitude)
		private String y; // 위도 (Latitude)
	}
}
