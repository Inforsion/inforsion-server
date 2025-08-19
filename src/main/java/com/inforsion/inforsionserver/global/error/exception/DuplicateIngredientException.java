package com.inforsion.inforsionserver.global.error.exception;

import com.inforsion.inforsionserver.global.error.code.ErrorCode;

public class DuplicateIngredientException extends BusinessException {

    public DuplicateIngredientException() {
        super(ErrorCode.INGREDIENT_ALREADY_EXISTS);
    }

    public DuplicateIngredientException(String message) {
        super(ErrorCode.INGREDIENT_ALREADY_EXISTS, message);
    }

    public DuplicateIngredientException(String ingredientName, Integer productId) {
        super(ErrorCode.INGREDIENT_ALREADY_EXISTS, 
              String.format("재료가 이미 존재합니다. 재료명: %s, 상품ID: %d", ingredientName, productId));
    }
}