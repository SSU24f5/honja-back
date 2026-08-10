package com.honjaopseoyae.config;


import com.honjaopseoyae.domain.course.dto.response.KakaoCoord2AddressResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class KakaoLocalClient {

	@Qualifier("kakaoLocalWebClient")
	private final WebClient kakaoLocalWebClient;

	public RoadCoordinate getNearestRoad(double mapx, double mapy) {

		KakaoCoord2AddressResponseDto response = kakaoLocalWebClient.get()
			.uri(uriBuilder -> uriBuilder
				.path("/v2/local/geo/coord2address.json")
				.queryParam("x", mapx)
				.queryParam("y", mapy)
				.build())
			.retrieve()
			.bodyToMono(KakaoCoord2AddressResponseDto.class)
			.block();

		log.info("response = {}", response);

		if (response == null) {
			log.info("response is null");
			return null;
		}

		if (response.getDocuments() == null || response.getDocuments().isEmpty()) {
			log.info("documents is empty");
			return null;
		}

		KakaoCoord2AddressResponseDto.Document document = response.getDocuments().get(0);

		log.info("document = {}", document);

		KakaoCoord2AddressResponseDto.RoadAddress roadAddress = document.getRoadAddress();

		if (roadAddress == null) {
			log.info("roadAddress is null");
			return null;
		}

		log.info("roadAddress = {}", roadAddress);

		String xStr = roadAddress.getX();
		String yStr = roadAddress.getY();
		if (xStr == null || xStr.isBlank() || yStr == null || yStr.isBlank()) {
			log.info("roadAddress x or y is null/blank");
			return null;
		}

		return new RoadCoordinate(
			Double.parseDouble(xStr),
			Double.parseDouble(yStr)
		);
	}

	public RoadCoordinate searchKeyword(String query) {
		if (query == null || query.isBlank()) {
			return null;
		}

		try {
			com.honjaopseoyae.domain.course.dto.response.KakaoSearchKeywordResponseDto response = kakaoLocalWebClient.get()
				.uri(uriBuilder -> uriBuilder
					.path("/v2/local/search/keyword.json")
					.queryParam("query", query)
					.build())
				.retrieve()
				.bodyToMono(com.honjaopseoyae.domain.course.dto.response.KakaoSearchKeywordResponseDto.class)
				.onErrorResume(e -> {
					log.warn("Kakao Local searchKeyword failed for query: {}", query, e);
					return reactor.core.publisher.Mono.empty();
				})
				.block();

			if (response == null || response.getDocuments() == null || response.getDocuments().isEmpty()) {
				log.info("searchKeyword empty result for query: {}", query);
				return null;
			}

			com.honjaopseoyae.domain.course.dto.response.KakaoSearchKeywordResponseDto.Document document = response.getDocuments().get(0);
			String xStr = document.getX();
			String yStr = document.getY();

			if (xStr == null || xStr.isBlank() || yStr == null || yStr.isBlank()) {
				return null;
			}

			return new RoadCoordinate(
				Double.parseDouble(xStr),
				Double.parseDouble(yStr)
			);
		} catch (Exception e) {
			log.error("Exception in searchKeyword for query: {}", query, e);
			return null;
		}
	}

	public record RoadCoordinate(
		double x,
		double y
	) {
	}
}