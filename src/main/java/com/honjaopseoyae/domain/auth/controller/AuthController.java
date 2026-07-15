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
    private final UserService userService;


    @PostMapping("/email/send")
    public ApiResponse<Void> sendCode(@RequestBody @Valid UserReqDTO.EmailRequest request) {
        emailAuthServiceImpl.sendCode(request.email());
        return ApiResponse.onSuccess(null);
    }

    @PostMapping("/signup")
    public ApiResponse<UserResDTO.SignUpDTO> signUp(@RequestBody @Valid UserReqDTO.SignUpDTO dto) {
        UserResDTO.SignUpDTO response = userService.signUp(dto);
        return ApiResponse.onSuccess(response);
    }

    @PostMapping("/login")
    public ApiResponse<UserResDTO.LoginDTO> login(@RequestBody @Valid UserReqDTO.LoginDTO dto) {
        UserResDTO.LoginDTO response = userService.login(dto);
        return ApiResponse.onSuccess(response);
    }
}