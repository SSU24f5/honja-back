package com.honjaopseoyae.global.apipayload.domain;

import org.springframework.http.HttpStatus;

import com.honjaopseoyae.global.apipayload.code.BaseCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum InvitationErrorStatus implements BaseCode {
    INVITATION_NOT_FOUND(HttpStatus.NOT_FOUND, "INVITATION_404_01", "초대장을 찾을 수 없습니다."),
    INVITATION_ACCESS_DENIED(HttpStatus.FORBIDDEN, "INVITATION_403_01", "해당 초대장에 대한 권한이 없습니다."),
    INVITATION_ALREADY_PROCESSED(HttpStatus.BAD_REQUEST, "INVITATION_400_01", "이미 처리된 초대장입니다."),
    SELF_INVITATION_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "INVITATION_400_02", "자기 자신을 초대할 수 없습니다."),
    DUPLICATE_PENDING_INVITATION(HttpStatus.CONFLICT, "INVITATION_409_01", "이미 대기중인 초대가 존재합니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}