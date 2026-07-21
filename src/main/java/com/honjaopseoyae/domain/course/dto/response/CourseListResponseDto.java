package com.honjaopseoyae.domain.course.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.honjaopseoyae.domain.course.entity.Course;
import com.honjaopseoyae.domain.course.entity.enums.CourseType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CourseListResponseDto {

    private Long id;
    private Long userId;
    private String name;
    private String description;
    private boolean isPublic;
    private boolean isExternal;
    private LocalDate startDate;
    private LocalDate endDate;
    private CourseType courseType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static CourseListResponseDto from(Course course) {
        return CourseListResponseDto.builder()
                .id(course.getId())
                .userId(course.getUser() != null ? course.getUser().getId() : null)
                .name(course.getName())
                .description(course.getDescription())
                .isPublic(course.isPublic())
                .isExternal(course.isExternal())
                .startDate(course.getStartDate())
                .endDate(course.getEndDate())
                .courseType(course.getCourseType())
                .createdAt(course.getCreatedAt())
                .updatedAt(course.getUpdatedAt())
                .build();
    }
}
