package com.honjaopseoyae.domain.place.service;

import java.util.List;

import com.honjaopseoyae.domain.place.dto.common.TourApiCommonResponse;
import com.honjaopseoyae.domain.place.dto.response.DetailAccessibilityDto;
import com.honjaopseoyae.domain.place.dto.response.PetDetailResponseDto;
import com.honjaopseoyae.domain.place.dto.response.TourCommonResponseDto;

public interface TourApiService {
	TourApiCommonResponse<List<DetailAccessibilityDto>> getBarrierFreeInfo(Long contentId);

	TourApiCommonResponse<List<PetDetailResponseDto>> getPetDetailInfo(Long contentId);

	List<TourCommonResponseDto> getPetPlaceFromTourAPI();

	List<TourCommonResponseDto> getBarrierFreePlaceFromTourAPI();

	List<TourCommonResponseDto> getCommonPlaceFromTourAPI();

	void syncTourPlacesWithApi();
}

