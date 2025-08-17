package com.inforsion.inforsionserver.domain.transaction.service;

import com.inforsion.inforsionserver.domain.transaction.dto.request.SalesAnalyticsRequest;
import com.inforsion.inforsionserver.domain.transaction.dto.response.SalesAnalyticsResponse;
import com.inforsion.inforsionserver.domain.store.entity.StoreEntity;
import com.inforsion.inforsionserver.domain.store.repository.StoreRepository;
import com.inforsion.inforsionserver.domain.transaction.repository.StoreTransactionRepository;
import com.inforsion.inforsionserver.global.enums.TransactionCategory;
import com.inforsion.inforsionserver.global.enums.TransactionType;
import com.inforsion.inforsionserver.global.error.exception.BusinessException;
import com.inforsion.inforsionserver.global.error.code.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SalesAnalyticsService {

    private final StoreTransactionRepository transactionRepository;
    private final StoreRepository storeRepository;

    public SalesAnalyticsResponse getAnalytics(Integer storeId, SalesAnalyticsRequest request) {
        StoreEntity store = storeRepository.findById(storeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND));

        LocalDateTime startDateTime = request.getStartDate().atStartOfDay();
        LocalDateTime endDateTime = request.getEndDate().atTime(LocalTime.MAX);

        // 매출 계산
        Map<String, BigDecimal> revenueData = calculateRevenue(storeId, startDateTime, endDateTime);
        
        // 비용 계산
        Map<String, BigDecimal> expenseData = calculateExpense(storeId, startDateTime, endDateTime);
        
        // 총 매출과 총 비용
        BigDecimal totalRevenue = revenueData.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalExpense = expenseData.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // 순이익 계산
        BigDecimal netProfit = totalRevenue.subtract(totalExpense);
        
        // 이익률 계산 ((순이익 / 매출) * 100)
        BigDecimal profitMargin = BigDecimal.ZERO;
        if (totalRevenue.compareTo(BigDecimal.ZERO) > 0) {
            profitMargin = netProfit.divide(totalRevenue, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }

        return SalesAnalyticsResponse.builder()
                .storeId(storeId)
                .storeName(store.getName())
                .calculatedAt(LocalDateTime.now())
                
                // 매출 데이터
                .totalRevenue(totalRevenue)
                .cardRevenue(revenueData.get("CARD"))
                .cashRevenue(revenueData.get("CASH"))
                .bankTransferRevenue(revenueData.get("BANK_TRANSFER"))
                .otherRevenue(revenueData.get("OTHER_INCOME"))
                
                // 비용 데이터
                .totalExpense(totalExpense)
                .materialCost(expenseData.get("MATERIAL_COST"))
                .fixedCost(expenseData.get("FIXED_COST"))
                .utilityCost(expenseData.get("UTILITY_COST"))
                .laborCost(expenseData.get("LABOR_COST"))
                .taxAmount(expenseData.get("TAX"))
                .refundAmount(expenseData.get("REFUND"))
                .otherExpense(expenseData.get("OTHER_EXPENSE"))
                
                // 최종 계산
                .netProfit(netProfit)
                .profitMargin(profitMargin)
                
                // 카테고리별 데이터
                .revenueByCategory(revenueData)
                .expenseByCategory(expenseData)
                .build();
    }

    private Map<String, BigDecimal> calculateRevenue(Integer storeId, LocalDateTime startDate, LocalDateTime endDate) {
        Map<String, BigDecimal> revenueData = new HashMap<>();
        
        revenueData.put("CARD", transactionRepository.sumAmountByStoreAndTypeAndCategoryAndDateRange(
                storeId, TransactionType.INCOME, TransactionCategory.CARD, startDate, endDate));
        revenueData.put("CASH", transactionRepository.sumAmountByStoreAndTypeAndCategoryAndDateRange(
                storeId, TransactionType.INCOME, TransactionCategory.CASH, startDate, endDate));
        revenueData.put("BANK_TRANSFER", transactionRepository.sumAmountByStoreAndTypeAndCategoryAndDateRange(
                storeId, TransactionType.INCOME, TransactionCategory.BANK_TRANSFER, startDate, endDate));
        revenueData.put("OTHER_INCOME", transactionRepository.sumAmountByStoreAndTypeAndCategoryAndDateRange(
                storeId, TransactionType.INCOME, TransactionCategory.OTHER_INCOME, startDate, endDate));
        
        return revenueData;
    }

    private Map<String, BigDecimal> calculateExpense(Integer storeId, LocalDateTime startDate, LocalDateTime endDate) {
        Map<String, BigDecimal> expenseData = new HashMap<>();
        
        expenseData.put("MATERIAL_COST", transactionRepository.sumAmountByStoreAndTypeAndCategoryAndDateRange(
                storeId, TransactionType.EXPENSE, TransactionCategory.MATERIAL_COST, startDate, endDate));
        expenseData.put("FIXED_COST", transactionRepository.sumAmountByStoreAndTypeAndCategoryAndDateRange(
                storeId, TransactionType.EXPENSE, TransactionCategory.FIXED_COST, startDate, endDate));
        expenseData.put("UTILITY_COST", transactionRepository.sumAmountByStoreAndTypeAndCategoryAndDateRange(
                storeId, TransactionType.EXPENSE, TransactionCategory.UTILITY_COST, startDate, endDate));
        expenseData.put("LABOR_COST", transactionRepository.sumAmountByStoreAndTypeAndCategoryAndDateRange(
                storeId, TransactionType.EXPENSE, TransactionCategory.LABOR_COST, startDate, endDate));
        expenseData.put("TAX", transactionRepository.sumAmountByStoreAndTypeAndCategoryAndDateRange(
                storeId, TransactionType.EXPENSE, TransactionCategory.TAX, startDate, endDate));
        expenseData.put("REFUND", transactionRepository.sumAmountByStoreAndTypeAndCategoryAndDateRange(
                storeId, TransactionType.EXPENSE, TransactionCategory.REFUND, startDate, endDate));
        expenseData.put("OTHER_EXPENSE", transactionRepository.sumAmountByStoreAndTypeAndCategoryAndDateRange(
                storeId, TransactionType.EXPENSE, TransactionCategory.OTHER_EXPENSE, startDate, endDate));
        
        return expenseData;
    }
}