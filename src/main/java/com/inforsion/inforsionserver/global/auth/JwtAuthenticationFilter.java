package com.inforsion.inforsionserver.global.auth;

import com.inforsion.inforsionserver.domain.user.entity.UserEntity;
import com.inforsion.inforsionserver.domain.user.repository.UserRepository;
import com.inforsion.inforsionserver.global.error.exception.AuthenticationFailedException;
import com.inforsion.inforsionserver.global.jwt.JwtTokenProvider;
import com.inforsion.inforsionserver.global.jwt.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final TokenService tokenService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (StringUtils.hasText(authorizationHeader)) {
                String accessToken = AuthorizationHeaderUtil.extractAccessToken(authorizationHeader);

                if (jwtTokenProvider.validateToken(accessToken)
                        && "access".equals(jwtTokenProvider.getTokenType(accessToken))
                        && !tokenService.isBlacklisted(accessToken)) {

                    Integer userId = jwtTokenProvider.getUserIdFromToken(accessToken);

                    if (SecurityContextHolder.getContext().getAuthentication() == null) {
                        Optional<UserEntity> userOptional = userRepository.findById(userId);
                        if (userOptional.isPresent()) {
                            UserPrincipal principal = UserPrincipal.from(userOptional.get());
                            UsernamePasswordAuthenticationToken authentication =
                                    new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
                            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                        } else {
                            log.debug("User not found for token subject: {}", userId);
                        }
                    }
                }
            }
        } catch (AuthenticationFailedException ex) {
            log.debug("Authentication failed: {}", ex.getMessage());
            SecurityContextHolder.clearContext();
        } catch (Exception ex) {
            log.error("JWT authentication filtering error", ex);
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}
