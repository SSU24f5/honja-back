package com.honjaopseoyae.domain.course.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.honjaopseoyae.domain.course.entity.Course;
import com.honjaopseoyae.domain.course.entity.enums.CourseType;
import com.honjaopseoyae.domain.course.entity.enums.InviteStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CourseListResponseDto {

    private Long id;
    private String name;
    private String description;
    private boolean isPublic;
    private LocalDate startDate;
    private LocalDate endDate;
    private CourseType courseType;
    private List<String> profiles;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static CourseListResponseDto from(Course course) {
        return CourseListResponseDto.builder()
                .id(course.getId())
                .name(course.getName())
                .description(course.getDescription())
                .isPublic(course.isPublic())
                .startDate(course.getStartDate())
                .endDate(course.getEndDate())
                .profiles(
                    course.getMembers().stream()
                        .filter(member -> member.getStatus() == InviteStatus.ACCEPTED)
                        .map(member -> member.getUser().getProfile())
                        .toList()
                )
                .courseType(course.getCourseType())
                .createdAt(course.getCreatedAt())
                .updatedAt(course.getUpdatedAt())
                .build();
    }
}
