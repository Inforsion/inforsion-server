package com.inforsion.inforsionserver.global.auth;

import com.inforsion.inforsionserver.global.error.exception.AuthenticationFailedException;
import org.springframework.util.StringUtils;

public final class AuthorizationHeaderUtil {

    private static final String BEARER_PREFIX = "Bearer ";

    private AuthorizationHeaderUtil() {
    }

    public static String extractAccessToken(String authorizationHeader) {
        if (!StringUtils.hasText(authorizationHeader) || !authorizationHeader.startsWith(BEARER_PREFIX)) {
            throw new AuthenticationFailedException("유효한 Authorization 헤더가 필요합니다.");
        }

        String token = authorizationHeader.substring(BEARER_PREFIX.length());
        String trimmedToken = token != null ? token.trim() : null;

        if (!StringUtils.hasText(trimmedToken)) {
            throw new AuthenticationFailedException("Access Token이 비어있습니다.");
        }

        return trimmedToken;
    }
}
