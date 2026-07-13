package com.honjaopseoyae.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.honjaopseoyae.domain.course.dto.response.KakaoDirectionsResponseDto;

@SpringBootTest
class KakaoMobilityClientTest {

	@Autowired
	private KakaoMobilityClient kakaoMobilityClient;

	@Test
	void getDirectionsTest() {
		// 서울시청 경도, 위도
		double originX = 126.9784;
		double originY = 37.5665;
		// 서울역 경도, 위도
		double destinationX = 126.9726;
		double destinationY = 37.5547;

		KakaoDirectionsResponseDto response = kakaoMobilityClient.getDirections(originX, originY, destinationX, destinationY);

		assertThat(response).isNotNull();
		assertThat(response.getRoutes()).isNotEmpty();
		KakaoDirectionsResponseDto.Summary summary = response.getRoutes().get(0).getSummary();
		assertThat(summary).isNotNull();
		
		System.out.println("====== Kakao Mobility Directions Test Result ======");
		System.out.println("Distance: " + summary.getDistance() + " meters");
		System.out.println("Duration: " + summary.getDuration() + " seconds");
		System.out.println("==================================================");
	}

	@Test
	void getRouteSummaryTest() {
		double originX = 126.9784;
		double originY = 37.5665;
		double destinationX = 126.9726;
		double destinationY = 37.5547;

		KakaoMobilityClient.RouteSummary summary = kakaoMobilityClient.getRouteSummary(originX, originY, destinationX, destinationY);

		assertThat(summary).isNotNull();
		assertThat(summary.distanceMeters()).isGreaterThan(0);
		assertThat(summary.durationSeconds()).isGreaterThan(0);

		System.out.println("====== Kakao Mobility RouteSummary Test Result ======");
		System.out.println("Distance: " + summary.distanceMeters() + " meters");
		System.out.println("Duration: " + summary.durationSeconds() + " seconds");
		System.out.println("=====================================================");
	}
}
