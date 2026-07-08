package com.honjaopseoyae.domain.place.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tour_place")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 스펙을 위한 기본 생성자
public class TourPlace {

	@Id
	@Column(name = "content_id", length = 20, nullable = false)
	private String contentId;           // 콘텐츠 ID (기본키 설정)

	@Column(name = "content_type_id", length = 10)
	private String contentTypeId;       // 관광타입 ID (예: 12=관광지 등)

	@Column(name = "title", length = 255)
	private String title;               // 콘텐츠 제목 (관광지명 등)

	@Column(name = "created_time", length = 20)
	private String createdTime;         // 등록일시 (공공 API 포맷에 맞춤)

	@Column(name = "modified_time", length = 20)
	private String modifiedTime;        // 수정일시

	// [연락처 및 주소 정보]
	@Column(name = "tel", length = 50)
	private String tel;                 // 전화번호

	@Column(name = "zipcode", length = 10)
	private String zipcode;             // 우편번호

	@Column(name = "addr1", length = 255)
	private String addr1;               // 주소 (기본 주소)

	@Column(name = "addr2", length = 255)
	private String addr2;               // 상세 주소

	// [지역 및 카테고리 분류]
	@Column(name = "area_code", length = 10)
	private String areaCode;            // 지역 코드

	@Column(name = "sigungu_code", length = 10)
	private String sigunguCode;         // 시군구 코드

	@Column(name = "cat1", length = 10)
	private String cat1;                // 대분류 코드

	@Column(name = "cat2", length = 10)
	private String cat2;                // 중분류 코드

	@Column(name = "cat3", length = 10)
	private String cat3;                // 소분류 코드

	// [위치 및 지도 좌표]
	@Column(name = "map_x", length = 30)
	private String mapX;                // GPS X좌표 (경도)

	@Column(name = "map_y", length = 30)
	private String mapY;                // GPS Y좌표 (위도)

	@Column(name = "mlevel", length = 5)
	private String mLevel;              // 지도 축척 레벨

	// [이미지 및 미디어 정보]
	@Column(name = "first_image", length = 500)
	private String firstImage;          // 대표 이미지 (원본, URL이 길 수 있어 length 확장)

	@Column(name = "first_image2", length = 500)
	private String firstImage2;         // 대표 이미지 (썸네일)

	@Column(name = "cpyrht_div_cd", length = 10)
	private String cpyrhtDivCd;         // 저작권 유형 코드

	// [법정동 및 지자체 시스템 코드]
	@Column(name = "l_dong_regn_cd", length = 20)
	private String lDongRegnCd;         // 법정동 지역 코드

	@Column(name = "l_dong_signgu_cd", length = 20)
	private String lDongSignguCd;       // 법정동 시군구 코드

	@Column(name = "lcls_systm1", length = 50)
	private String lclsSystm1;          // 지자체 시스템 코드 1

	@Column(name = "lcls_systm2", length = 50)
	private String lclsSystm2;          // 지자체 시스템 코드 2

	@Column(name = "lcls_systm3", length = 50)
	private String lclsSystm3;          // 지자체 시스템 코드 3

	@Builder
	public TourPlace(String contentId, String contentTypeId, String title, String createdTime, String modifiedTime,
		String tel, String zipcode, String addr1, String addr2, String areaCode, String sigunguCode,
		String cat1, String cat2, String cat3, String mapX, String mapY, String mLevel,
		String firstImage, String firstImage2, String cpyrhtDivCd,
		String lDongRegnCd, String lDongSignguCd, String lclsSystm1, String lclsSystm2, String lclsSystm3) {
		this.contentId = contentId;
		this.contentTypeId = contentTypeId;
		this.title = title;
		this.createdTime = createdTime;
		this.modifiedTime = modifiedTime;
		this.tel = tel;
		this.zipcode = zipcode;
		this.addr1 = addr1;
		this.addr2 = addr2;
		this.areaCode = areaCode;
		this.sigunguCode = sigunguCode;
		this.cat1 = cat1;
		this.cat2 = cat2;
		this.cat3 = cat3;
		this.mapX = mapX;
		this.mapY = mapY;
		this.mLevel = mLevel;
		this.firstImage = firstImage;
		this.firstImage2 = firstImage2;
		this.cpyrhtDivCd = cpyrhtDivCd;
		this.lDongRegnCd = lDongRegnCd;
		this.lDongSignguCd = lDongSignguCd;
		this.lclsSystm1 = lclsSystm1;
		this.lclsSystm2 = lclsSystm2;
		this.lclsSystm3 = lclsSystm3;
	}
}