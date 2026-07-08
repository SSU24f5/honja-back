package com.honjaopseoyae.place.dto.common;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TourApiCommonResponse<T> { // <--- 서비스 안에서 돌려쓸 제네릭 클래스

	private TourResponse<T> response;

	@Getter
	@Setter
	@ToString
	public static class TourResponse<T> {
		private Header header;
		private Body<T> body;
	}

	@Getter
	@Setter
	@ToString
	public static class Header {
		private String resultCode; // 결과코드 (성공 시 "0000")
		private String resultMsg;  // 결과메시지 (성공 시 "OK")
	}

	@Getter
	@Setter
	@ToString
	public static class Body<T> {
		private Integer numOfRows;  // 한 페이지 결과 수
		private Integer pageNo;     // 페이지 번호
		private Integer totalCount; // 데이터 총 개수
		private Items<T> items;
	}

	@Getter
	@Setter
	@ToString
	public static class Items<T> {
		// 단건 조회일 때는 일반 DTO 객체가, 다건 조회일 때는 List<DTO>가 이 T 자리에 들어갑니다.
		private T item;
	}
}