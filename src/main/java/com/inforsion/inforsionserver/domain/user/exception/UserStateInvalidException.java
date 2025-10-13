package com.inforsion.inforsionserver.domain.user.exception;

public class UserStateInvalidException extends RuntimeException{

    private static final String DEFAULT_MESSAGE = "사용자 상태가 유효하지 않습니다.";

    public UserStateInvalidException() {
        super(DEFAULT_MESSAGE);
    }

    public UserStateInvalidException(String message) {
        super(message);
    }
}
