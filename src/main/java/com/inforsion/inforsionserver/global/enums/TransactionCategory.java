package com.inforsion.inforsionserver.global.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TransactionCategory {
    INCOME("수익"),
    EXPENSE("지출");

    private final String label;
}