package com.honjaopseoyae.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.honjaopseoyae.domain.course.dto.response.KakaoDirectionsResponseDto;

@Component
public class KakaoMobilityClient {

	private final WebClient kakaoMobilityWebClient;

	public KakaoMobilityClient(@Qualifier("kakaoMobilityWebClient") WebClient kakaoMobilityWebClient) {
		this.kakaoMobilityWebClient = kakaoMobilityWebClient;
	}

	public KakaoDirectionsResponseDto getDirections(
		double originX,
		double originY,
		double destinationX,
		double destinationY
	) {
		return kakaoMobilityWebClient.get()
			.uri(uriBuilder -> uriBuilder
				.path("/v1/directions")
				.queryParam("origin", originX + "," + originY)
				.queryParam(
					"destination",
					destinationX + "," + destinationY
				)
				.queryParam("summary", true)
				.build())
			.retrieve()
			.bodyToMono(KakaoDirectionsResponseDto.class)
			.block();
	}

	public RouteSummary getRouteSummary(
		double originX,
		double originY,
		double destinationX,
		double destinationY
	) {
		KakaoDirectionsResponseDto response = getDirections(originX, originY, destinationX, destinationY);
		if (response == null || response.getRoutes() == null || response.getRoutes().isEmpty()) {
			return null;
		}

		KakaoDirectionsResponseDto.Summary summary = response.getRoutes().get(0).getSummary();
		if (summary == null) {
			return null;
		}

		return new RouteSummary(summary.getDistance(), summary.getDuration());
	}

	public record RouteSummary(int distanceMeters, int durationSeconds) {
	}

	public KakaoDirectionsResponseDto getDirectionsWithPath(
			double originX,
			double originY,
			double destinationX,
			double destinationY,
			String priority
	) {
		return kakaoMobilityWebClient.get()
				.uri(uriBuilder -> uriBuilder
						.path("/v1/directions")
						.queryParam("origin", originX + "," + originY)
						.queryParam("destination", destinationX + "," + destinationY)
						.queryParam("priority", priority)
						.queryParam("summary", false)
						.queryParam("road_details", false)
						.queryParam("alternatives", false)
						.build())
				.retrieve()
				.bodyToMono(KakaoDirectionsResponseDto.class)
				.block();
	}
}
