package com.honjaopseoyae.domain.user.controller;

import com.honjaopseoyae.domain.term.dto.res.TermResDTO;
import com.honjaopseoyae.domain.user.dto.req.UserReqDTO;
import com.honjaopseoyae.domain.user.dto.res.UserResDTO;
import com.honjaopseoyae.domain.user.entity.mapping.Agree;
import com.honjaopseoyae.domain.user.repository.AgreeRepository;
import com.honjaopseoyae.domain.user.service.UserService;
import com.honjaopseoyae.global.apipayload.ApiResponse;
import com.honjaopseoyae.global.security.PrincipalDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

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

    @PatchMapping("/profile")
    public ApiResponse<UserResDTO.UpdateProfileDTO> updateProfile(
            @AuthenticationPrincipal PrincipalDetails userDetails,
            @RequestBody UserReqDTO.UpdateProfileDTO request){

        UserResDTO.UpdateProfileDTO response =
                userService.updateProfile(userDetails.getUserId(), request);

        return ApiResponse.onSuccess(response);
    }

    @DeleteMapping("/delete")
    public ApiResponse<Void>deleteUser(
        @AuthenticationPrincipal PrincipalDetails userDetails
    ){
        userService.deleteUser(userDetails.getUserId());
        return ApiResponse.onSuccess(null);
    }

}
