package com.honjaopseoyae.global.apipayload.domain;

import com.honjaopseoyae.global.apipayload.code.BaseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CourseErrorStatus implements BaseCode {

    COURSE_NOT_FOUND(HttpStatus.NOT_FOUND, "COURSE_404_01", "코스를 찾을 수 없습니다."),
    COURSE_PLACE_NOT_FOUND(HttpStatus.NOT_FOUND, "COURSE_404_02", "코스에 담긴 장소를 찾을 수 없습니다."),
    COURSE_NOT_WRITER(HttpStatus.FORBIDDEN, "COURSE_403_01", "코스 작성자만 삭제할 수 있습니다."),
    PLACE_NOT_FOUND(HttpStatus.NOT_FOUND, "COURSE_404_03", "장소를 찾을 수 없습니다."),

    INVALID_COURSE_PLACE(HttpStatus.BAD_REQUEST, "COURSE_400_01", "해당 코스에 속한 장소가 아닙니다."),
    SAME_ORIGIN_DESTINATION(HttpStatus.BAD_REQUEST, "COURSE_400_02", "출발지와 도착지가 동일합니다."),
    ROUTE_NOT_FOUND(HttpStatus.NOT_FOUND, "COURSE_404_04", "경로를 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}