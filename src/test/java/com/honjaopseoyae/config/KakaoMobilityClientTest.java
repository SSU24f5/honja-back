package com.honjaopseoyae.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

class KakaoMobilityClientTest {

	private KakaoMobilityClient kakaoMobilityClient;

	@BeforeEach
	void setUp() {
		String baseUrl = "https://apis-navi.kakaomobility.com";
		String restApiKey = "75ad8ef966f3cdd834c5282ecf2b9450";

		WebClient webClient = WebClient.builder()
			.baseUrl(baseUrl)
			.defaultHeader(HttpHeaders.AUTHORIZATION, "KakaoAK " + restApiKey)
			.build();

		kakaoMobilityClient = new KakaoMobilityClient(webClient);
	}

	// @Test
	// void getRouteSummaryTest() {
	// 	// 서울시청 경도, 위도 -> 서울역 경도, 위도
	// 	double originX = 126.9415156012;
	// 	double originY = 33.4581111174;
	//
	// 	double destinationX = 126.9262142246;
	// 	double destinationY = 33.4548306763;
	//
	//
	// 	KakaoMobilityClient.RouteSummary summary = kakaoMobilityClient.getRouteSummary(originX, originY, destinationX, destinationY);
	//
	// 	System.out.println("====== Kakao Mobility RouteSummary Test Result ======");
	// 	System.out.println("Summary result: " + summary);
	// 	if (summary != null) {
	// 		System.out.println("Distance: " + summary.distanceMeters() + " meters (" + String.format("%.1f", summary.distanceMeters()/1000.0) + "km)");
	// 		System.out.println("Duration: " + summary.durationSeconds() + " seconds (" + (int)Math.ceil(summary.durationSeconds()/60.0) + "분)");
	// 	}
	// 	System.out.println("=====================================================");
	//
	// 	assertThat(summary).isNotNull();
	// 	assertThat(summary.distanceMeters()).isGreaterThan(0);
	// 	assertThat(summary.durationSeconds()).isGreaterThan(0);
	// }

	@Test
	void getRouteSummaryTest() {
		// 성산일출봉
		double originX = 126.9415156012;
		double originY = 33.4581111174;

		// 성산포JC공원
		double destinationX = 126.9262142246;
		double destinationY = 33.4548306763;

		kakaoMobilityClient.getRouteSummary(
			originX,
			originY,
			destinationX,
			destinationY
		);
	}
}
