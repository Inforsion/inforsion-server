package com.inforsion.inforsionserver.global.error.exception;

import com.inforsion.inforsionserver.global.error.code.ErrorCode;

public class AuthenticationFailedException extends BusinessException {

    public AuthenticationFailedException() {
        super(ErrorCode.AUTHENTICATION_FAILED);
    }

    public AuthenticationFailedException(String message) {
        super(ErrorCode.AUTHENTICATION_FAILED, message);
    }

    public AuthenticationFailedException(String message, Throwable cause) {
        super(ErrorCode.AUTHENTICATION_FAILED, message, cause);
    }
}
