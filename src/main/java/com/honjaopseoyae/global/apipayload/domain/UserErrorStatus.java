package com.honjaopseoyae.global.apipayload.domain;

import org.springframework.http.HttpStatus;

import com.honjaopseoyae.global.apipayload.code.BaseCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserErrorStatus implements BaseCode {
	USER_NOT_FOUND(HttpStatus.NOT_FOUND, "COURSE_404_01", "사용자를 찾을 수 없습니다."),
	INVALID_USER(HttpStatus.NOT_FOUND, "COURSE_404_01", "사용자를 찾을 수 없습니다.");
	private final HttpStatus status;
	private final String code;
	private final String message;
}
