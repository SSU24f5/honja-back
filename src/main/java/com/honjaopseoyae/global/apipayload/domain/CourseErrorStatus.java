package com.honjaopseoyae.global.apipayload.domain;

import com.honjaopseoyae.global.apipayload.code.BaseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CourseErrorStatus implements BaseCode {

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "COURSE_404_01", "사용자를 찾을 수 없습니다."),
    COURSE_NOT_FOUND(HttpStatus.NOT_FOUND, "COURSE_404_02", "코스를 찾을 수 없습니다."),
    COURSE_NOT_WRITER(HttpStatus.FORBIDDEN, "COURSE_403_01", "코스 작성자만 삭제할 수 있습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
