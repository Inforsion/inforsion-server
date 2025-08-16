package com.inforsion.inforsionserver.user;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

	private final UserRepository userRepository;

	public User saveUser(UserDTO userDto) {
		User user = User.builder()
						.email(userDto.getEmail())
						.name(userDto.getName())
						.password(userDto.getPassword())
						.build();
		return userRepository.save(user);
	}
}
