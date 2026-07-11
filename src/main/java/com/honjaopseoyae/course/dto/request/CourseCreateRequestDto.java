package com.honjaopseoyae.course.dto.request;

import java.time.LocalDate;

import com.honjaopseoyae.domain.course.CourseType;

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

    @NotNull(message = "작성자 ID는 필수 입력값입니다.")
    private Long userId;

    @NotBlank(message = "코스 이름은 필수 입력값입니다.")
    private String name;

    private String description;

    @NotNull(message = "공개 여부는 필수 입력값입니다.")
    private Boolean isPublic;

    @NotNull(message = "야외 여부는 필수 입력값입니다.")
    private Boolean isExternal;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

    @NotNull
    private CourseType courseType;
}
