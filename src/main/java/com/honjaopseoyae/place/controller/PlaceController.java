package com.honjaopseoyae.place.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.honjaopseoyae.place.service.PlaceService;
import com.honjaopseoyae.place.service.TourApiService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/tour")
@RequiredArgsConstructor
public class PlaceController {
	private final PlaceService placeService;
	private final TourApiService tourApiService;

	// @GetMapping("/barrier-free/{contentId}")
	// public ResponseEntity<BarrierFreeResponseDto> getBarrierFree(@PathVariable String contentId) {
	// 	BarrierFreeResponseDto response = tourApiService.getBarrierFreeInfo(contentId);
	// 	return ResponseEntity.ok(response);
	// }
	//
	//
	// @GetMapping("/pet-friendly/{contentId}")
	// public ResponseEntity<PetFriendlyResponseDto> getPetFriendly(@PathVariable String contentId) {
	// 	PetFriendlyResponseDto response = tourApiService.getPetFriendlyInfo(contentId);
	// 	return ResponseEntity.ok(response);
	// }


	// 전체 조회 api
	// @GetMapping("/map/barrirer-free")
	// public ResponseEntity<AreaBaseTourResponseDto> getBarrierFreePlaceMap(){
	// 	AreaBaseTourResponseDto response = tourApiService.getBarrierFreePlaceMap();
	// 	return ResponseEntity.ok(response);
	// }
}
