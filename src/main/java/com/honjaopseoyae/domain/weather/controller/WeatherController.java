package com.honjaopseoyae.domain.weather.controller;

import com.honjaopseoyae.domain.place.entity.Place;
import com.honjaopseoyae.domain.place.repository.PlaceRepository;
import com.honjaopseoyae.domain.weather.RecommendationType;
import com.honjaopseoyae.domain.weather.dto.WeatherResDTO;
import com.honjaopseoyae.domain.weather.service.WeatherService;
import com.honjaopseoyae.global.apipayload.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/weather")
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherService weatherService;
    private final PlaceRepository placeRepository;

    /**
     * 위경도 기반 날씨 정보 조회
     * GET /api/weather?lat=33.5&lon=126.5
     */
    @GetMapping
    public ApiResponse<WeatherResDTO> getWeather(
            @RequestParam double lat,
            @RequestParam double lon
    ) {
        log.info("날씨 조회 요청: lat={}, lon={}", lat, lon);
        WeatherResDTO response = weatherService.getWeatherAndRecommendation(lat, lon);
        return ApiResponse.onSuccess(response);
    }

    /**
     * 날씨 기반 장소 추천
     * GET /api/places/recommend?lat=33.5&lon=126.5
     */
    @GetMapping("/recommend")
    public ApiResponse<List<Place>> recommendPlaces(
            @RequestParam double lat,
            @RequestParam double lon
    ) {
        // 1. 날씨 조회
        WeatherResDTO weather = weatherService.getWeatherAndRecommendation(lat, lon);

        // 2. 추천 타입에 따라 장소 필터링
        List<Integer> contentTypes = weather.recommendation() == RecommendationType.OUTDOOR
                ? weatherService.getOutdoorContentTypes()
                : weatherService.getIndoorContentTypes();

        List<Place> places = placeRepository.findByContentTypeIn(contentTypes);

        return ApiResponse.onSuccess(places);
    }

}
