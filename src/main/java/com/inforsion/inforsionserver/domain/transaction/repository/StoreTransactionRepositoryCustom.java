package com.inforsion.inforsionserver.domain.transaction.repository;

import com.inforsion.inforsionserver.domain.transaction.entity.StoreTransactionEntity;
import com.inforsion.inforsionserver.global.enums.TransactionCategory;
import com.inforsion.inforsionserver.global.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface StoreTransactionRepositoryCustom {
    
    // 기간별 거래 조회
    List<StoreTransactionEntity> findByStoreIdAndDateRange(
        Integer storeId,
        LocalDateTime startDate,
        LocalDateTime endDate
    );
    
    // 총 매출 계산 (수입)
    BigDecimal sumAmountByStoreAndTypeAndDateRange(
        Integer storeId,
        TransactionType type,
        LocalDateTime startDate,
        LocalDateTime endDate
    );
    
    // 카테고리별 금액 계산
    BigDecimal sumAmountByStoreAndCategoryAndDateRange(
        Integer storeId,
        TransactionCategory category,
        LocalDateTime startDate,
        LocalDateTime endDate
    );
    
    // 거래 타입과 카테고리별 금액 계산
    BigDecimal sumAmountByStoreAndTypeAndCategoryAndDateRange(
        Integer storeId,
        TransactionType type,
        TransactionCategory category,
        LocalDateTime startDate,
        LocalDateTime endDate
    );
}