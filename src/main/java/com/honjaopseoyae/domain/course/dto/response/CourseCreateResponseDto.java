package com.honjaopseoyae.domain.course.dto.response;

import com.honjaopseoyae.domain.course.entity.enums.CourseType;
import com.honjaopseoyae.domain.course.entity.Course;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class CourseCreateResponseDto {

    private Long id;
    private Long userId;
    private String userNickname;
    private String name;
    private String description;
    private boolean isPublic;
    private boolean isExternal;
    private LocalDate startDate;
    private LocalDate endDate;
    private CourseType courseType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static CourseCreateResponseDto from(Course course) {
        return CourseCreateResponseDto.builder()
                .id(course.getId())
                .userId(course.getUser() != null ? course.getUser().getId() : null)
                .userNickname(course.getUser() != null ? course.getUser().getNickname() : null)
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
