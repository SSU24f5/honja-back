package com.honjaopseoyae.place.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.honjaopseoyae.domain.place.entity.TourPlace;

@Repository
public interface TourPlaceRepository extends JpaRepository<TourPlace, String> {
}
