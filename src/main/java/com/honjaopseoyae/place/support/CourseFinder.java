package com.honjaopseoyae.place.support;

import org.springframework.stereotype.Component;

import com.honjaopseoyae.course.entity.Course;
import com.honjaopseoyae.course.repository.CourseRepository;
import com.honjaopseoyae.global.apipayload.domain.CourseErrorStatus;
import com.honjaopseoyae.global.apipayload.exception.GeneralException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CourseFinder {

	private final CourseRepository courseRepository;

	public Course findById(Long courseId) {
		return courseRepository.findById(courseId)
			.orElseThrow(() ->
				new GeneralException(
					CourseErrorStatus.COURSE_NOT_FOUND
				));
	}
}