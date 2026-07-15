package com.honjaopseoyae.domain.user.service;

import com.honjaopseoyae.domain.user.dto.req.UserReqDTO;
import com.honjaopseoyae.domain.user.dto.res.UserResDTO;
import org.springframework.transaction.annotation.Transactional;

public interface UserService {

    @Transactional
    UserResDTO.SignUpDTO signUp(UserReqDTO.SignUpDTO dto);

    @Transactional
    UserResDTO.LoginDTO login(UserReqDTO.LoginDTO dto);

    @Transactional
    UserResDTO.UpdateProfileDTO updateProfile(Long userId, UserReqDTO.UpdateProfileDTO request);

    @Transactional
    void deleteUser(Long userId);
}
