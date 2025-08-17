package com.inforsion.inforsionserver.global.error.exception;

import com.inforsion.inforsionserver.global.error.code.ErrorCode;

public class StoreNotFoundException extends BusinessException {

    public StoreNotFoundException() {
        super(ErrorCode.STORE_NOT_FOUND);
    }

    public StoreNotFoundException(String message) {
        super(ErrorCode.STORE_NOT_FOUND, message);
    }

    public StoreNotFoundException(Integer storeId) {
        super(ErrorCode.STORE_NOT_FOUND, "가게를 찾을 수 없습니다. ID: " + storeId);
    }
}