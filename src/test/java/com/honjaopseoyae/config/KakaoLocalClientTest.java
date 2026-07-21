package com.honjaopseoyae.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

import static org.assertj.core.api.Assertions.assertThat;

class KakaoLocalClientTest {

	private KakaoLocalClient kakaoLocalClient;

	@BeforeEach
	void setUp() {
		String baseUrl = "https://dapi.kakao.com";
		String restApiKey = "75ad8ef966f3cdd834c5282ecf2b9450";

		WebClient webClient = WebClient.builder()
			.baseUrl(baseUrl)
			.defaultHeader(HttpHeaders.AUTHORIZATION, "KakaoAK " + restApiKey)
			.build();

		kakaoLocalClient = new KakaoLocalClient(webClient);
	}

	@Test
	void test() {
		// 서울시청 부근 좌표 (경도 x, 위도 y)
		double mapx = 126.9784;
		double mapy = 37.5665;

		KakaoLocalClient.RoadCoordinate road =
			kakaoLocalClient.getNearestRoad(mapx, mapy);

		System.out.println("==========================");

		if (road == null) {
			System.out.println("road = null (도로명 주소가 없는 위치이거나 API 응답에 road_address가 없음)");
		} else {
			System.out.println("Nearest Road Coordinate x = " + road.x());
			System.out.println("Nearest Road Coordinate y = " + road.y());
		}

		System.out.println("==========================");

		// 주소 변환 결과 검증 (지번에 따라 null일 수도 있거나 좌표가 있을 수 있음)
		System.out.println("API 호출 완료, road 객체: " + road);
	}
}