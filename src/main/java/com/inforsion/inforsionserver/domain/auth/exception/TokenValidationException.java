package com.inforsion.inforsionserver.domain.auth.exception;

public class TokenValidationException extends RuntimeException {

    public TokenValidationException() {
        super("Access Token이 유효하지 않습니다.");
    }

    public TokenValidationException(String message) {
        super(message);
    }
}
