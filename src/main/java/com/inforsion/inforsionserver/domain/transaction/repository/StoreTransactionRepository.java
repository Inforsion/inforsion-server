package com.inforsion.inforsionserver.domain.transaction.repository;

import com.inforsion.inforsionserver.domain.transaction.entity.StoreTransactionEntity;
import com.inforsion.inforsionserver.global.enums.TransactionCategory;
import com.inforsion.inforsionserver.global.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StoreTransactionRepository extends JpaRepository<StoreTransactionEntity, Integer>, StoreTransactionRepositoryCustom {
    
    List<StoreTransactionEntity> findByStoreIdOrderByTransactionDateDesc(Integer storeId);
    
    /* QueryDSL로 대체됨 - StoreTransactionRepositoryImpl 참조
    
    // 기간별 거래 조회
    @Query("SELECT t FROM StoreTransactionEntity t WHERE t.store.id = :storeId " +
           "AND t.transactionDate >= :startDate AND t.transactionDate <= :endDate " +
           "ORDER BY t.transactionDate DESC")
    List<StoreTransactionEntity> findByStoreIdAndDateRange(
        @Param("storeId") Integer storeId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
    
    // 총 매출 계산 (수입)
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM StoreTransactionEntity t " +
           "WHERE t.store.id = :storeId AND t.type = :type " +
           "AND t.transactionDate >= :startDate AND t.transactionDate <= :endDate")
    BigDecimal sumAmountByStoreAndTypeAndDateRange(
        @Param("storeId") Integer storeId,
        @Param("type") TransactionType type,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
    
    // 카테고리별 금액 계산
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM StoreTransactionEntity t " +
           "WHERE t.store.id = :storeId AND t.category = :category " +
           "AND t.transactionDate >= :startDate AND t.transactionDate <= :endDate")
    BigDecimal sumAmountByStoreAndCategoryAndDateRange(
        @Param("storeId") Integer storeId,
        @Param("category") TransactionCategory category,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
    
    */
}