package com.honjaopseoyae.domain.weather.service;

import com.honjaopseoyae.domain.weather.JejuRegion;
import com.honjaopseoyae.domain.weather.RecommendationType;
import com.honjaopseoyae.domain.weather.WeatherType;
import com.honjaopseoyae.domain.weather.client.AirKoreaClient;
import com.honjaopseoyae.domain.weather.client.AirKoreaClient.DustInfo;
import com.honjaopseoyae.domain.weather.client.KmaWeatherClient;
import com.honjaopseoyae.domain.weather.client.KmaWeatherClient.FcstItem;
import com.honjaopseoyae.domain.weather.dto.WeatherResDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeatherService {

    private final KmaWeatherClient kmaWeatherClient;
    private final AirKoreaClient airKoreaClient;

    private static final List<Integer> OUTDOOR_TYPES = List.of(12, 28);
    private static final List<Integer> INDOOR_TYPES = List.of(14, 32, 38, 39);

    public WeatherResDTO getWeatherAndRecommendation(double latitude, double longitude) {
        JejuRegion region = JejuRegion.findNearest(latitude, longitude);
        log.info("사용자 위치 기반 권역 매칭: ({}, {}) → {}", latitude, longitude, region.getDisplayName());

        FcstItem fcst;
        try {
            fcst = kmaWeatherClient.getUltraSrtFcst(region);
        } catch (Exception e) {
            log.error("기상청 API 호출 실패 - 권역: {}, 사유: {}", region.getDisplayName(), e.getMessage());
            throw new RuntimeException("기상청 날씨 데이터를 가져오지 못했습니다.", e);
        }

        if (fcst == null) {
            log.error("기상청 응답 객체가 null입니다. - 권역: {}", region.getDisplayName());
            throw new RuntimeException("기상청 날씨 데이터가 존재하지 않습니다.");
        }

        WeatherType weatherType = WeatherType.of(fcst.skyCode(), fcst.ptyCode());
        log.info("날씨 조회 완료: {} (기온: {}℃)", weatherType, fcst.temperature());

        DustInfo dust;
        try {
            dust = airKoreaClient.getRealtimeDust(region.getAirkoreaStationName());
        } catch (Exception e) {
            log.error("에어코리아 API 호출 또는 파싱 실패 - 측정소: {}, 사유: {}", region.getAirkoreaStationName(), e.getMessage());
            dust = new DustInfo(null, null, "데이터없음");
        }

        log.info("미세먼지 조회 완료: 등급={}", dust.grade());

        RecommendationType recommendation = determineRecommendation(weatherType, dust);
        List<String> categories = recommendation == RecommendationType.OUTDOOR
                ? List.of("관광지", "레포츠")
                : List.of("문화시설", "숙박", "쇼핑", "음식점");

        return WeatherResDTO.builder()
                .region(region.getDisplayName())
                .weatherType(weatherType)
                .temperature(fcst.temperature())
                .dustGrade(dust.grade())
                .pm10Value(dust.pm10Value())
                .pm25Value(dust.pm25Value())
                .recommendation(recommendation)
                .recommendedPlaceCategories(categories)
                .build();
    }

    private RecommendationType determineRecommendation(WeatherType weatherType, DustInfo dust) {
        if ("나쁨".equals(dust.grade()) || "매우나쁨".equals(dust.grade())) {
            return RecommendationType.INDOOR;
        }

        if (weatherType.isOutdoorFriendly()) {
            return RecommendationType.OUTDOOR;
        }

        return RecommendationType.INDOOR;
    }

    public List<Integer> getOutdoorContentTypes() {
        return OUTDOOR_TYPES;
    }

    public List<Integer> getIndoorContentTypes() {
        return INDOOR_TYPES;
    }
}