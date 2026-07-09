package com.honjaopseoyae.place.dto.request;

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
public class LocationBasedRequestDto {

	@NotBlank
	private String mapx;

	@NotBlank
	private String mapy;

	private String radius = "10000";

	private Integer numOfRows;

	@Builder.Default
	private Integer pageNo = 1;

	@NotBlank(message = "MobileOS is required.")
	@Builder.Default
	private String mobileOS = "ETC";

	@NotBlank(message = "MobileApp is required.")
	@Builder.Default
	private String mobileApp = "HonjaOpseoYae";

	@NotBlank(message = "ServiceKey is required.")
	private String serviceKey;

	@Builder.Default
	private String _type = "json";

	private String arrange;
	private String contentTypeId;
	private String modifiedtime;

	@Builder.Default
	private String areaCode = "39";

	private String sigunguCode;
	private String cat1;
	private String cat2;
	private String cat3;

	@NotBlank(message = "Legal dong region code is required.")
	@Builder.Default
	private String lDongRegnCd = "50";

	private String lDongSignguCd;

	private String lclsSystm1;

	private String lclsSystm2;
	private String lclsSystm3;
}
