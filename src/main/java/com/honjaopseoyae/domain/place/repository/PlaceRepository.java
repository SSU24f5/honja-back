package com.honjaopseoyae.domain.place.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.honjaopseoyae.domain.place.entity.Place;
public interface PlaceRepository extends JpaRepository<Place, Long> {
	List<Place> findAllByContentId(String contentId);

	List<Place> findAllByContentIdIn(List<String> contentIds);

	Optional<Place> findByContentId(String contentId);

    List<Place> findByContentTypeIn(List<Integer> contentTypes);
    List<Place> findAllByIndoor(boolean indoor);
	Optional<Place> findByMapxAndMapyAndPlaceType(double mapx, double mapy, com.honjaopseoyae.domain.place.entity.PlaceType placeType);
}
