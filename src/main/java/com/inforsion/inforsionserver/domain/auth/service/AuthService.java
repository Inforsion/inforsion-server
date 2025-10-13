package com.inforsion.inforsionserver.domain.auth.service;

import com.inforsion.inforsionserver.domain.auth.dto.request.LoginRequestDto;
import com.inforsion.inforsionserver.domain.auth.dto.response.LoginResponseDto;
import com.inforsion.inforsionserver.domain.auth.dto.response.TokenResponseDto;
import com.inforsion.inforsionserver.domain.auth.exception.InvalidCredentialsException;
import com.inforsion.inforsionserver.domain.auth.exception.InvalidRefreshTokenException;
import com.inforsion.inforsionserver.domain.auth.exception.TokenValidationException;
import com.inforsion.inforsionserver.domain.user.dto.response.UserResponseDto;
import com.inforsion.inforsionserver.domain.user.entity.UserEntity;
import com.inforsion.inforsionserver.domain.user.repository.UserRepository;
import com.inforsion.inforsionserver.global.jwt.JwtTokenProvider;
import com.inforsion.inforsionserver.global.jwt.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * JWT 기반 인증 서비스
 * - 로그인: JWT 토큰 발급 및 Redis 저장
 * - 로그아웃: 토큰 무효화 (블랙리스트)
 * - 토큰 갱신: Refresh Token으로 새로운 Access Token 발급
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenService tokenService;
    private static final String MDC_USER_ID = "userId";
    private static final String MDC_USER_EMAIL = "userEmail";

    /**
     * 로그인
     * 1. 이메일/비밀번호 검증
     * 2. JWT Access/Refresh Token 생성
     * 3. Refresh Token을 Redis에 저장
     * 4. 마지막 로그인 시간 업데이트
     */
    @Transactional
    public LoginResponseDto login(LoginRequestDto requestDto) {
        MDC.put(MDC_USER_EMAIL, requestDto.getEmail());
        log.debug("로그인 시도: email={}", requestDto.getEmail());

        try {
            UserEntity user = userRepository.findByEmail(requestDto.getEmail())
                    .orElseThrow(() -> {
                        log.warn("존재하지 않는 이메일 로그인 시도: {}", requestDto.getEmail());
                        return new InvalidCredentialsException();
                    });

            if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
                log.warn("잘못된 비밀번호 입력: userId={}", user.getId());
                throw new InvalidCredentialsException();
            }

            MDC.put(MDC_USER_ID, user.getId().toString());

            String accessToken = jwtTokenProvider.createAccessToken(user.getId(), user.getEmail());
            String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());
            tokenService.saveRefreshToken(user.getId(), refreshToken);
            user.updateLastLoginAt();

            log.info("로그인 성공: userId={}", user.getId());

            UserResponseDto userDto = UserResponseDto.from(user);
            TokenResponseDto tokenDto = TokenResponseDto.of(
                    accessToken,
                    refreshToken,
                    jwtTokenProvider.getExpirationTime(accessToken)
            );

            return LoginResponseDto.of(userDto, tokenDto);
        } finally {
            MDC.remove(MDC_USER_ID);
            MDC.remove(MDC_USER_EMAIL);
        }
    }

    /**
     * 로그아웃
     * 1. Access Token을 블랙리스트에 추가
     * 2. Redis에서 Refresh Token 삭제
     */
    @Transactional
    public void logout(String accessToken, Integer userId) {
        MDC.put(MDC_USER_ID, String.valueOf(userId));
        log.debug("로그아웃 요청 수신: userId={}", userId);

        try {
            tokenService.addToBlacklist(accessToken);
            tokenService.deleteRefreshToken(userId);
            log.info("로그아웃 완료: userId={}", userId);
        } finally {
            MDC.remove(MDC_USER_ID);
        }
    }

    /**
     * 토큰 갱신
     * 1. Refresh Token 검증
     * 2. 새로운 Access Token 생성
     * 3. 새로운 Refresh Token 생성 (선택적)
     */
    @Transactional
    public TokenResponseDto refreshToken(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            log.warn("만료되었거나 위조된 Refresh Token");
            throw new InvalidRefreshTokenException();
        }

        Integer userId = jwtTokenProvider.getUserIdFromToken(refreshToken);
        MDC.put(MDC_USER_ID, String.valueOf(userId));

        try {
            if (!tokenService.validateRefreshToken(userId, refreshToken)) {
                log.warn("Redis에 저장된 Refresh Token과 불일치: userId={}", userId);
                throw new InvalidRefreshTokenException();
            }

            UserEntity user = userRepository.findById(userId)
                    .orElseThrow(() -> {
                        log.warn("토큰 갱신 중 사용자 미존재: userId={}", userId);
                        return new InvalidRefreshTokenException("사용자를 찾을 수 없습니다.");
                    });

            String newAccessToken = jwtTokenProvider.createAccessToken(user.getId(), user.getEmail());
            String newRefreshToken = jwtTokenProvider.createRefreshToken(user.getId());
            tokenService.saveRefreshToken(user.getId(), newRefreshToken);

            log.info("토큰 갱신 성공: userId={}", userId);

            return TokenResponseDto.of(
                    newAccessToken,
                    newRefreshToken,
                    jwtTokenProvider.getExpirationTime(newAccessToken)
            );
        } finally {
            MDC.remove(MDC_USER_ID);
        }
    }

    /**
     * Access Token 검증 (블랙리스트 확인 포함)
     */
    public boolean validateAccessToken(String accessToken) {
        if (tokenService.isBlacklisted(accessToken)) {
            log.warn("블랙리스트에 있는 토큰입니다");
            return false;
        }

        boolean valid = jwtTokenProvider.validateToken(accessToken);
        if (!valid) {
            log.warn("JWT 토큰 검증 실패");
        }
        return valid;
    }

    /**
     * Access Token으로 사용자 정보 조회
     */
    public UserResponseDto getUserFromToken(String accessToken) {
        if (!validateAccessToken(accessToken)) {
            throw new TokenValidationException();
        }

        Integer userId = jwtTokenProvider.getUserIdFromToken(accessToken);
        MDC.put(MDC_USER_ID, String.valueOf(userId));

        try {
            UserEntity user = userRepository.findById(userId)
                    .orElseThrow(() -> {
                        log.warn("토큰에서 얻은 사용자 ID가 존재하지 않음: {}", userId);
                        return new TokenValidationException("사용자를 찾을 수 없습니다.");
                    });

            log.info("토큰 기반 사용자 조회 성공: userId={}", userId);
            return UserResponseDto.from(user);
        } finally {
            MDC.remove(MDC_USER_ID);
        }
    }
}
