package com.inforsion.inforsionserver.global.auth;

import com.inforsion.inforsionserver.domain.user.repository.UserRepository;
import com.inforsion.inforsionserver.global.error.exception.AuthenticationFailedException;
import com.inforsion.inforsionserver.global.error.exception.UserNotFoundException;
import com.inforsion.inforsionserver.global.jwt.JwtTokenProvider;
import com.inforsion.inforsionserver.global.jwt.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthenticatedUserProvider {

    private final JwtTokenProvider jwtTokenProvider;
    private final TokenService tokenService;
    private final UserRepository userRepository;

    public Integer getUserId(String authorizationHeader) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserPrincipal userPrincipal) {
                return userPrincipal.getId();
            }
            if (principal instanceof UserDetails userDetails && userDetails.getUsername() != null) {
                return mapUserIdFromEmail(userDetails.getUsername());
            }
        }

        String accessToken = AuthorizationHeaderUtil.extractAccessToken(authorizationHeader);

        if (!jwtTokenProvider.validateToken(accessToken)) {
            throw new AuthenticationFailedException("유효하지 않은 Access Token입니다.");
        }

        if (!"access".equals(jwtTokenProvider.getTokenType(accessToken))) {
            throw new AuthenticationFailedException("Access Token이 필요합니다.");
        }

        if (tokenService.isBlacklisted(accessToken)) {
            throw new AuthenticationFailedException("로그아웃된 토큰입니다.");
        }

        Integer userId = jwtTokenProvider.getUserIdFromToken(accessToken);

        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException();
        }

        return userId;
    }

    private Integer mapUserIdFromEmail(String email) {
        return userRepository.findByEmail(email)
                .map(user -> user.getId())
                .orElseThrow(UserNotFoundException::new);
    }
}
