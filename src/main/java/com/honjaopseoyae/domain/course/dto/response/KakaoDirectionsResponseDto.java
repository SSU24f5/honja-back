package com.honjaopseoyae.domain.course.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoDirectionsResponseDto {
	private List<Route> routes;

	@Getter
	@ToString
	@NoArgsConstructor
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Route {
		private Summary summary;
	}

	@Getter
	@ToString
	@NoArgsConstructor
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Summary {
		private int distance;
		private int duration;
	}
}
