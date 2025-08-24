package com.inforsion.inforsionserver.global.enums;

import lombok.RequiredArgsConstructor;
import lombok.Getter;

@Getter
@RequiredArgsConstructor
public enum TransactionType {
    SALE("매출", TransactionCategory.INCOME),
    TAX("세금", TransactionCategory.EXPENSE),
    REFUND("환불", TransactionCategory.EXPENSE),
    COST("원가", TransactionCategory.EXPENSE),
    OTHER_EXPENSE("기타 지출", TransactionCategory.EXPENSE);

    private final String label;
    private final TransactionCategory category;
}

