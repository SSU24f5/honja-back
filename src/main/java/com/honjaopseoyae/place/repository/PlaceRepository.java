package com.honjaopseoyae.place.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.honjaopseoyae.place.entity.Place;

public interface PlaceRepository extends JpaRepository<Place, Long> {
	List<Place> findAllByContentId(String contentId);

	List<Place> findAllByContentIdIn(List<String> contentIds);

	Optional<Place> findByContentId(String contentId);
}
