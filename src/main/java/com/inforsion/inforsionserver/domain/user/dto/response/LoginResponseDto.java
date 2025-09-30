package com.inforsion.inforsionserver.domain.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponseDto {

    private UserResponseDto user;
    private TokenResponseDto token;

    public static LoginResponseDto of(UserResponseDto user, TokenResponseDto token) {
        return LoginResponseDto.builder()
                .user(user)
                .token(token)
                .build();
    }
}