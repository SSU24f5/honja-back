package com.honjaopseoyae.global.apipayload.domain;

import com.honjaopseoyae.global.apipayload.code.BaseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CourseErrorStatus implements BaseCode {

    COURSE_NOT_FOUND(HttpStatus.NOT_FOUND, "COURSE_404_02", "코스를 찾을 수 없습니다."),
    COURSE_PLACE_NOT_FOUND(HttpStatus.NOT_FOUND, "COURSE_404_02", "코스를 찾을 수 없습니다."),
    COURSE_NOT_WRITER(HttpStatus.FORBIDDEN, "COURSE_403_01", "코스 작성자만 삭제할 수 있습니다."),
    PLACE_NOT_FOUND(HttpStatus.NOT_FOUND, "COURSE_404_03", "장소를 찾을 수 없습니다."),
    COURSE_ACCESS_DENIED(HttpStatus.FORBIDDEN, "COURSE_403_02", "코스 접근 권한이 없습니다."),
    INVITATION_NOT_FOUND(HttpStatus.NOT_FOUND, "COURSE_404_04", "초대 요청을 찾을 수 없습니다."),
    ALREADY_INVITED_OR_MEMBER(HttpStatus.BAD_REQUEST, "COURSE_400_01", "이미 초대되었거나 해당 코스의 멤버입니다."),
    CANNOT_INVITE_SELF(HttpStatus.BAD_REQUEST, "COURSE_400_02", "자기 자신을 초대할 수 없습니다."),
    INVALID_INVITATION_STATUS(HttpStatus.BAD_REQUEST, "COURSE_400_03", "이미 처리된 초대 요청입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
