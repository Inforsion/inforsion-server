package com.inforsion.inforsionserver.domain.user.controller;

import com.inforsion.inforsionserver.domain.user.dto.request.UserUpdateRequestDto;
import com.inforsion.inforsionserver.domain.user.dto.response.UserResponseDto;
import com.inforsion.inforsionserver.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "회원 관리 API")
public class UserController {

    private final UserService userService;

    /**
     * ID로 사용자 조회 (Redis 캐싱)
     */
    @GetMapping("/{userId}")
    @Operation(summary = "사용자 조회 (ID)", description = "사용자 ID로 사용자 정보를 조회합니다 (Redis 캐싱)")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Integer userId) {
        UserResponseDto response = userService.getUserById(userId);
        return ResponseEntity.ok(response);
    }

    /**
     * 이메일로 사용자 조회 (Redis 캐싱)
     */
    @GetMapping("/email/{email}")
    @Operation(summary = "사용자 조회 (이메일)", description = "이메일로 사용자 정보를 조회합니다 (Redis 캐싱)")
    public ResponseEntity<UserResponseDto> getUserByEmail(@PathVariable String email) {
        UserResponseDto response = userService.getUserByEmail(email);
        return ResponseEntity.ok(response);
    }

    /**
     * 사용자명으로 사용자 조회 (Redis 캐싱)
     */
    @GetMapping("/username/{username}")
    @Operation(summary = "사용자 조회 (사용자명)", description = "사용자명으로 사용자 정보를 조회합니다 (Redis 캐싱)")
    public ResponseEntity<UserResponseDto> getUserByUsername(@PathVariable String username) {
        UserResponseDto response = userService.getUserByUsername(username);
        return ResponseEntity.ok(response);
    }

    /**
     * 전체 사용자 목록 조회
     */
    @GetMapping
    @Operation(summary = "전체 사용자 목록 조회", description = "전체 사용자 목록을 조회합니다")
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        List<UserResponseDto> response = userService.getAllUsers();
        return ResponseEntity.ok(response);
    }

    /**
     * 사용자 정보 수정 (Redis 캐시 갱신)
     */
    @PutMapping("/{userId}")
    @Operation(summary = "사용자 정보 수정", description = "사용자 정보를 수정합니다 (Redis 캐시 갱신)")
    public ResponseEntity<UserResponseDto> updateUser(
            @PathVariable Integer userId,
            @Valid @RequestBody UserUpdateRequestDto requestDto) {
        UserResponseDto response = userService.updateUser(userId, requestDto);
        return ResponseEntity.ok(response);
    }

}