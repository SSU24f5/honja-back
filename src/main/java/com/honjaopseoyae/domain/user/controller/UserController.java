package com.honjaopseoyae.domain.user.controller;

import com.honjaopseoyae.domain.user.dto.req.UserReqDTO;
import com.honjaopseoyae.domain.user.dto.res.UserResDTO;
import com.honjaopseoyae.domain.user.service.UserService;
import com.honjaopseoyae.global.apipayload.ApiResponse;
import com.honjaopseoyae.global.security.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PatchMapping("/profile")
    public ApiResponse<UserResDTO.UpdateProfileDTO> updateProfile(
            @AuthenticationPrincipal PrincipalDetails userDetails,
            @RequestBody UserReqDTO.UpdateProfileDTO request){

        UserResDTO.UpdateProfileDTO response =
                userService.updateProfile(userDetails.getUserId(), request);

        return ApiResponse.onSuccess(response);
    }
}
