package com.honjaopseoyae.domain.user.converter;

import com.honjaopseoyae.domain.user.dto.req.UserReqDTO;
import com.honjaopseoyae.domain.user.dto.res.UserResDTO;
import com.honjaopseoyae.domain.user.entity.User;
import com.honjaopseoyae.global.common.RoleType;


public class UserConverter {

    // Entity -> DTO
    public static UserResDTO.SignUpDTO toSignUpDTO(User user) {
        return new UserResDTO.SignUpDTO(
                user.getId(),
                user.getCreatedAt()
        );
    }

    // DTO -> Entity
    public static User toUser(UserReqDTO.SignUpDTO dto, String encodedPassword) {
        return User.builder()
                .email(dto.email())
                .password(encodedPassword)
                .nickname(dto.nickname() + System.currentTimeMillis() % 10000)
                .role(RoleType.ROLE_USER)
                .build();
    }
}