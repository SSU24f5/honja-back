package com.honjaopseoyae.domain.weather.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoRegionResDTO {

    private List<Document> documents;

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Document {

        @JsonProperty("region_type")
        private String regionType;       // "H" 행정동 / "B" 법정동

        @JsonProperty("region_1depth_name")
        private String region1depthName; // 제주특별자치도

        @JsonProperty("region_2depth_name")
        private String region2depthName; // 제주시

        @JsonProperty("region_3depth_name")
        private String region3depthName; // 노형동
    }
}