package com.inforsion.inforsionserver.global.jwt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Redis를 활용한 JWT 토큰 관리 서비스
 * - Refresh Token 저장 및 검증
 * - Logout 시 토큰 블랙리스트 관리
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TokenService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final JwtTokenProvider jwtTokenProvider;

    private static final String REFRESH_TOKEN_PREFIX = "refresh_token:";
    private static final String BLACKLIST_PREFIX = "blacklist:";

    /**
     * Refresh Token을 Redis에 저장
     * Key: "refresh_token:{userId}"
     * Value: Refresh Token
     * TTL: Refresh Token 유효 기간
     */
    public void saveRefreshToken(Integer userId, String refreshToken) {
        String key = REFRESH_TOKEN_PREFIX + userId;
        long expirationTime = jwtTokenProvider.getExpirationTime(refreshToken);

        redisTemplate.opsForValue().set(
                key,
                refreshToken,
                expirationTime,
                TimeUnit.MILLISECONDS
        );

        log.info("Refresh Token 저장: userId={}", userId);
    }

    /**
     * Redis에서 Refresh Token 조회
     */
    public String getRefreshToken(Integer userId) {
        String key = REFRESH_TOKEN_PREFIX + userId;
        Object token = redisTemplate.opsForValue().get(key);
        return token != null ? token.toString() : null;
    }

    /**
     * Refresh Token 검증
     * 1. Redis에 저장된 토큰과 일치하는지 확인
     * 2. 토큰 유효성 검증
     */
    public boolean validateRefreshToken(Integer userId, String refreshToken) {
        String savedToken = getRefreshToken(userId);

        if (savedToken == null || !savedToken.equals(refreshToken)) {
            log.warn("Refresh Token이 일치하지 않음: userId={}", userId);
            return false;
        }

        return jwtTokenProvider.validateToken(refreshToken);
    }

    /**
     * Refresh Token 삭제 (로그아웃)
     */
    public void deleteRefreshToken(Integer userId) {
        String key = REFRESH_TOKEN_PREFIX + userId;
        redisTemplate.delete(key);
        log.info("Refresh Token 삭제: userId={}", userId);
    }

    /**
     * Access Token을 블랙리스트에 추가 (로그아웃)
     * Key: "blacklist:{accessToken}"
     * Value: "logout"
     * TTL: 토큰의 남은 유효 시간
     */
    public void addToBlacklist(String accessToken) {
        if (!jwtTokenProvider.validateToken(accessToken)) {
            return;
        }

        String key = BLACKLIST_PREFIX + accessToken;
        long expirationTime = jwtTokenProvider.getExpirationTime(accessToken);

        if (expirationTime > 0) {
            redisTemplate.opsForValue().set(
                    key,
                    "logout",
                    expirationTime,
                    TimeUnit.MILLISECONDS
            );

            Integer userId = jwtTokenProvider.getUserIdFromToken(accessToken);
            log.info("Access Token 블랙리스트 추가: userId={}", userId);
        }
    }

    /**
     * 블랙리스트에 있는 토큰인지 확인
     */
    public boolean isBlacklisted(String accessToken) {
        String key = BLACKLIST_PREFIX + accessToken;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /**
     * 모든 토큰 제거 (특정 사용자)
     */
    public void deleteAllTokens(Integer userId) {
        deleteRefreshToken(userId);
        log.info("사용자의 모든 토큰 삭제: userId={}", userId);
    }
}