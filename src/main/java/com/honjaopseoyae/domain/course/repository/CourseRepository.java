package com.honjaopseoyae.domain.course.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.honjaopseoyae.domain.course.entity.Course;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {


    @EntityGraph(attributePaths = "members")
    List<Course> findAllByIdIn(List<Long> ids);
}
