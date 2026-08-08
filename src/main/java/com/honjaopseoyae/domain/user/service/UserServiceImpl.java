package com.honjaopseoyae.domain.user.service;

import com.honjaopseoyae.domain.auth.service.EmailAuthServiceImpl;
import com.honjaopseoyae.domain.term.entity.Term;
import com.honjaopseoyae.domain.term.repository.TermRepository;
import com.honjaopseoyae.domain.user.converter.UserConverter;
import com.honjaopseoyae.domain.user.dto.req.UserReqDTO;
import com.honjaopseoyae.domain.user.dto.res.UserResDTO;
import com.honjaopseoyae.domain.user.entity.User;
import com.honjaopseoyae.domain.user.entity.mapping.Agree;
import com.honjaopseoyae.domain.user.repository.AgreeRepository;
import com.honjaopseoyae.domain.user.repository.UserRepository;
import com.honjaopseoyae.global.apipayload.domain.AuthErrorStatus;
import com.honjaopseoyae.global.apipayload.domain.UserErrorStatus;
import com.honjaopseoyae.global.apipayload.exception.GeneralException;
import com.honjaopseoyae.global.utils.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final EmailAuthServiceImpl emailAuthServiceImpl;
    private final UserRepository userRepository;
    private final TermRepository termRepository;
    private final AgreeRepository agreeRepository;
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

        validateRequiredTerms(dto.agreedTermIds());

        String encodedPassword = passwordEncoder.encode(dto.password());

        User user = userConverter.toUser(dto, encodedPassword);
        User savedUser = userRepository.save(user);

        saveAgreements(savedUser, dto.agreedTermIds());
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

    @Override
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(UserErrorStatus.USER_NOT_FOUND));

        if (user.isDeleted()) {
            throw new GeneralException(UserErrorStatus.ALREADY_DELETED_USER);
        }

        user.softDelete();
    }

    private void validateRequiredTerms(List<Long> agreedTermIds) {
        List<Term> requiredTerms = termRepository.findAll().stream()
                .filter(Term::isRequired)
                .toList();

        for (Term required : requiredTerms) {
            if (!agreedTermIds.contains(required.getId())) {
                throw new GeneralException(UserErrorStatus.REQUIRED_TERMS_NOT_AGREED);
            }
        }
    }

    private void saveAgreements(User user, List<Long> agreedTermIds) {
        List<Agree> agreements = agreedTermIds.stream()
                .map(termId -> {
                    Term term = termRepository.findById(termId)
                            .orElseThrow(() -> new GeneralException(UserErrorStatus.TERM_NOT_FOUND));
                    return Agree.builder()
                            .user(user)
                            .term(term)
                            .build();
                })
                .toList();

        agreeRepository.saveAll(agreements);
    }

}