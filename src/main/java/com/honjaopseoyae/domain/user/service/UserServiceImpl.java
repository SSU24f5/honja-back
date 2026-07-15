package com.honjaopseoyae.domain.user.service;

import com.honjaopseoyae.domain.auth.service.EmailAuthServiceImpl;
import com.honjaopseoyae.domain.user.converter.UserConverter;
import com.honjaopseoyae.domain.user.dto.req.UserReqDTO;
import com.honjaopseoyae.domain.user.dto.res.UserResDTO;
import com.honjaopseoyae.domain.user.entity.User;
import com.honjaopseoyae.domain.user.repository.UserRepository;
import com.honjaopseoyae.global.apipayload.domain.AuthErrorStatus;
import com.honjaopseoyae.global.apipayload.domain.UserErrorStatus;
import com.honjaopseoyae.global.apipayload.exception.GeneralException;
import com.honjaopseoyae.global.utils.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final EmailAuthServiceImpl emailAuthServiceImpl;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserConverter userConverter;
    private final JWTUtil jwtUtil;

    @Override
    @Transactional
    public UserResDTO.SignUpDTO signUp(UserReqDTO.SignUpDTO dto) {

        boolean verified = emailAuthServiceImpl.verifyCode(dto.email(), dto.authCode());
        if (!verified) {
            throw new GeneralException(AuthErrorStatus.INVALID_AUTH_CODE);
        }

        if (userRepository.findByEmail(dto.email()).isPresent()) {
            throw new GeneralException(AuthErrorStatus.ALREADY_EXIST_EMAIL);
        }

        String encodedPassword = passwordEncoder.encode(dto.password());

        User user = userConverter.toUser(dto, encodedPassword);
        User savedUser = userRepository.save(user);

        return userConverter.toSignUpDTO(savedUser);
    }

    @Override
    public UserResDTO.LoginDTO login(UserReqDTO.LoginDTO dto) {

        User user = userRepository.findByEmail(dto.email())
                .orElseThrow(() -> new GeneralException(AuthErrorStatus.NOT_FOUND_USER));

        if (!passwordEncoder.matches(dto.password(), user.getPassword())) {
            throw new GeneralException(AuthErrorStatus.PASSWORD_MISMATCH);
        }

        String accessToken = jwtUtil.createAccessToken(
                user.getId(),
                user.getEmail(),
                user.getRole().name()
        );

        return new UserResDTO.LoginDTO(accessToken);
    }

    @Override
    public UserResDTO.UpdateProfileDTO updateProfile(Long userId, UserReqDTO.UpdateProfileDTO request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(UserErrorStatus.USER_NOT_FOUND));

        // 닉네임: 값 있으면 바로 반영, 검증 없음
        if (request.nickname() != null && !request.nickname().isBlank()) {
            user.updateNickname(request.nickname());
        }

        // 이메일: 값 있으면 인증코드 필수 + 검증 통과해야 반영
        if (request.email() != null && !request.email().isBlank()) {

            if (request.code() == null || request.code().isBlank()) {
                throw new GeneralException(AuthErrorStatus.VERIFICATION_CODE_REQUIRED);
            }

            boolean verified = emailAuthServiceImpl.verifyCode(request.email(), request.code());
            if (!verified) {
                throw new GeneralException(AuthErrorStatus.INVALID_AUTH_CODE);
            }

            if (userRepository.existsByEmail(request.email())) {
                throw new GeneralException(AuthErrorStatus.ALREADY_EXIST_EMAIL);
            }

            user.updateEmail(request.email());
        }

        return userConverter.toUpdateProfileDTO(user);
    }
}