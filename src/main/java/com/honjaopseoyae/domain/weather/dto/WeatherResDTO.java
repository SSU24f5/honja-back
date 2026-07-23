package com.honjaopseoyae.domain.weather.dto;

import com.honjaopseoyae.domain.place.entity.Place;
import com.honjaopseoyae.domain.weather.RecommendationType;
import com.honjaopseoyae.domain.weather.WeatherType;
import lombok.Builder;

import java.util.List;

@Builder
public record WeatherResDTO(
        String region,
        WeatherType weatherType,
        double temperature,
        String dustGrade,
        Integer pm10Value,
        Integer pm25Value,
        RecommendationType recommendation,
        List<PlaceSummary> places,
        boolean weatherDataReliable   // 추가: false면 기상청 API 실패로 인한 기본값
) {
    @Builder
    public record PlaceSummary(
            String contentId,
            String title,
            String image,
            double mapx,
            double mapy,
            boolean petPlace,
            boolean barrierFree
    ) {
        public static PlaceSummary from(Place place) {
            return PlaceSummary.builder()
                    .contentId(place.getContentId())
                    .title(place.getTitle())
                    .image(place.getImage())
                    .mapx(place.getMapx())
                    .mapy(place.getMapy())
                    .petPlace(place.isPetPlace())
                    .barrierFree(place.isBarrierFree())
                    .build();
        }
    }
}