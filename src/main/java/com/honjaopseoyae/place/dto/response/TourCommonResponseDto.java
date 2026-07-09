package com.honjaopseoyae.place.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TourCommonResponseDto {
	// [기본 고유 정보]
	private String contentid;           // 콘텐츠 ID
	private String contenttypeid;       // 관광타입 ID (예: 12=관광지, 14=문화시설, 32=숙박, 39=음식점 등)
	private String title;               // 콘텐츠 제목 (관광지명, 식당명 등)

	// [연락처 및 주소 정보]
	private String tel;
	private String addr1;               // 주소 (기본 주소)
	private String addr2;               // 상세 주소

	// [위치 및 지도 좌표]
	private String mapx;                // GPS X좌표 (경도, Longitude)
	private String mapy;                // GPS Y좌표 (위도, Latitude)
	private String mlevel;              // 지도 축척 레벨

	// [이미지 및 미디어 정보]
	private String firstimage;          // 대표 이미지 (원본, 500x333)
	private String firstimage2;         // 대표 이미지 (썸네일, 150x100)
	private String cpyrhtDivCd;         // 저작권 유형 코드

}
