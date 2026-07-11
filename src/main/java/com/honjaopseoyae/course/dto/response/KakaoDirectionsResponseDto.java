package com.honjaopseoyae.course.dto.response;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KakaoDirectionsResponseDto {
	private List<Route> routes;

	@Getter
	@NoArgsConstructor
	public static class Route {
		private Summary summary;
	}

	@Getter
	@NoArgsConstructor
	public static class Summary {
		private int distance;
		private int duration;
	}
}
