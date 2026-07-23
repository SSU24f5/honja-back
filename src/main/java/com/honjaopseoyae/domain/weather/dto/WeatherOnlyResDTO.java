package com.honjaopseoyae.domain.weather.dto;

import com.honjaopseoyae.domain.weather.RecommendationType;
import com.honjaopseoyae.domain.weather.WeatherType;
import lombok.Builder;

@Builder
public record WeatherOnlyResDTO(
        String region,
        WeatherType weatherType,
        double temperature,
        String dustGrade,
        Integer pm10Value,
        Integer pm25Value,
        RecommendationType recommendation,
        boolean weatherDataReliable   // false면 기상청 API 실패로 인한 기본값
) {}