package com.honjaopseoyae;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import com.honjaopseoyae.place.dto.common.TourApiCommonResponse;
import com.honjaopseoyae.place.dto.response.DetailAccessibilityDto;
import com.honjaopseoyae.place.dto.response.TourPlaceDto;
import com.honjaopseoyae.place.service.TourApiService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class TourApiTest {

	@Autowired
	private TourApiService tourApiService;

	@Test
	void callTourApiListTest() {
		// 1. 공통 껍데기 + 목록 알맹이 타입으로 리턴 타입을 변경합니다.
		TourApiCommonResponse<List<TourPlaceDto>> result = tourApiService.getBarrierFreePlaceFromTourAPI();

		System.out.println("====== Tour API List Test Result ======");
		System.out.println(result);
		System.out.println("=======================================");

		// 2. 외부 API 응답 및 결과 코드 검증
		assertThat(result).isNotNull();
		assertThat(result.getResponse()).isNotNull();
		assertThat(result.getResponse().getHeader()).isNotNull();
		assertThat(result.getResponse().getHeader().getResultCode()).isEqualTo("0000");

		// 추가 데이터 검증: 가져온 리스트가 비어있지 않은지 체크
		if (result.getResponse().getBody() != null && result.getResponse().getBody().getItems() != null) {
			List<TourPlaceDto> items = (List<TourPlaceDto>) result.getResponse().getBody().getItems().getItem();
			assertThat(items).isNotEmpty();
			System.out.println("가져온 첫 번째 관광지 제목: " + items.get(0).getTitle());
		}
	}

	@Test
	void callTBarrierFreeDetailTest() {
		//곽재해수욕장
		Long testContentId = 127870L;

		TourApiCommonResponse<List<DetailAccessibilityDto>> result = tourApiService.getBarrierFreeInfo(testContentId);

		System.out.println("====== Tour API Detail Test Result ======");
		System.out.println(result);
		System.out.println("=========================================");

		assertThat(result).isNotNull();
		assertThat(result.getResponse()).isNotNull();
		assertThat(result.getResponse().getHeader()).isNotNull();
		assertThat(result.getResponse().getHeader().getResultCode()).isEqualTo("0000");

		if (result.getResponse().getBody() != null && result.getResponse().getBody().getItems() != null) {
			List<DetailAccessibilityDto> items = (List<DetailAccessibilityDto>) result.getResponse().getBody().getItems().getItem();

			assertThat(items).isNotEmpty();

			DetailAccessibilityDto firstItem = items.get(0);

			assertThat(firstItem).isNotNull();
			assertThat(firstItem.getContentid()).isEqualTo(testContentId.toString());

			System.out.println("wheelchair: " + firstItem.getWheelchair());
			System.out.println("elevator: " + firstItem.getElevator());
		}
	}

	// @Test
	// void callTourApiDetailTest() {
	// 	// [추가] 새로 만든 무장애 상세 정보 단건 조회 테스트
	// 	// 테스트용 contentId 입력 (실제 공공데이터에 존재하는 ID를 넣으시면 정확히 테스트 가능합니다)
	// 	Long testContentId = 2894503L;
	//
	// 	AccessibilityItemDto result = tourApiService.getBarrierFreeInfo(testContentId);
	//
	// 	System.out.println("====== Tour API Detail Test Result ======");
	// 	System.out.println(result);
	// 	System.out.println("=========================================");
	//
	// 	// 단건 조회 알맹이가 정상적으로 수신되었는지 검증
	// 	if (result != null) {
	// 		assertThat(result).isNotNull();
	// 		assertThat(result.getContentid()).isEqualTo(testContentId.toString());
	// 		System.out.println("휠체어 대여 여부 (wheelchair): " + result.getWheelchair());
	// 		System.out.println("엘리베이터 유무 (elevator): " + result.getElevator());
	// 	} else {
	// 		System.out.println("해당 ID의 무장애 상세 정보가 없거나 에러가 발생했습니다.");
	// 	}
	// }
}
