package com.inforsion.inforsionserver.domain.auth.controller;

import com.inforsion.inforsionserver.domain.auth.service.AuthService;
import com.inforsion.inforsionserver.domain.user.dto.request.LoginRequestDto;
import com.inforsion.inforsionserver.domain.user.dto.request.UserCreateRequestDto;
import com.inforsion.inforsionserver.domain.user.dto.response.LoginResponseDto;
import com.inforsion.inforsionserver.domain.user.dto.response.TokenResponseDto;
import com.inforsion.inforsionserver.domain.user.dto.response.UserResponseDto;
import com.inforsion.inforsionserver.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * JWT 인증 API Controller
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "인증 API (JWT + Redis)")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    /**
     * 회원가입
     */
    @PostMapping("/signup")
    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다")
    public ResponseEntity<UserResponseDto> signup(@Valid @RequestBody UserCreateRequestDto requestDto) {
        UserResponseDto response = userService.createUser(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 로그인 (JWT 토큰 발급)
     */
    @PostMapping("/login")
    @Operation(
            summary = "로그인",
            description = "이메일/비밀번호로 로그인하고 JWT 토큰을 발급받습니다. " +
                    "Refresh Token은 Redis에 저장됩니다."
    )
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto requestDto) {
        LoginResponseDto response = authService.login(requestDto);
        return ResponseEntity.ok(response);
    }

    /**
     * 로그아웃 (토큰 무효화)
     */
    @PostMapping("/logout")
    @Operation(
            summary = "로그아웃",
            description = "Access Token을 블랙리스트에 추가하고 Refresh Token을 삭제합니다."
    )
    public ResponseEntity<Void> logout(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam Integer userId
    ) {
        String accessToken = extractToken(authorizationHeader);
        authService.logout(accessToken, userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 토큰 갱신
     */
    @PostMapping("/refresh")
    @Operation(
            summary = "토큰 갱신",
            description = "Refresh Token으로 새로운 Access Token을 발급받습니다."
    )
    public ResponseEntity<TokenResponseDto> refreshToken(@RequestParam String refreshToken) {
        TokenResponseDto response = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(response);
    }

    /**
     * 토큰 검증
     */
    @GetMapping("/validate")
    @Operation(
            summary = "토큰 검증",
            description = "Access Token의 유효성을 검증합니다 (블랙리스트 확인 포함)"
    )
    public ResponseEntity<Boolean> validateToken(
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        String accessToken = extractToken(authorizationHeader);
        boolean isValid = authService.validateAccessToken(accessToken);
        return ResponseEntity.ok(isValid);
    }

    /**
     * 현재 로그인한 사용자 정보 조회
     */
    @GetMapping("/me")
    @Operation(
            summary = "현재 사용자 정보 조회",
            description = "Access Token으로 현재 로그인한 사용자 정보를 조회합니다"
    )
    public ResponseEntity<UserResponseDto> getCurrentUser(
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        String accessToken = extractToken(authorizationHeader);
        UserResponseDto response = authService.getUserFromToken(accessToken);
        return ResponseEntity.ok(response);
    }

    /**
     * Authorization 헤더에서 토큰 추출
     * "Bearer {token}" 형식에서 토큰만 추출
     */
    private String extractToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("유효하지 않은 Authorization 헤더입니다");
        }
        return authorizationHeader.substring(7);
    }
}