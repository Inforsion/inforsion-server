package com.inforsion.inforsionserver.global.error.exception;

import com.inforsion.inforsionserver.global.error.code.ErrorCode;

public class InsufficientStockException extends BusinessException {

    public InsufficientStockException() {
        super(ErrorCode.INVENTORY_INSUFFICIENT);
    }

    public InsufficientStockException(String message) {
        super(ErrorCode.INVENTORY_INSUFFICIENT, message);
    }

    public InsufficientStockException(String itemName, Integer required, Integer available) {
        super(ErrorCode.INVENTORY_INSUFFICIENT, 
            String.format("재고가 부족합니다. 상품: %s, 필요량: %d, 보유량: %d", itemName, required, available));
    }
}