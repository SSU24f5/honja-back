package com.honjaopseoyae.domain.user.dto.req;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class UserReqDTO {
    public record SignUpDTO(
            @NotBlank
            @Email
            String email,

            @NotBlank
            String authCode,

            @NotBlank
            String nickname,

            @NotBlank
            String password
    ){}

    public record LoginDTO(
            @NotBlank @Email String email,
            @NotBlank String password
    ) {}

    public record EmailRequest(@NotBlank @Email String email) {}
}

