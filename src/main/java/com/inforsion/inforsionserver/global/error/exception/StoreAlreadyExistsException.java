package com.inforsion.inforsionserver.global.error.exception;

import com.inforsion.inforsionserver.global.error.code.ErrorCode;

public class StoreAlreadyExistsException extends BusinessException {

    public StoreAlreadyExistsException() {
        super(ErrorCode.STORE_ALREADY_EXISTS);
    }

    public StoreAlreadyExistsException(String message) {
        super(ErrorCode.STORE_ALREADY_EXISTS, message);
    }

    public StoreAlreadyExistsException(String message, Throwable cause) {
        super(ErrorCode.STORE_ALREADY_EXISTS, message, cause);
    }
}
