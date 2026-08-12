package com.honjaopseoyae.domain.place.service;

import java.util.List;

import com.honjaopseoyae.domain.place.dto.common.TourApiCommonResponse;
import com.honjaopseoyae.domain.place.dto.response.DetailAccessibilityDto;
import com.honjaopseoyae.domain.place.dto.response.PetDetailResponseDto;
import com.honjaopseoyae.domain.place.dto.response.TourCommonResponseDto;
import com.honjaopseoyae.domain.place.entity.Place;

public interface TourApiService {
	TourApiCommonResponse<List<DetailAccessibilityDto>> getBarrierFreeInfo(Long contentId);

	TourApiCommonResponse<List<PetDetailResponseDto>> getPetDetailInfo(Long contentId);

	List<TourCommonResponseDto> getPetPlaceFromTourAPI();

	List<TourCommonResponseDto> getBarrierFreePlaceFromTourAPI();

	List<TourCommonResponseDto> getCommonPlaceFromTourAPI();

    List<Place> findNearby(double lat, double lon, boolean indoor, double radiusKm, int limit);
	void syncTourPlacesWithApi();

	List<TourCommonResponseDto> searchCommonPlacesByKeyword(String keyword, Integer pageNo, Integer numOfRows);

	List<TourCommonResponseDto> searchBarrierFreePlacesByKeyword(String keyword, Integer pageNo, Integer numOfRows);

	List<TourCommonResponseDto> searchPetPlacesByKeyword(String keyword, Integer pageNo, Integer numOfRows);
}

