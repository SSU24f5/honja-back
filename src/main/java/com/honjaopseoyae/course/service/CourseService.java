package com.honjaopseoyae.course.service;

import com.honjaopseoyae.course.dto.request.CourseCreateRequestDto;
import com.honjaopseoyae.course.dto.response.CourseResponseDto;
import com.honjaopseoyae.course.repository.CourseRepository;
import com.honjaopseoyae.domain.course.entity.Course;
import com.honjaopseoyae.domain.user.entity.User;
import com.honjaopseoyae.global.apipayload.domain.CourseErrorStatus;
import com.honjaopseoyae.global.apipayload.exception.GeneralException;
import com.honjaopseoyae.member.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    @Transactional
    public CourseResponseDto createCourse(CourseCreateRequestDto requestDto) {
        User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new GeneralException(CourseErrorStatus.USER_NOT_FOUND));

        Course course = Course.builder()
                .user(user)
                .name(requestDto.getName())
                .description(requestDto.getDescription())
                .isPublic(requestDto.getIsPublic())
                .isExternal(requestDto.getIsExternal())
                .build();

        Course savedCourse = courseRepository.save(course);
        return CourseResponseDto.from(savedCourse);
    }

    @Transactional
    public void deleteCourse(Long courseId, Long userId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new GeneralException(CourseErrorStatus.COURSE_NOT_FOUND));

        if (course.getUser() == null || !course.getUser().getId().equals(userId)) {
            throw new GeneralException(CourseErrorStatus.COURSE_NOT_WRITER);
        }

        courseRepository.delete(course);
    }
}
