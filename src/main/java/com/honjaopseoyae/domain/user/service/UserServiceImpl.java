package com.honjaopseoyae.domain.user.service;

import com.honjaopseoyae.domain.auth.service.EmailAuthServiceImpl;
import com.honjaopseoyae.domain.user.converter.UserConverter;
import com.honjaopseoyae.domain.user.dto.req.UserReqDTO;
import com.honjaopseoyae.domain.user.dto.res.UserResDTO;
import com.honjaopseoyae.domain.user.entity.User;
import com.honjaopseoyae.domain.user.repository.UserRepository;
import com.honjaopseoyae.global.apipayload.domain.AuthErrorStatus;
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

        User user = UserConverter.toUser(dto, encodedPassword);
        User savedUser = userRepository.save(user);

        return UserConverter.toSignUpDTO(savedUser);
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
}