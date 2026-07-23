package com.honjaopseoyae.domain.weather.controller;

import com.honjaopseoyae.domain.weather.dto.WeatherOnlyResDTO;
import com.honjaopseoyae.domain.weather.dto.WeatherResDTO;
import com.honjaopseoyae.domain.weather.service.WeatherService;
import com.honjaopseoyae.global.apipayload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/weather")
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherService weatherService;

    @GetMapping("/recommendation")
    public ApiResponse<WeatherResDTO> getRecommendation(
            @RequestParam double lat,
            @RequestParam double lon
    ) {
        return ApiResponse.onSuccess(weatherService.getWeatherAndRecommendation(lat, lon));
    }

    @GetMapping("/current")
    public ApiResponse<WeatherOnlyResDTO> getWeatherOnly(
            @RequestParam double lat,
            @RequestParam double lon
    ) {
        return ApiResponse.onSuccess(weatherService.getWeatherOnly(lat, lon));
    }
}