package com.honjaopseoyae.domain.weather.service;

import com.honjaopseoyae.config.KakaoGeoClient;
import com.honjaopseoyae.domain.place.entity.Place;
import com.honjaopseoyae.domain.place.service.TourApiService;
import com.honjaopseoyae.domain.weather.RecommendationType;
import com.honjaopseoyae.domain.weather.WeatherType;
import com.honjaopseoyae.domain.weather.client.AirKoreaClient;
import com.honjaopseoyae.domain.weather.client.AirKoreaClient.DustInfo;
import com.honjaopseoyae.domain.weather.client.KmaWeatherClient;
import com.honjaopseoyae.domain.weather.client.KmaWeatherClient.FcstItem;
import com.honjaopseoyae.domain.weather.dto.WeatherOnlyResDTO;
import com.honjaopseoyae.domain.weather.dto.WeatherResDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeatherServiceImpl implements WeatherService {

    private final KmaWeatherClient kmaWeatherClient;
    private final AirKoreaClient airKoreaClient;
    private final KakaoGeoClient kakaoGeoClient;
    private final TourApiService tourApiService;

    private static final double DEFAULT_RADIUS_KM = 10.0;
    private static final int DEFAULT_LIMIT = 20;

    // 기상청 API 실패 시 사용할 기본값
    private static final WeatherType FALLBACK_WEATHER_TYPE = WeatherType.SUNNY;
    private static final double FALLBACK_TEMPERATURE = 20.0;

    @Override
    public WeatherOnlyResDTO getWeatherOnly(double latitude, double longitude) {

        WeatherSnapshot snapshot = resolveWeather(latitude, longitude);

        DustInfo dust;
        try {
            String stationName = airKoreaClient.findNearestStation(latitude, longitude);
            dust = airKoreaClient.getRealtimeDust(stationName);
        } catch (Exception e) {
            log.error("에어코리아 API 호출 또는 파싱 실패 - 좌표: ({}, {}), 사유: {}", latitude, longitude, e.getMessage());
            dust = new DustInfo(null, null, "데이터없음");
        }
        log.info("미세먼지 조회 완료: 등급={}", dust.grade());

        String regionName;
        try {
            regionName = kakaoGeoClient.getRegionName(latitude, longitude);
        } catch (Exception e) {
            log.error("카카오 지역명 조회 실패 - 좌표: ({}, {}), 사유: {}", latitude, longitude, e.getMessage());
            regionName = "위치 확인 불가";
        }

        RecommendationType recommendation = determineRecommendation(snapshot.weatherType(), dust);

        return WeatherOnlyResDTO.builder()
                .region(regionName)
                .weatherType(snapshot.weatherType())
                .temperature(snapshot.temperature())
                .dustGrade(dust.grade())
                .pm10Value(dust.pm10Value())
                .pm25Value(dust.pm25Value())
                .recommendation(recommendation)
                .weatherDataReliable(snapshot.reliable())
                .build();
    }

    @Override
    public WeatherResDTO getWeatherAndRecommendation(double latitude, double longitude) {

        WeatherSnapshot snapshot = resolveWeather(latitude, longitude);

        DustInfo dust;
        try {
            String stationName = airKoreaClient.findNearestStation(latitude, longitude);
            dust = airKoreaClient.getRealtimeDust(stationName);
        } catch (Exception e) {
            log.error("에어코리아 API 호출 또는 파싱 실패 - 좌표: ({}, {}), 사유: {}", latitude, longitude, e.getMessage());
            dust = new DustInfo(null, null, "데이터없음");
        }
        log.info("미세먼지 조회 완료: 등급={}", dust.grade());

        String regionName;
        try {
            regionName = kakaoGeoClient.getRegionName(latitude, longitude);
        } catch (Exception e) {
            log.error("카카오 지역명 조회 실패 - 좌표: ({}, {}), 사유: {}", latitude, longitude, e.getMessage());
            regionName = "위치 확인 불가";
        }

        RecommendationType recommendation = determineRecommendation(snapshot.weatherType(), dust);
        boolean indoorFlag = recommendation == RecommendationType.INDOOR;

        List<Place> nearbyPlaces = tourApiService.findNearby(
                latitude, longitude, indoorFlag, DEFAULT_RADIUS_KM, DEFAULT_LIMIT
        );

        List<WeatherResDTO.PlaceSummary> placeSummaries = nearbyPlaces.stream()
                .map(WeatherResDTO.PlaceSummary::from)
                .toList();

        return WeatherResDTO.builder()
                .region(regionName)
                .weatherType(snapshot.weatherType())
                .temperature(snapshot.temperature())
                .dustGrade(dust.grade())
                .pm10Value(dust.pm10Value())
                .pm25Value(dust.pm25Value())
                .recommendation(recommendation)
                .places(placeSummaries)
                .weatherDataReliable(snapshot.reliable())
                .build();
    }


    private WeatherSnapshot resolveWeather(double latitude, double longitude) {
        try {
            FcstItem fcst = kmaWeatherClient.getUltraSrtFcst(latitude, longitude);

            if (fcst == null) {
                throw new IllegalStateException("기상청 응답이 null입니다.");
            }

            WeatherType weatherType = WeatherType.of(fcst.skyCode(), fcst.ptyCode());
            log.info("날씨 조회 완료: {} (기온: {}℃)", weatherType, fcst.temperature());

            return new WeatherSnapshot(weatherType, fcst.temperature(), true);

        } catch (Exception e) {
            log.error("기상청 API 호출 실패 - 좌표: ({}, {}), 사유: {}. 기본값({})으로 대체합니다.",
                    latitude, longitude, e.getMessage(), FALLBACK_WEATHER_TYPE);
            return new WeatherSnapshot(FALLBACK_WEATHER_TYPE, FALLBACK_TEMPERATURE, false);
        }
    }

    private record WeatherSnapshot(WeatherType weatherType, double temperature, boolean reliable) {}

    private RecommendationType determineRecommendation(WeatherType weatherType, DustInfo dust) {
        if ("나쁨".equals(dust.grade()) || "매우나쁨".equals(dust.grade())) {
            return RecommendationType.INDOOR;
        }

        if (weatherType.isOutdoorFriendly()) {
            return RecommendationType.OUTDOOR;
        }

        return RecommendationType.INDOOR;
    }
}