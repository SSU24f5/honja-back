package com.honjaopseoyae.place.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.honjaopseoyae.domain.place.entity.Place;

public interface TourApiRepository extends JpaRepository<Place, Long> {
	List<Place> findAllByContentId(String contentId);

	List<Place> findAllByContentIdIn(List<String> contentIds);
}
