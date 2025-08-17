package com.inforsion.inforsionserver.domain.transaction.repository;

import com.inforsion.inforsionserver.domain.transaction.entity.QStoreTransactionEntity;
import com.inforsion.inforsionserver.domain.transaction.entity.StoreTransactionEntity;
import com.inforsion.inforsionserver.global.enums.TransactionCategory;
import com.inforsion.inforsionserver.global.enums.TransactionType;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 가게 거래 정보 조회를 위한 QueryDSL 구현체
 * 
 * 주요 기능:
 * - 기간별 거래 내역 조회
 * - 거래 유형별 금액 합계 계산 (매출/지출)
 * - 거래 카테고리별 금액 합계 계산 (카드, 현금, 재료비 등)
 */
@Repository
@RequiredArgsConstructor
public class StoreTransactionRepositoryImpl implements StoreTransactionRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QStoreTransactionEntity transaction = QStoreTransactionEntity.storeTransactionEntity;

    /**
     * 특정 가게의 기간별 거래 내역을 조회합니다.
     * 
     * @param storeId 가게 ID
     * @param startDate 조회 시작 일시
     * @param endDate 조회 종료 일시
     * @return 거래 내역 리스트 (거래 일시 내림차순 정렬)
     */
    @Override
    public List<StoreTransactionEntity> findByStoreIdAndDateRange(
            Integer storeId, 
            LocalDateTime startDate, 
            LocalDateTime endDate) {
        
        return queryFactory
                .selectFrom(transaction)
                .where(
                    transaction.store.id.eq(storeId)           // 가게 ID 조건
                    .and(transaction.transactionDate.goe(startDate))  // 시작 일시 이후
                    .and(transaction.transactionDate.loe(endDate))    // 종료 일시 이전
                )
                .orderBy(transaction.transactionDate.desc())   // 최신순 정렬
                .fetch();
    }

    /**
     * 특정 가게의 거래 유형별 금액 합계를 계산합니다.
     * 
     * @param storeId 가게 ID
     * @param type 거래 유형 (INCOME: 수입, EXPENSE: 지출)
     * @param startDate 조회 시작 일시
     * @param endDate 조회 종료 일시
     * @return 해당 유형의 총 금액 합계 (null인 경우 0 반환)
     */
    @Override
    public BigDecimal sumAmountByStoreAndTypeAndDateRange(
            Integer storeId, 
            TransactionType type, 
            LocalDateTime startDate, 
            LocalDateTime endDate) {
        
        BigDecimal result = queryFactory
                .select(transaction.amount.sum().coalesce(BigDecimal.ZERO))  // 합계 계산, null일 경우 0
                .from(transaction)
                .where(
                    transaction.store.id.eq(storeId)                    // 가게 ID 조건
                    .and(transaction.type.eq(type))                     // 거래 유형 조건
                    .and(transaction.transactionDate.goe(startDate))    // 시작 일시 이후
                    .and(transaction.transactionDate.loe(endDate))      // 종료 일시 이전
                )
                .fetchOne();
        
        return result != null ? result : BigDecimal.ZERO;
    }

    /**
     * 특정 가게의 거래 카테고리별 금액 합계를 계산합니다.
     * 
     * @param storeId 가게 ID
     * @param category 거래 카테고리 (카드, 현금, 재료비, 고정비, 세금, 환불 등)
     * @param startDate 조회 시작 일시
     * @param endDate 조회 종료 일시
     * @return 해당 카테고리의 총 금액 합계 (null인 경우 0 반환)
     */
    @Override
    public BigDecimal sumAmountByStoreAndCategoryAndDateRange(
            Integer storeId, 
            TransactionCategory category, 
            LocalDateTime startDate, 
            LocalDateTime endDate) {
        
        BigDecimal result = queryFactory
                .select(transaction.amount.sum().coalesce(BigDecimal.ZERO))  // 합계 계산, null일 경우 0
                .from(transaction)
                .where(
                    transaction.store.id.eq(storeId)                      // 가게 ID 조건
                    .and(transaction.category.eq(category))               // 거래 카테고리 조건
                    .and(transaction.transactionDate.goe(startDate))      // 시작 일시 이후
                    .and(transaction.transactionDate.loe(endDate))        // 종료 일시 이전
                )
                .fetchOne();
        
        return result != null ? result : BigDecimal.ZERO;
    }

    /**
     * 특정 가게의 거래 유형과 카테고리별 금액 합계를 계산합니다.
     * 
     * @param storeId 가게 ID
     * @param type 거래 유형 (INCOME: 수입, EXPENSE: 지출)
     * @param category 거래 카테고리
     * @param startDate 조회 시작 일시
     * @param endDate 조회 종료 일시
     * @return 해당 유형과 카테고리의 총 금액 합계 (null인 경우 0 반환)
     */
    @Override
    public BigDecimal sumAmountByStoreAndTypeAndCategoryAndDateRange(
            Integer storeId, 
            TransactionType type,
            TransactionCategory category, 
            LocalDateTime startDate, 
            LocalDateTime endDate) {
        
        BigDecimal result = queryFactory
                .select(transaction.amount.sum().coalesce(BigDecimal.ZERO))  // 합계 계산, null일 경우 0
                .from(transaction)
                .where(
                    transaction.store.id.eq(storeId)                      // 가게 ID 조건
                    .and(transaction.type.eq(type))                       // 거래 유형 조건
                    .and(transaction.category.eq(category))               // 거래 카테고리 조건
                    .and(transaction.transactionDate.goe(startDate))      // 시작 일시 이후
                    .and(transaction.transactionDate.loe(endDate))        // 종료 일시 이전
                )
                .fetchOne();
        
        return result != null ? result : BigDecimal.ZERO;
    }
}