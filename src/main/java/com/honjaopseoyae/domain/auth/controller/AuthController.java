package com.honjaopseoyae.domain.auth.controller;

import com.honjaopseoyae.domain.auth.service.EmailAuthServiceImpl;
import com.honjaopseoyae.domain.user.dto.req.UserReqDTO;
import com.honjaopseoyae.domain.user.dto.res.UserResDTO;
import com.honjaopseoyae.domain.user.service.UserService;
import com.honjaopseoyae.global.apipayload.ApiResponse;
import com.honjaopseoyae.global.security.PrincipalDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final EmailAuthServiceImpl emailAuthServiceImpl;


    @PostMapping("/email/send")
    public ApiResponse<Void> sendCode(@RequestBody @Valid UserReqDTO.EmailRequest request) {
        emailAuthServiceImpl.sendCode(request.email());
        return ApiResponse.onSuccess(null);
    }
}