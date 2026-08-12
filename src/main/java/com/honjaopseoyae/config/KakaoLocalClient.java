package com.honjaopseoyae.config;


import com.honjaopseoyae.domain.course.dto.response.KakaoCoord2AddressResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
public class KakaoLocalClient {
	private static final String BRACKETED_DESCRIPTION_REGEX =
		"(?s)\\([^()]*\\)|\\[[^\\[\\]]*\\]|\\{[^{}]*\\}|<[^<>]*>"
			+ "|\\x{FF08}[^\\x{FF08}\\x{FF09}]*\\x{FF09}"  // （...）
			+ "|\\x{FF3B}[^\\x{FF3B}\\x{FF3D}]*\\x{FF3D}"  // ［...］
			+ "|\\x{FF5B}[^\\x{FF5B}\\x{FF5D}]*\\x{FF5D}"  // ｛...｝
			+ "|\\x{3008}[^\\x{3008}\\x{3009}]*\\x{3009}"  // 〈...〉
			+ "|\\x{300A}[^\\x{300A}\\x{300B}]*\\x{300B}"  // 《...》
			+ "|\\x{3010}[^\\x{3010}\\x{3011}]*\\x{3011}"; // 【...】

	private final WebClient kakaoLocalWebClient;

	public KakaoLocalClient(
		@Qualifier("kakaoLocalWebClient") WebClient kakaoLocalWebClient
	) {
		this.kakaoLocalWebClient = kakaoLocalWebClient;
	}

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
		String normalizedQuery = normalizeKeyword(query);

		try {
			com.honjaopseoyae.domain.course.dto.response.KakaoSearchKeywordResponseDto response = kakaoLocalWebClient.get()
				.uri(uriBuilder -> uriBuilder
					.path("/v2/local/search/keyword.json")
					.queryParam("query", normalizedQuery)
					.build())
				.retrieve()
				.bodyToMono(com.honjaopseoyae.domain.course.dto.response.KakaoSearchKeywordResponseDto.class)
				.onErrorResume(e -> {
					log.warn("Kakao Local searchKeyword failed. originalQuery={}, normalizedQuery={}",
						query, normalizedQuery, e);
					return reactor.core.publisher.Mono.empty();
				})
				.block();

			if (response == null || response.getDocuments() == null || response.getDocuments().isEmpty()) {
				log.info("Kakao Local searchKeyword empty result. originalQuery={}, normalizedQuery={}",
					query, normalizedQuery);
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
			log.error("Exception in Kakao Local searchKeyword. originalQuery={}, normalizedQuery={}",
				query, normalizedQuery, e);
			return null;
		}
	}

	static String normalizeKeyword(String query) {
		String normalized = query;
		String previous;
		do {
			previous = normalized;
			normalized = normalized.replaceAll(BRACKETED_DESCRIPTION_REGEX, " ");
		} while (!normalized.equals(previous));

		normalized = normalized
			.replaceAll("\\s+", " ")
			.trim();

		return normalized.isBlank() ? query.trim() : normalized;
	}

	public record RoadCoordinate(
		double x,
		double y
	) {
	}
}
