package com.inforsion.inforsionserver.domain.auth.service;

import com.inforsion.inforsionserver.domain.user.dto.request.LoginRequestDto;
import com.inforsion.inforsionserver.domain.user.dto.response.LoginResponseDto;
import com.inforsion.inforsionserver.domain.user.dto.response.TokenResponseDto;
import com.inforsion.inforsionserver.domain.user.dto.response.UserResponseDto;
import com.inforsion.inforsionserver.domain.user.entity.UserEntity;
import com.inforsion.inforsionserver.domain.user.repository.UserRepository;
import com.inforsion.inforsionserver.global.jwt.JwtTokenProvider;
import com.inforsion.inforsionserver.global.jwt.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    /**
     * 로그인
     * 1. 이메일/비밀번호 검증
     * 2. JWT Access/Refresh Token 생성
     * 3. Refresh Token을 Redis에 저장
     * 4. 마지막 로그인 시간 업데이트
     */
    @Transactional
    public LoginResponseDto login(LoginRequestDto requestDto) {
        // 사용자 조회
        UserEntity user = userRepository.findByEmail(requestDto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호가 일치하지 않습니다"));

        // 비밀번호 검증
        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 일치하지 않습니다");
        }

        // JWT 토큰 생성
        String accessToken = jwtTokenProvider.createAccessToken(user.getId(), user.getEmail());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());

        // Refresh Token을 Redis에 저장
        tokenService.saveRefreshToken(user.getId(), refreshToken);

        // 마지막 로그인 시간 업데이트
        user.updateLastLoginAt();

        log.info("로그인 성공: userId={}, email={}", user.getId(), user.getEmail());

        // 응답 생성
        UserResponseDto userDto = UserResponseDto.from(user);
        TokenResponseDto tokenDto = TokenResponseDto.of(
                accessToken,
                refreshToken,
                jwtTokenProvider.getExpirationTime(accessToken)
        );

        return LoginResponseDto.of(userDto, tokenDto);
    }

    /**
     * 로그아웃
     * 1. Access Token을 블랙리스트에 추가
     * 2. Redis에서 Refresh Token 삭제
     */
    @Transactional
    public void logout(String accessToken, Integer userId) {
        // Access Token 블랙리스트 추가
        tokenService.addToBlacklist(accessToken);

        // Refresh Token 삭제
        tokenService.deleteRefreshToken(userId);

        log.info("로그아웃 완료: userId={}", userId);
    }

    /**
     * 토큰 갱신
     * 1. Refresh Token 검증
     * 2. 새로운 Access Token 생성
     * 3. 새로운 Refresh Token 생성 (선택적)
     */
    @Transactional
    public TokenResponseDto refreshToken(String refreshToken) {
        // Refresh Token 유효성 검증
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 Refresh Token입니다");
        }

        // 토큰에서 사용자 ID 추출
        Integer userId = jwtTokenProvider.getUserIdFromToken(refreshToken);

        // Redis에 저장된 토큰과 비교
        if (!tokenService.validateRefreshToken(userId, refreshToken)) {
            throw new IllegalArgumentException("Refresh Token이 일치하지 않습니다");
        }

        // 사용자 정보 조회
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

        // 새로운 Access Token 생성
        String newAccessToken = jwtTokenProvider.createAccessToken(user.getId(), user.getEmail());

        // 새로운 Refresh Token 생성 (선택적 - 보안 강화)
        String newRefreshToken = jwtTokenProvider.createRefreshToken(user.getId());
        tokenService.saveRefreshToken(user.getId(), newRefreshToken);

        log.info("토큰 갱신 완료: userId={}", userId);

        return TokenResponseDto.of(
                newAccessToken,
                newRefreshToken,
                jwtTokenProvider.getExpirationTime(newAccessToken)
        );
    }

    /**
     * Access Token 검증 (블랙리스트 확인 포함)
     */
    public boolean validateAccessToken(String accessToken) {
        // 블랙리스트 확인
        if (tokenService.isBlacklisted(accessToken)) {
            log.warn("블랙리스트에 있는 토큰입니다");
            return false;
        }

        // JWT 토큰 유효성 검증
        return jwtTokenProvider.validateToken(accessToken);
    }

    /**
     * Access Token으로 사용자 정보 조회
     */
    public UserResponseDto getUserFromToken(String accessToken) {
        if (!validateAccessToken(accessToken)) {
            throw new IllegalArgumentException("유효하지 않은 Access Token입니다");
        }

        Integer userId = jwtTokenProvider.getUserIdFromToken(accessToken);
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

        return UserResponseDto.from(user);
    }
}