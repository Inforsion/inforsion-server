package com.inforsion.inforsionserver.global.error.exception;

import com.inforsion.inforsionserver.global.error.code.ErrorCode;

public class UserNotFoundException extends BusinessException {

    public UserNotFoundException() {
        super(ErrorCode.USER_NOT_FOUND);
    }

    public UserNotFoundException(String message) {
        super(ErrorCode.USER_NOT_FOUND, message);
    }

    public UserNotFoundException(Integer userId) {
        super(ErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다. ID: " + userId);
    }
}