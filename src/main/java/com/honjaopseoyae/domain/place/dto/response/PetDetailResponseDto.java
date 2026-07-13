package com.honjaopseoyae.domain.place.dto.response;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PetDetailResponseDto {
	@JsonProperty("acmpyNeedMtr")
	private String acmpyNeedMtr;

	@JsonProperty("contentid")
	private String contentid;

	@JsonProperty("relaAcdntRiskMtr")
	private String relaAcdntRiskMtr;

	@JsonProperty("acmpyTypeCd")
	private String acmpyTypeCd;

	@JsonProperty("relaPosesFclty")
	private String relaPosesFclty;

	@JsonProperty("relaFrnshPrdlst")
	private String relaFrnshPrdlst;

	@JsonProperty("etcAcmpyInfo")
	private String etcAcmpyInfo;

	@JsonProperty("relaPurcPrdlst")
	private String relaPurcPrdlst;

	@JsonProperty("acmpyPsblCpam")
	private String acmpyPsblCpam;

	@JsonProperty("relaRntlPrdlst")
	private String relaRntlPrdlst;
}
