package com.inforsion.inforsionserver.domain.user.exception;

public class UserDuplicateException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "이미 존재하는 사용자입니다.";

    public UserDuplicateException() {
        super(DEFAULT_MESSAGE);
    }

    public UserDuplicateException(String message) {
        super(message);
    }
}
