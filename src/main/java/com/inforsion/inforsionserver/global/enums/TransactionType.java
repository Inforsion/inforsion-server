package com.inforsion.inforsionserver.global.enums;

import lombok.RequiredArgsConstructor;
import lombok.Getter;

@Getter
@RequiredArgsConstructor
public enum TransactionType {
    INCOME("매출"),
    EXPENSE("지출");

    private final String description;
}
