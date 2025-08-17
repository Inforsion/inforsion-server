package com.inforsion.inforsionserver.global.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TransactionCategory {
    
    // 매출 관련 카테고리
    CARD("카드", TransactionType.INCOME),
    CASH("현금", TransactionType.INCOME),
    BANK_TRANSFER("계좌이체", TransactionType.INCOME),
    OTHER_INCOME("기타 수입", TransactionType.INCOME),
    
    // 원가 관련 카테고리
    MATERIAL_COST("재료비", TransactionType.EXPENSE),
    FIXED_COST("고정비", TransactionType.EXPENSE),
    UTILITY_COST("공과금", TransactionType.EXPENSE),
    LABOR_COST("인건비", TransactionType.EXPENSE),
    TAX("세금", TransactionType.EXPENSE),
    REFUND("환불", TransactionType.EXPENSE),
    OTHER_EXPENSE("기타 지출", TransactionType.EXPENSE);
    
    private final String description;
    private final TransactionType transactionType;
}