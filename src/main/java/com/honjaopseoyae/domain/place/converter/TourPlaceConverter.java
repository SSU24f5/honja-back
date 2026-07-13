package com.honjaopseoyae.domain.place.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.honjaopseoyae.domain.place.dto.response.TourCommonResponseDto;
import com.honjaopseoyae.domain.place.dto.response.TourPlaceDto;

public class TourPlaceConverter {

	public static TourCommonResponseDto toTourCommonResponseDto(TourPlaceDto dto) {
		if (dto == null) {
			return null;
		}
		return TourCommonResponseDto.builder()
			.contentid(dto.getContentid())
			.contenttypeid(dto.getContenttypeid())
			.title(dto.getTitle())
			.tel(dto.getTel())
			.addr1(dto.getAddr1())
			.addr2(dto.getAddr2())
			.mapx(dto.getMapx())
			.mapy(dto.getMapy())
			.mlevel(dto.getMlevel())
			.firstimage(dto.getFirstimage())
			.firstimage2(dto.getFirstimage2())
			.cpyrhtDivCd(dto.getCpyrhtDivCd())
			.build();
	}

	public static List<TourCommonResponseDto> toTourCommonResponseDtoList(List<TourPlaceDto> list) {
		if (list == null) {
			return new ArrayList<>();
		}
		return list.stream()
			.map(TourPlaceConverter::toTourCommonResponseDto)
			.collect(Collectors.toList());
	}
}
