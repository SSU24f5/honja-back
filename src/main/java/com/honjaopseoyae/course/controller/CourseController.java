package com.honjaopseoyae.course.controller;

import com.honjaopseoyae.course.dto.request.CourseCreateRequestDto;
import com.honjaopseoyae.course.dto.response.CourseResponseDto;
import com.honjaopseoyae.course.service.CourseService;
import com.honjaopseoyae.global.apipayload.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @PostMapping
    public ApiResponse<CourseResponseDto> createCourse(@RequestBody @Valid CourseCreateRequestDto requestDto) {
        CourseResponseDto response = courseService.createCourse(requestDto);
        return ApiResponse.onSuccess(response);
    }

    @DeleteMapping("/{courseId}")
    public ApiResponse<String> deleteCourse(
            @PathVariable Long courseId,
            @RequestParam Long userId) {
        courseService.deleteCourse(courseId, userId);
        return ApiResponse.onSuccess("코스가 성공적으로 삭제되었습니다.");
    }
}
