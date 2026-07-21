package com.honjaopseoyae.domain.course.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@NoArgsConstructor
public class KakaoCoord2AddressResponseDto {

	private List<Document> documents;

	@Getter
	@ToString
	@NoArgsConstructor
	public static class Document {

		@JsonProperty("road_address")
		private RoadAddress roadAddress;
	}

	@Getter
	@ToString
	@NoArgsConstructor
	public static class RoadAddress {

		private String x;
		private String y;
	}
}