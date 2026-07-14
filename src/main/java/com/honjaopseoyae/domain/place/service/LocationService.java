package com.honjaopseoyae.domain.place.service;

import java.util.List;

import com.honjaopseoyae.domain.place.dto.request.UserLocationDto;
import com.honjaopseoyae.domain.place.dto.response.TourCommonResponseDto;

public interface LocationService {
	List<TourCommonResponseDto> getLocationBasedPetPlace(UserLocationDto dto);
	List<TourCommonResponseDto> getLocationBasedBarrierFreePlace(UserLocationDto dto);
	// List<TourCommonResponseDto> getPetPlaceByCategoryFromTourAPI(UserLocationDto dto);
	//
	// List<TourCommonResponseDto> getBarrierFreePlaceByCategoryFromTourAPI(UserLocationDto dto);

	List<TourCommonResponseDto> getCommonPlaceByCategoryFromTourAPI(UserLocationDto dto);
}
