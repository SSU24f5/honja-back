package com.honjaopseoyae.place.service;

import java.util.List;

import com.honjaopseoyae.place.dto.request.UserLocationDto;
import com.honjaopseoyae.place.dto.response.TourCommonResponseDto;

public interface LocationService {
	List<TourCommonResponseDto> getLocationBasedPetPlace(UserLocationDto dto);
	List<TourCommonResponseDto> getLocationBasedBarrierFreePlace(UserLocationDto dto);
}
