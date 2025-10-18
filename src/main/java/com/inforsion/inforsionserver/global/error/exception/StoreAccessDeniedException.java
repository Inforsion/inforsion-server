package com.inforsion.inforsionserver.global.error.exception;

import com.inforsion.inforsionserver.global.error.code.ErrorCode;

public class StoreAccessDeniedException extends BusinessException {

    public StoreAccessDeniedException() {
        super(ErrorCode.STORE_ACCESS_DENIED);
    }

    public StoreAccessDeniedException(String message) {
        super(ErrorCode.STORE_ACCESS_DENIED, message);
    }

    public StoreAccessDeniedException(String message, Throwable cause) {
        super(ErrorCode.STORE_ACCESS_DENIED, message, cause);
    }
}
