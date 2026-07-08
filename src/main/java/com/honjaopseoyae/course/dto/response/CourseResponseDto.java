package com.honjaopseoyae.course.dto.response;

import com.honjaopseoyae.domain.course.entity.Course;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class CourseResponseDto {

    private Long id;
    private Long userId;
    private String userNickname;
    private String name;
    private String description;
    private boolean isPublic;
    private boolean isExternal;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static CourseResponseDto from(Course course) {
        return CourseResponseDto.builder()
                .id(course.getId())
                .userId(course.getUser() != null ? course.getUser().getId() : null)
                .userNickname(course.getUser() != null ? course.getUser().getNickname() : null)
                .name(course.getName())
                .description(course.getDescription())
                .isPublic(course.isPublic())
                .isExternal(course.isExternal())
                .createdAt(course.getCreatedAt())
                .updatedAt(course.getUpdatedAt())
                .build();
    }
}
