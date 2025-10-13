package com.inforsion.inforsionserver.domain.auth.dto.response;

import com.inforsion.inforsionserver.domain.user.dto.response.UserResponseDto;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponseDto {

    private final UserResponseDto user;
    private final TokenResponseDto token;

    public static LoginResponseDto of(UserResponseDto user, TokenResponseDto token) {
        return LoginResponseDto.builder()
                .user(user)
                .token(token)
                .build();
    }
}
