package com.inforsion.inforsionserver.domain.auth.exception;

public class InvalidRefreshTokenException extends RuntimeException {

    public InvalidRefreshTokenException() {
        super("Refresh Token이 유효하지 않습니다.");
    }

    public InvalidRefreshTokenException(String message) {
        super(message);
    }
}
