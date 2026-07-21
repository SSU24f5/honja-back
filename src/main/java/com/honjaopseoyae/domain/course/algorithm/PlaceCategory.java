package com.honjaopseoyae.domain.course.algorithm;
import com.honjaopseoyae.domain.place.entity.Place;

public enum PlaceCategory {

	TOUR,
	RESTAURANT,
	CAFE,
	HOTEL,
	SHOPPING,
	ETC;

	public static PlaceCategory from(Place place) {

		Integer contentType = place.getContentType();

		if (contentType == null) {
			return ETC;
		}

		switch (contentType) {

			case 12:
			case 14:
			case 15:
			case 28:
				return TOUR;

			case 32:
				return HOTEL;

			case 38:
				return SHOPPING;

			case 39:
				return isCafe(place.getCat3())
					? CAFE
					: RESTAURANT;

			default:
				return ETC;
		}
	}

	private static boolean isCafe(String cat3) {

		return "A05020900".equals(cat3);
	}

}