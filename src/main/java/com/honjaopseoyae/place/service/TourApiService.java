package com.honjaopseoyae.place.service;

import java.util.List;

import com.honjaopseoyae.place.dto.common.TourApiCommonResponse;
import com.honjaopseoyae.place.dto.response.DetailAccessibilityDto;
import com.honjaopseoyae.place.dto.response.PetDetailResponseDto;
import com.honjaopseoyae.place.dto.response.TourPlaceDto;

public interface TourApiService {
	TourApiCommonResponse<List<DetailAccessibilityDto>> getBarrierFreeInfo(Long contentId);

	TourApiCommonResponse<List<PetDetailResponseDto>> getPetDetailInfo(Long contentId);

	TourApiCommonResponse<List<TourPlaceDto>> getPetPlaceFromTourAPI();

	TourApiCommonResponse<List<TourPlaceDto>> getBarrierFreePlaceFromTourAPI();

	void syncTourPlacesWithApi();
}
