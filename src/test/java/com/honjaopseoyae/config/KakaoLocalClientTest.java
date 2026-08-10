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

	@Test
	void searchKeywordTest() {
		String query = "성산일출봉";
		KakaoLocalClient.RoadCoordinate coord = kakaoLocalClient.searchKeyword(query);

		System.out.println("====== Kakao Local Keyword Search Test Result ======");
		System.out.println("Query: " + query);
		if (coord != null) {
			System.out.println("Refined Kakao x (Longitude): " + coord.x());
			System.out.println("Refined Kakao y (Latitude): " + coord.y());
		} else {
			System.out.println("Search result: null");
		}
		System.out.println("===================================================");

		assertThat(coord).isNotNull();
		assertThat(coord.x()).isGreaterThan(0);
		assertThat(coord.y()).isGreaterThan(0);
	}

	@Test
	void jejuPlacesRouteTest() {
		String restApiKey = "75ad8ef966f3cdd834c5282ecf2b9450";

		// 1. Kakao Mobility Client 생성
		WebClient mobilityWebClient = WebClient.builder()
			.baseUrl("https://apis-navi.kakaomobility.com")
			.defaultHeader(HttpHeaders.AUTHORIZATION, "KakaoAK " + restApiKey)
			.build();
		KakaoMobilityClient kakaoMobilityClient = new KakaoMobilityClient(mobilityWebClient);

		// 2. 실제 제주도 장소 키워드 검색 (성산일출봉 & 협재해수욕장)
		String place1Name = "성산일출봉";
		String place2Name = "협재해수욕장";

		KakaoLocalClient.RoadCoordinate coord1 = kakaoLocalClient.searchKeyword(place1Name);
		KakaoLocalClient.RoadCoordinate coord2 = kakaoLocalClient.searchKeyword(place2Name);

		System.out.println("================ [제주도 장소 카카오 검색 결과] ================");
		System.out.println(place1Name + " 좌표: " + coord1);
		System.out.println(place2Name + " 좌표: " + coord2);
		System.out.println("===============================================================");

		if (coord1 != null && coord2 != null) {
			// 3. 카카오 모빌리티 API로 장소간 거리 및 소요시간 조회
			KakaoMobilityClient.RouteSummary summary = kakaoMobilityClient.getRouteSummary(
				coord1.x(), coord1.y(),
				coord2.x(), coord2.y()
			);

			System.out.println("================ [카카오 모빌리티 길찾기 결과] ================");
			System.out.println("출발지: " + place1Name + " (x=" + coord1.x() + ", y=" + coord1.y() + ")");
			System.out.println("도착지: " + place2Name + " (x=" + coord2.x() + ", y=" + coord2.y() + ")");

			if (summary != null) {
				double distanceKm = summary.distanceMeters() / 1000.0;
				int durationMinutes = (int) Math.ceil(summary.durationSeconds() / 60.0);

				System.out.println("이동 거리: " + summary.distanceMeters() + "m (" + String.format("%.1f", distanceKm) + " km)");
				System.out.println("소요 시간: " + summary.durationSeconds() + "초 (" + durationMinutes + " 분)");
			} else {
				System.out.println("Route Summary is NULL (길찾기 실패)");
			}
			System.out.println("===============================================================");
		}
	}

	@Test
	void userFourPlacesRouteTest() {
		String restApiKey = "75ad8ef966f3cdd834c5282ecf2b9450";
		WebClient mobilityWebClient = WebClient.builder()
			.baseUrl("https://apis-navi.kakaomobility.com")
			.defaultHeader(HttpHeaders.AUTHORIZATION, "KakaoAK " + restApiKey)
			.build();
		KakaoMobilityClient kakaoMobilityClient = new KakaoMobilityClient(mobilityWebClient);

		// 사용자 요청의 4개 장소
		String title1 = "첫번째 숙소";
		double userX1 = 126.93081520961395, userY1 = 33.46044086741585;

		String title2 = "제주 성산항";
		double userX2 = 126.9299218287, userY2 = 33.4725482235;

		String title3 = "오조마을";
		double userX3 = 126.9229030466, userY3 = 33.4701955919;

		String title4 = "첫번째 숙소";
		double userX4 = 126.93081520961395, userY4 = 33.46044086741585;

		System.out.println("================ [1. 키워드 검색 결과 비교] ================");
		KakaoLocalClient.RoadCoordinate search1 = kakaoLocalClient.searchKeyword(title1);
		KakaoLocalClient.RoadCoordinate search2 = kakaoLocalClient.searchKeyword(title2);
		KakaoLocalClient.RoadCoordinate search3 = kakaoLocalClient.searchKeyword(title3);

		System.out.println("1. '" + title1 + "' -> 유저입력(" + userX1 + ", " + userY1 + ") vs 카카오검색(" + search1 + ")");
		System.out.println("2. '" + title2 + "' -> 유저입력(" + userX2 + ", " + userY2 + ") vs 카카오검색(" + search2 + ")");
		System.out.println("3. '" + title3 + "' -> 유저입력(" + userX3 + ", " + userY3 + ") vs 카카오검색(" + search3 + ")");
		System.out.println("===============================================================");

		System.out.println("================ [2. 유저 입력 원본 좌표로 길찾기 결과] ================");
		KakaoMobilityClient.RouteSummary s1_2_user = kakaoMobilityClient.getRouteSummary(userX1, userY1, userX2, userY2);
		KakaoMobilityClient.RouteSummary s2_3_user = kakaoMobilityClient.getRouteSummary(userX2, userY2, userX3, userY3);
		KakaoMobilityClient.RouteSummary s3_4_user = kakaoMobilityClient.getRouteSummary(userX3, userY3, userX4, userY4);

		System.out.println("1->2 (첫번째숙소->성산항): " + (s1_2_user != null ? s1_2_user.distanceMeters() + "m / " + s1_2_user.durationSeconds() + "초" : "NULL"));
		System.out.println("2->3 (성산항->오조마을): " + (s2_3_user != null ? s2_3_user.distanceMeters() + "m / " + s2_3_user.durationSeconds() + "초" : "NULL"));
		System.out.println("3->4 (오조마을->첫번째숙소): " + (s3_4_user != null ? s3_4_user.distanceMeters() + "m / " + s3_4_user.durationSeconds() + "초" : "NULL"));
		System.out.println("===============================================================");

		if (search1 != null && search2 != null && search3 != null) {
			System.out.println("================ [3. 카카오 검색 정제 좌표로 길찾기 결과] ================");
			KakaoMobilityClient.RouteSummary s1_2_kakao = kakaoMobilityClient.getRouteSummary(search1.x(), search1.y(), search2.x(), search2.y());
			KakaoMobilityClient.RouteSummary s2_3_kakao = kakaoMobilityClient.getRouteSummary(search2.x(), search2.y(), search3.x(), search3.y());
			KakaoMobilityClient.RouteSummary s3_4_kakao = kakaoMobilityClient.getRouteSummary(search3.x(), search3.y(), search1.x(), search1.y());

			System.out.println("1->2 (첫번째숙소->성산항): " + (s1_2_kakao != null ? s1_2_kakao.distanceMeters() + "m / " + s1_2_kakao.durationSeconds() + "초" : "NULL"));
			System.out.println("2->3 (성산항->오조마을): " + (s2_3_kakao != null ? s2_3_kakao.distanceMeters() + "m / " + s2_3_kakao.durationSeconds() + "초" : "NULL"));
			System.out.println("3->4 (오조마을->첫번째숙소): " + (s3_4_kakao != null ? s3_4_kakao.distanceMeters() + "m / " + s3_4_kakao.durationSeconds() + "초" : "NULL"));
			System.out.println("===============================================================");
		}
	}
}