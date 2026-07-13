package com.honjaopseoyae.common;

import org.springframework.stereotype.Component;

import com.honjaopseoyae.domain.user.entity.User;
import com.honjaopseoyae.global.apipayload.domain.UserErrorStatus;
import com.honjaopseoyae.global.apipayload.exception.GeneralException;
import com.honjaopseoyae.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserReader {

	private final UserRepository userRepository;

	public User getById(Long userId) {
		return userRepository.findById(userId)
			.orElseThrow(() ->
				new GeneralException(UserErrorStatus.USER_NOT_FOUND));
	}
}