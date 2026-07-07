package com.honjaopseoyae.global.apipayload.domain;

import com.honjaopseoyae.global.apipayload.code.BaseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AuthErrorStatus implements BaseCode {

    TOKEN_MISSING(HttpStatus.BAD_REQUEST, "AUTH_400_07", "토큰 값이 필요합니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH_401_01", "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH_401_02", "만료된 토큰입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}