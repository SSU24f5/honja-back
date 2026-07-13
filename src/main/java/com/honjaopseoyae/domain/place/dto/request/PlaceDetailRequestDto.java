package com.honjaopseoyae.domain.place.dto.request;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class PlaceDetailRequestDto {
	@Builder.Default
	private Integer numOfRows = 1;          // 한 페이지 결과 수 (단건 조하으므로 1)

	@Builder.Default
	private Integer pageNo = 1;             // 페이지 번호

	@Builder.Default
	private String mobileOS = "ETC";        // OS 구분 (IOS, AND, WIN, ETC)

	private String mobileApp;               // 서비스명 (어플명) *필수

	private String contentId;               // 콘텐츠 ID *필수

	@Builder.Default
	private String type = "json";           // 응답메세지 형식 (기본 json 설정)

	private String serviceKey;              // 인증키 *필수
}
