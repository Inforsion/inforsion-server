package com.inforsion.inforsionserver.global.error.exception;

import com.inforsion.inforsionserver.global.error.code.ErrorCode;

public class IngredientNotFoundException extends BusinessException {

    public IngredientNotFoundException() {
        super(ErrorCode.INGREDIENT_NOT_FOUND);
    }

    public IngredientNotFoundException(String message) {
        super(ErrorCode.INGREDIENT_NOT_FOUND, message);
    }

    public IngredientNotFoundException(Integer ingredientId) {
        super(ErrorCode.INGREDIENT_NOT_FOUND, "재료를 찾을 수 없습니다. ID: " + ingredientId);
    }
}