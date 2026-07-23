package com.honjaopseoyae.domain.weather.service;

import com.honjaopseoyae.domain.weather.dto.WeatherOnlyResDTO;
import com.honjaopseoyae.domain.weather.dto.WeatherResDTO;

public interface WeatherService {
    WeatherOnlyResDTO getWeatherOnly(double latitude, double longitude);
    WeatherResDTO getWeatherAndRecommendation(double latitude, double longitude);
}
