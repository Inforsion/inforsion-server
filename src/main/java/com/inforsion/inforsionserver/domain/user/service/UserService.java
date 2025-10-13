package com.inforsion.inforsionserver.domain.user.service;

import com.inforsion.inforsionserver.domain.user.dto.request.UserCreateRequestDto;
//import com.inforsion.inforsionserver.domain.user.dto.request.UserUpdateRequestDto;
import com.inforsion.inforsionserver.domain.user.exception.UserDuplicateException;
import com.inforsion.inforsionserver.domain.user.exception.UserNotFoundException;
import org.slf4j.MDC;
import com.inforsion.inforsionserver.domain.user.dto.response.UserResponseDto;
import com.inforsion.inforsionserver.domain.user.entity.UserEntity;
import com.inforsion.inforsionserver.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private static final String MDC_USER_ID = "userId";
    private static final String MDC_USER_EMAIL = "userEmail";

    /**
     * 사용자 생성
     * 비밀번호는 암호화하여 저장
     */
    @Transactional
    public UserResponseDto createUser(UserCreateRequestDto requestDto) {
        MDC.put(MDC_USER_EMAIL, requestDto.getEmail());
        log.debug("사용자 생성 요청 수신: email={}, username={}", requestDto.getEmail(), requestDto.getUsername());

        try {
            if (userRepository.existsByEmail(requestDto.getEmail())) {
                log.warn("중복 이메일 감지: {}", requestDto.getEmail());
                throw new UserDuplicateException("이미 사용 중인 이메일입니다: " + requestDto.getEmail());
            }

            String encodedPassword = passwordEncoder.encode(requestDto.getPassword());

            UserEntity user = UserEntity.builder()
                    .username(requestDto.getUsername())
                    .email(requestDto.getEmail())
                    .password(encodedPassword)
                    .build();

            UserEntity savedUser = userRepository.save(user);
            MDC.put(MDC_USER_ID, savedUser.getId().toString());
            log.info("사용자 생성 완료");

            return UserResponseDto.from(savedUser);
        } finally {
            MDC.remove(MDC_USER_ID);
            MDC.remove(MDC_USER_EMAIL);
        }
    }

    /**
     * ID로 사용자 조회 (Redis 캐싱)
     * 캐시 키: "user::{userId}"
     */
    @Cacheable(value = "user", key = "#userId")
    public UserResponseDto getUserById(Integer userId) {
        MDC.put(MDC_USER_ID, String.valueOf(userId));
        log.info("DB에서 사용자 조회: userId={}", userId);
        try {
            UserEntity user = userRepository.findById(userId)
                    .orElseThrow(() -> {
                        log.warn("사용자를 찾을 수 없습니다. userId={}", userId);
                        return new UserNotFoundException();
                    });

            log.info("사용자 조회 성공: userId={}", userId);
            return UserResponseDto.from(user);
        } finally {
            MDC.remove(MDC_USER_ID);
        }
    }

    /**
     * 이메일로 사용자 조회 (Redis 캐싱)
     * 캐시 키: "user::email:{email}"
     */
    @Cacheable(value = "user", key = "'email:' + #email")
    public UserResponseDto getUserByEmail(String email) {
        log.info("DB에서 사용자 조회: email={}", email);
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + email));

        return UserResponseDto.from(user);
    }

    /**
     * 사용자명으로 사용자 조회 (Redis 캐싱)
     * 캐시 키: "user::username:{username}"

    @Cacheable(value = "user", key = "'username:' + #username")
    public UserResponseDto getUserByUsername(String username) {
        log.info("DB에서 사용자 조회: username={}", username);
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + username));

        return UserResponseDto.from(user);
    }
    */

    /**
     * 전체 사용자 목록 조회
     * 목록은 캐싱하지 않음 (변경이 빈번하므로)
     */
    public List<UserResponseDto> getAllUsers() {
        log.info("전체 사용자 목록 조회");
        List<UserEntity> users = userRepository.findAll();
        return users.stream()
                .map(UserResponseDto::from)
                .collect(Collectors.toList());
    }

    /**
     * 사용자 정보 수정 (Redis 캐시 갱신)
     * @CachePut: 메서드를 실행하고 결과를 캐시에 저장
    @Transactional
    @CachePut(value = "user", key = "#userId")
    public UserResponseDto updateUser(Integer userId, UserUpdateRequestDto requestDto) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));

        user.updateProfile(requestDto.getName());

        log.info("사용자 정보 수정 완료: userId={}", userId);
        return UserResponseDto.from(user);
    }
    */

    /**
     * 마지막 로그인 시간 업데이트 (Redis 캐시 갱신)
     */
    @Transactional
    @CachePut(value = "user", key = "#userId")
    public UserResponseDto updateLastLoginAt(Integer userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));

        user.updateLastLoginAt();

        log.info("마지막 로그인 시간 업데이트: userId={}", userId);
        return UserResponseDto.from(user);
    }
}