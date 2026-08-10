package com.honjaopseoyae.domain.course.dto.request;

import java.time.LocalDate;

import com.honjaopseoyae.domain.course.entity.Course;
import com.honjaopseoyae.domain.course.entity.enums.CourseType;
import com.honjaopseoyae.domain.course.entity.enums.TripCategory;
import com.honjaopseoyae.domain.user.entity.User;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CourseCreateRequestDto {
    @NotBlank(message = "코스 이름은 필수 입력값입니다.")
    private String name;

    private String description;

    @NotNull(message = "공개 여부는 필수 입력값입니다.")
    private Boolean isPublic;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

    @NotNull
    private CourseType courseType;

    @NotNull
    private TripCategory tripCategory;


    public static Course toEntity(CourseCreateRequestDto requestDto) {
        return Course.builder()
                .name(requestDto.getName())
                .description(requestDto.getDescription())
                .isPublic(requestDto.getIsPublic())
                .startDate(requestDto.getStartDate())
                .endDate(requestDto.getEndDate())
                .courseType(requestDto.getCourseType())
                .tripCategory(requestDto.getTripCategory())
                .build();
    }
}
