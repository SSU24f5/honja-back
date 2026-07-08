package com.honjaopseoyae.place.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.honjaopseoyae.domain.place.entity.TourPlace;

public interface TourApiRepository extends JpaRepository<TourPlace, String> {
	List<TourPlace> findAllByContentId(String contentId);

	List<TourPlace> findAllByContentIdIn(List<String> contentIds);
}
