package com.honjaopseoyae.config;

import com.honjaopseoyae.domain.weather.dto.KakaoRegionResDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
@Component
public class KakaoGeoClient {

    private final WebClient kakaoLocalWebClient;

    public KakaoGeoClient(@Qualifier("kakaoLocalWebClient") WebClient kakaoLocalWebClient) {
        this.kakaoLocalWebClient = kakaoLocalWebClient;
    }

    public String getRegionName(double lat, double lon) {
        KakaoRegionResDTO response = kakaoLocalWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v2/local/geo/coord2regioncode.json")
                        .queryParam("x", lon)   // 카카오는 x=경도, y=위도
                        .queryParam("y", lat)
                        .build())
                .retrieve()
                .bodyToMono(KakaoRegionResDTO.class)
                .block();

        return extractRegionName(response);
    }

    private String extractRegionName(KakaoRegionResDTO response) {
        if (response == null || response.getDocuments() == null || response.getDocuments().isEmpty()) {
            return "위치 확인 불가";
        }

        KakaoRegionResDTO.Document target = response.getDocuments().stream()
                .filter(doc -> "H".equals(doc.getRegionType()))   // 행정동 우선
                .findFirst()
                .orElse(response.getDocuments().get(0));

        String region2 = target.getRegion2depthName();  // 제주시
        String region3 = target.getRegion3depthName();  // 노형동

        return (region2 + " " + region3).trim();
    }
}