package com.honjaopseoyae.domain.auth.service;

import com.honjaopseoyae.domain.user.service.UserService;
import com.honjaopseoyae.global.apipayload.domain.AuthErrorStatus;
import jakarta.security.auth.message.AuthException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class EmailAuthServiceImpl implements EmailAuthService {

    // redis 안쓰고 메모리에 임시 저장
    private final Map<String, CodeEntry> codeStore = new ConcurrentHashMap<>();

    private static final long EXPIRE_MINUTES = 5;

    @Override
    public void sendCode(String email) {
        String code = generateCode();

        codeStore.put(email, new CodeEntry(code, LocalDateTime.now().plusMinutes(EXPIRE_MINUTES)));

//        // 진짜 메일 발송
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setTo(email);
//        message.setSubject("혼자옵서예 회원가입 인증코드");
//        message.setText("인증코드: " + code);
//
//        mailSender.send(message);

        // 실제 메일 발송 대신 콘솔 출력 (테스트용)
        System.out.println("[테스트] " + email + " 인증코드: " + code);
    }

    @Override
    public boolean verifyCode(String email, String inputCode) {
        CodeEntry entry = codeStore.get(email);

        if (entry == null || entry.expireAt().isBefore(LocalDateTime.now())) {
            return false;
        }

        if (!entry.code().equals(inputCode)) {
            return false;
        }

        codeStore.remove(email);
        return true;
    }


    private String generateCode() {
        return String.valueOf((int) (Math.random() * 900000) + 100000);
    }

    private record CodeEntry(String code, LocalDateTime expireAt) {}
}