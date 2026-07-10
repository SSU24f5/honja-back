package com.honjaopseoyae.domain.auth.service;

import java.time.LocalDateTime;

public interface EmailAuthService {
    public void sendCode(String email);
    public boolean verifyCode(String email, String inputCode);
}
