package com.honjaopseoyae.domain.place.service;

import org.springframework.stereotype.Service;

import com.honjaopseoyae.config.KakaoLocalClient;
import com.honjaopseoyae.config.KakaoMobilityClient;
import com.honjaopseoyae.domain.course.dto.response.KakaoCoord2AddressResponseDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KakaoService {


	private final KakaoLocalClient kakaoLocalClient;

	public KakaoLocalClient.RoadCoordinate test(double mapx, double mapy) {
		return kakaoLocalClient.getNearestRoad(mapx, mapy);
	}
}
