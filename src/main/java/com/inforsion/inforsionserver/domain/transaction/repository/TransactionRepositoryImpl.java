package com.inforsion.inforsionserver.domain.transaction.repository;

import com.inforsion.inforsionserver.domain.transaction.dto.StoreSalesFinancialDto;
import com.inforsion.inforsionserver.domain.transaction.dto.TransactionConditionDto;
import com.inforsion.inforsionserver.domain.transaction.dto.request.TransactionRequestDto;
import com.inforsion.inforsionserver.domain.transaction.dto.response.TransactionResponseDto;
import com.inforsion.inforsionserver.domain.transaction.entity.QTransactionEntity;

import com.inforsion.inforsionserver.global.enums.PeriodType;
import com.inforsion.inforsionserver.global.enums.TransactionType;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

////////////////////// DAO /////////////////////////
@Repository
@RequiredArgsConstructor
public class TransactionRepositoryImpl implements TransactionRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    // queryDSL에서 기본 제공하는 static instance
    private final QTransactionEntity t = QTransactionEntity.transactionEntity;

    // 거래 조회
    @Override
    public List<TransactionResponseDto> findByStoreIdDateRange(
            Integer storeId,
            TransactionType transactionType,
            LocalDateTime startDate,
            LocalDateTime endDate) {

        return queryFactory
                .select(Projections.constructor(
                        TransactionResponseDto.class,
                        t.id,
                        t.store.id,
                        t.store.name,
                        t.date,
                        t.amount,
                        t.type
                ))
                .from(t)
                .where(
                        t.store.id.eq(storeId),
                        transactionTypeEq(transactionType),
                        dateBetween(startDate, endDate)
                )
                .orderBy(t.date.desc())
                .fetch();
    }

    // 거래 수정
    @Override
    public Long updateTransaction(Integer transactionId, TransactionRequestDto requestDto){
       return queryFactory
                .update(t)
                .set(t.name, requestDto.getName())
                .set(t.amount, requestDto.getAmount())
                .set(t.date, requestDto.getDate())
                .set(t.type, requestDto.getType())
                .where(t.id.eq(transactionId)) // 특정 행만 업데이트
                .execute();
    }

    // 거래 삭제
    @Override
    public Long deleteTransaction(Integer transactionId) {
        return queryFactory
                .delete(t)
                .where(t.id.eq(transactionId))
                .execute();
    }

    // 매출 계산
    @Override
    public List<StoreSalesFinancialDto> findStoreSalesFinancial(
            TransactionConditionDto condition, PeriodType periodType
    ) {
        LocalDate start = condition.getStartDate();
        LocalDate end = condition.getEndDate();

        LocalDateTime startDateTime = (start != null) ? start.atStartOfDay() : null;
        LocalDateTime endDateTime = (end != null) ? end.atTime(23, 59, 59) : null;

        BooleanBuilder builder = new BooleanBuilder();
        if (condition.getStoreId() != null) {
            builder.and(t.store.id.eq(condition.getStoreId()));
        }
        if (condition.getStoreName() != null && !condition.getStoreName().isBlank()) {
            builder.and(t.store.name.eq(condition.getStoreName()));
        }
        if (startDateTime != null && endDateTime != null) {
            builder.and(t.date.between(startDateTime, endDateTime));
        } else if (startDateTime != null) {
            builder.and(t.date.goe(startDateTime));
        } else if (endDateTime != null) {
            builder.and(t.date.loe(endDateTime));
        }

        String pattern;

        switch (periodType) {
            case MONTH: pattern = "%Y-%m"; break;
            case YEAR:  pattern = "%Y"; break;
            case DAY:
            default:    pattern = "%Y-%m-%d"; break;
        }
        var periodExpr = Expressions.stringTemplate("DATE_FORMAT({0}, '" + pattern + "')", t.date);

        // 총매출 (SALE)
        var grossExpr = Expressions.numberTemplate(BigDecimal.class,
                "COALESCE(SUM(CASE WHEN {0} = {1} THEN {2} ELSE 0 END), 0)",
                t.type.stringValue(), Expressions.constant("SALE"), t.amount);

        // 환불
        var refundExpr = Expressions.numberTemplate(BigDecimal.class,
                "COALESCE(SUM(CASE WHEN {0} = {1} THEN {2} ELSE 0 END), 0)",
                t.type.stringValue(), Expressions.constant("REFUND"), t.amount);

        // 원가 전체 (COST)
        var costExpr = Expressions.numberTemplate(BigDecimal.class,
                "COALESCE(SUM(CASE WHEN {0} = {1} THEN {2} ELSE 0 END), 0)",
                t.type.stringValue(), Expressions.constant("COST"), t.amount);

        // 세금
        var taxExpr = Expressions.numberTemplate(BigDecimal.class,
                "COALESCE(SUM(CASE WHEN {0} = {1} THEN {2} ELSE 0 END), 0)",
                t.type.stringValue(), Expressions.constant("TAX"), t.amount);

        // 결제수단별
        var cardExpr = Expressions.numberTemplate(BigDecimal.class,
                "COALESCE(SUM(CASE WHEN {0} = {1} AND {2} = {3} THEN {4} ELSE 0 END), 0)",
                t.type.stringValue(), Expressions.constant("SALE"),
                t.paymentMethod.stringValue(), Expressions.constant("CARD"),
                t.amount);

        // 현금 결제
        var cashExpr = Expressions.numberTemplate(BigDecimal.class,
                "COALESCE(SUM(CASE WHEN {0} = {1} AND {2} = {3} THEN {4} ELSE 0 END), 0)",
                t.type.stringValue(), Expressions.constant("SALE"),
                t.paymentMethod.stringValue(), Expressions.constant("CASH"),
                t.amount);

        // 이외 매출
        var otherSalesExpr = Expressions.numberTemplate(BigDecimal.class,
                "COALESCE(SUM(CASE WHEN {0} = {1} AND {2} = {3} THEN {4} ELSE 0 END), 0)",
                t.type.stringValue(), Expressions.constant("SALE"),
                t.paymentMethod.stringValue(), Expressions.constant("OTHER"),
                t.amount);

        // 재료비
        var materialExpr = Expressions.numberTemplate(BigDecimal.class,
                "COALESCE(SUM(CASE WHEN {0} = {1} AND {2} = {3} THEN {4} ELSE 0 END), 0)",
                t.type.stringValue(), Expressions.constant("COST"),
                t.costCategory.stringValue(), Expressions.constant("MATERIAL"),
                t.amount);

        // 고정 비용
        var fixedExpr = Expressions.numberTemplate(BigDecimal.class,
                "COALESCE(SUM(CASE WHEN {0} = {1} AND {2} = {3} THEN {4} ELSE 0 END), 0)",
                t.type.stringValue(), Expressions.constant("COST"),
                t.costCategory.stringValue(), Expressions.constant("FIXED"),
                t.amount);

        var otherCostExpr = Expressions.numberTemplate(BigDecimal.class,
                "COALESCE(SUM(CASE WHEN {0} = {1} AND {2} = {3} THEN {4} ELSE 0 END), 0)",
                t.type.stringValue(), Expressions.constant("COST"),
                t.costCategory.stringValue(), Expressions.constant("OTHER"),
                t.amount);

        // 총 비용
        var totalCostExpr = Expressions.numberTemplate(BigDecimal.class,
                "({0} + {1} + {2})",
                materialExpr, fixedExpr, otherCostExpr);

        // 순수익
        var netProfitExpr = Expressions.numberTemplate(BigDecimal.class,
                "({0} - {1} - {2} - {3})",
                grossExpr, refundExpr, totalCostExpr, taxExpr);

        return queryFactory
                .select(Projections.constructor(
                        StoreSalesFinancialDto.class,
                        t.store.id,            // storeId
                        t.store.name,          // storeName
                        periodExpr,            // period (String)
                        cardExpr,              // cardAmount
                        cashExpr,              // cashAmount
                        otherSalesExpr,        // otherAmount
                        grossExpr,             // grossSales
                        refundExpr,            // refundAmount
                        materialExpr,          // materialCost
                        fixedExpr,             // fixedCost
                        otherCostExpr,         // otherCost
                        totalCostExpr,         // totalCost
                        taxExpr,               // taxAmount
                        netProfitExpr          // netProfit
                ))
                .from(t)
                .where(builder)
                .groupBy(t.store.id, t.store.name, periodExpr)
                .orderBy(periodExpr.asc(), t.store.name.asc())
                .fetch();
    }


    private BooleanExpression transactionTypeEq(TransactionType type) {
        return type != null ? t.type.eq(type) : null;
    }

    private BooleanExpression dateBetween(LocalDateTime start, LocalDateTime end) {
        if (start != null && end != null) {
            return t.date.between(start, end);
        }
        if (start != null) {
            return t.date.goe(start);
        }
        if (end != null) {
            return t.date.loe(end);
        }
        return null;
    }


}
