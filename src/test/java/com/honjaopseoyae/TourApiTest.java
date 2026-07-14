package com.honjaopseoyae;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import com.honjaopseoyae.domain.place.dto.common.TourApiCommonResponse;
import com.honjaopseoyae.domain.place.dto.response.DetailAccessibilityDto;
import com.honjaopseoyae.domain.place.dto.response.TourCommonResponseDto;
import com.honjaopseoyae.domain.place.service.LocationService;
import com.honjaopseoyae.domain.place.service.TourApiService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class TourApiTest {

	@Autowired
	private TourApiService tourApiService;

	@Autowired
	private LocationService locationService;

	@Test
	// 반려동물, 무장애 전체 조회 테스트
	void callTourApiListTest() {
		// List<TourCommonResponseDto> result = tourApiService.getBarrierFreePlaceFromTourAPI();
		List<TourCommonResponseDto> result = tourApiService.getPetPlaceFromTourAPI();

		System.out.println("====== Tour API List Test Result ======");
		System.out.println(result);
		System.out.println("=======================================");

		// 2. 외부 API 응답 및 결과 검증
		assertThat(result).isNotNull();
		assertThat(result).isNotEmpty();
		System.out.println("가져온 첫 번째 관광지 제목: " + result.get(0).getTitle());
	}

	@Test
	// 국문 관광정보 전체 조회 테스트
	void callCommonPlaceApiTest() {
		List<TourCommonResponseDto> result = tourApiService.getCommonPlaceFromTourAPI();

		System.out.println("====== Tour API Common Place Test Result ======");
		System.out.println(result);
		System.out.println("==============================================");

		assertThat(result).isNotNull();
		assertThat(result).isNotEmpty();
		System.out.println("가져온 첫 번째 관광지 제목: " + result.get(0).getTitle());
	}

	// @Test
	// 카테고리별 조회 테스트
	// void callPlaceByCategoryApiTest() {
	// 	String testContentTypeId = "12"; // 관광지
	//
	// 	List<TourCommonResponseDto> petResult = locationService.getPetPlaceByCategoryFromTourAPI(testContentTypeId);
	// 	List<TourCommonResponseDto> barrierFreeResult = locationService.getBarrierFreePlaceByCategoryFromTourAPI(testContentTypeId);
	// 	List<TourCommonResponseDto> commonResult = locationService.getCommonPlaceByCategoryFromTourAPI(testContentTypeId);
	//
	// 	System.out.println("====== Tour API Category-based Place Test Result ======");
	// 	System.out.println("Pet Places count: " + petResult.size());
	// 	System.out.println("Barrier Free Places count: " + barrierFreeResult.size());
	// 	System.out.println("Common Places count: " + commonResult.size());
	// 	System.out.println("======================================================");
	//
	// 	assertThat(petResult).isNotNull();
	// 	assertThat(barrierFreeResult).isNotNull();
	// 	assertThat(commonResult).isNotNull();
	// }

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

}
