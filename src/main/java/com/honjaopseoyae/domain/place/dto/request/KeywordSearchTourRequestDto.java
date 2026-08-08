package com.honjaopseoyae.domain.place.dto.request;

import jakarta.validation.constraints.NotBlank;
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
public class KeywordSearchTourRequestDto {

	@Builder.Default
	private Integer numOfRows = 10;

	@Builder.Default
	private Integer pageNo = 1;

	@NotBlank(message = "MobileOS is required.")
	@Builder.Default
	private String mobileOS = "ETC";

	@NotBlank(message = "MobileApp is required.")
	@Builder.Default
	private String mobileApp = "HonjaOpseoYae";

	private String serviceKey;

	@Builder.Default
	private String _type = "json";

	private String arrange;

	@NotBlank(message = "keyword is required.")
	private String keyword;

	@Builder.Default
	private String areaCode = "39";

	private String sigunguCode;

	private String cat1;
	private String cat2;
	private String cat3;

	private String lDongRegnCd;
	private String lDongSignguCd;

	private String lclsSystm1;
	private String lclsSystm2;
	private String lclsSystm3;
}
