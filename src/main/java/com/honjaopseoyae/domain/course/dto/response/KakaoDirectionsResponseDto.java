package com.honjaopseoyae.domain.course.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KakaoDirectionsResponseDto {
	private List<Route> routes;

	@Getter
	@NoArgsConstructor
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Route {
		@JsonProperty("result_code")
		private int resultCode;

		@JsonProperty("result_msg")
		private String resultMsg;

		private Summary summary;
		private List<Section> sections;
	}

	@Getter
	@NoArgsConstructor
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Summary {
		private int distance;
		private int duration;
	}

	@Getter
	@NoArgsConstructor
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Section {
		private int distance;
		private int duration;
		private List<Road> roads;
		private List<Guide> guides;
	}

	@Getter
	@NoArgsConstructor
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Road {
		private String name;
		private int distance;
		private int duration;
		private List<Double> vertexes;
	}

	@Getter
	@NoArgsConstructor
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Guide {
		private String name;
		private double x;   // 경도
		private double y;   // 위도
		private int distance;
		private int duration;
		private String guidance;
	}
}
