package com.honjaopseoyae.domain.weather.dto;
import com.honjaopseoyae.domain.weather.RecommendationType;
import com.honjaopseoyae.domain.weather.WeatherType;
import lombok.Builder;

@Builder
public record WeatherResDTO(
        String region,
        WeatherType weatherType,
        double temperature,
        String dustGrade,      // 좋음/보통/나쁨/매우나쁨
        Integer pm10Value,
        Integer pm25Value,
        RecommendationType recommendation,
        java.util.List<String> recommendedPlaceCategories
) {}