package com.honjaopseoyae.config;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.honjaopseoyae.domain.course.dto.response.KakaoDirectionsResponseDto;

import reactor.core.publisher.Mono;

@Component
public class KakaoMobilityClient {

	private final WebClient kakaoMobilityWebClient;

	private static final Duration TIMEOUT = Duration.ofSeconds(3);

	public KakaoMobilityClient(@Qualifier("kakaoMobilityWebClient") WebClient kakaoMobilityWebClient) {
		this.kakaoMobilityWebClient = kakaoMobilityWebClient;
	}

	/**
	 * non-blocking 버전. 여러 구간을 동시에 조회할 때 사용 (Flux.merge로 병렬 처리).
	 * 실패/타임아웃 시 Mono.empty()로 흘려보내고, 호출부에서 fallback 처리.
	 */
	public Mono<RouteSummary> getRouteSummaryAsync(
		double originX,
		double originY,
		double destinationX,
		double destinationY
	) {
		return kakaoMobilityWebClient.get()
			.uri(uriBuilder -> uriBuilder
				.path("/v1/directions")
				.queryParam("origin", originX + "," + originY)
				.queryParam("destination", destinationX + "," + destinationY)
				.queryParam("summary", true)
				.build())
			.retrieve()
			.bodyToMono(KakaoDirectionsResponseDto.class)
			.timeout(TIMEOUT)
			.map(this::extractSummary)
			.onErrorResume(e -> { e.printStackTrace();
				return Mono.empty();
			});
	}

	/** 단건 동기 호출이 필요한 다른 곳에서 쓰던 기존 메서드는 유지 */
	public RouteSummary getRouteSummary(
		double originX,
		double originY,
		double destinationX,
		double destinationY
	) {
		return getRouteSummaryAsync(originX, originY, destinationX, destinationY)
			.blockOptional(TIMEOUT)
			.orElse(null);
	}

	private RouteSummary extractSummary(KakaoDirectionsResponseDto response) {
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
}
