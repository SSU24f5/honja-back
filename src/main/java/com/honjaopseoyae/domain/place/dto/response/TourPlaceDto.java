package com.honjaopseoyae.domain.place.dto.response;

import com.honjaopseoyae.domain.place.entity.Place;

import com.honjaopseoyae.domain.place.util.IndoorOutdoorClassifier;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TourPlaceDto {
	private String contentid;           // 콘텐츠 ID
	private String contenttypeid;       // 관광타입 ID (예: 12=관광지, 14=문화시설, 32=숙박, 39=음식점 등)
	private String title;               // 콘텐츠 제목 (관광지명, 식당명 등)
	private String createdtime;         // 등록일시
	private String modifiedtime;        // 수정일시

	// [연락처 및 주소 정보]
	private String tel;                 // 전화번호
	private String zipcode;             // 우편번호
	private String addr1;               // 주소 (기본 주소)
	private String addr2;               // 상세 주소

	// [지역 및 카테고리 분류]
	private String areacode;            // 지역 코드
	private String sigungucode;         // 시군구 코드
	private String cat1;                // 대분류 코드
	private String cat2;                // 중분류 코드
	private String cat3;                // 소분류 코드

	// [위치 및 지도 좌표]
	private String mapx;                // GPS X좌표 (경도, Longitude)
	private String mapy;                // GPS Y좌표 (위도, Latitude)
	private String mlevel;              // 지도 축척 레벨

	// [이미지 및 미디어 정보]
	private String firstimage;          // 대표 이미지 (원본, 500x333)
	private String firstimage2;         // 대표 이미지 (썸네일, 150x100)
	private String cpyrhtDivCd;         // 저작권 유형 코드

	// [법정동 및 지자체 시스템 코드]
	private String lDongRegnCd;         // 법정동 지역 코드
	private String lDongSignguCd;       // 법정동 시군구 코드
	private String lclsSystm1;          // 지자체 시스템 코드 1
	private String lclsSystm2;          // 지자체 시스템 코드 2
	private String lclsSystm3;          // 지자체 시스템 코드 3

	public Place toEntity(boolean isPetPlace, boolean isBarrierFree, IndoorOutdoorClassifier classifier) {
		return Place.builder()
			.contentId(contentid)
			.contentType(parseContentType(contenttypeid))
			.mapx(parseDouble(mapx))
			.mapy(parseDouble(mapy))
			.image(firstimage)
			.petPlace(isPetPlace)
			.barrierFree(isBarrierFree)
            .title(title)
            .indoor(classifier.isIndoor(title))
			.build();
	}

	private double parseDouble(String value) {
		if (value == null || value.isBlank()) {
			return 0.0;
		}

		try {
			return Double.parseDouble(value.trim());
		} catch (NumberFormatException e) {
			return 0.0;
		}
	}

	private Integer parseContentType(String value) {
		if (value == null || value.isBlank()) {
			return null;
		}

		try {
			return Integer.parseInt(value.trim());
		} catch (NumberFormatException e) {
			return null;
		}
	}
}
