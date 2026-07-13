package com.honjaopseoyae.domain.place.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DetailAccessibilityDto {
	private String contentid;           // 콘텐츠 ID

	// [지체장애 / 휠체어 관련]
	private String wheelchair;          // 휠체어 대여 여부
	private String exit;                // 주출입구 (경사로 등)
	private String elevator;            // 엘리베이터 유무
	private String restroom;            // 장애인 화장실 유무
	private String parking;             // 장애인 주차구역 유무
	private String route;               // 이동로 구조 (단차 여부 등)
	private String ticketoffice;        // 매표소 접근성

	// [시각장애 관련]
	private String braileblock;         // 점자블록 유무
	private String helpdog;             // 보조견 동반 가능 여부
	private String guidehuman;          // 안내 요원 배치 여부
	private String audioguide;          // 오디오 가이드 유무
	private String bigprint;            // 큰 활자 안내서 유무
	private String brailepromotion;     // 점자 홍보물 유무
	private String guidesystem;         // 유도 가이드 시스템
	private String blindhandicapetc;    // 시각장애기타 상세

	// [청각장애 관련]
	private String signguide;           // 수어 안내 여부
	private String videoguide;          // 영상 가이드 유무
	private String hearingroom;         // 청각 객실 유무
	private String hearinghandicapetc;  // 청각장애기타 상세

	// [영유아 / 가족 동반 관련]
	private String stroller;            // 유모차 대여 여부
	private String lactationroom;       // 수유실 유무
	private String babysparechair;      // 유아용 보조의자 유무
	private String infantsfamilyetc;    // 영유아패밀리기타 상세

	// [기타 시설]
	private String auditorium;          // 관람석 유무
	private String room;                // 장애인 전용 객실 유무
	private String handicapetc;         // 기타 장애인 편의시설 상세
	private String publictransport;     // 대중교통 접근성
	private String promotion;           // 홍보물 정보
}