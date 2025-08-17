package com.inforsion.inforsionserver.domain.transaction.service;

import com.inforsion.inforsionserver.domain.transaction.dto.request.SalesAnalyticsRequest;
import com.inforsion.inforsionserver.domain.transaction.dto.response.SalesAnalyticsResponse;
import com.inforsion.inforsionserver.domain.store.entity.StoreEntity;
import com.inforsion.inforsionserver.domain.store.repository.StoreRepository;
import com.inforsion.inforsionserver.domain.transaction.repository.StoreTransactionRepository;
import com.inforsion.inforsionserver.global.enums.TransactionCategory;
import com.inforsion.inforsionserver.global.enums.TransactionType;
import com.inforsion.inforsionserver.global.error.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("SalesAnalyticsService 테스트")
class SalesAnalyticsServiceTest {

    @Mock
    private StoreTransactionRepository transactionRepository;

    @Mock
    private StoreRepository storeRepository;

    @InjectMocks
    private SalesAnalyticsService salesAnalyticsService;

    private StoreEntity testStore;
    private SalesAnalyticsRequest testRequest;
    private LocalDate testDate;

    @BeforeEach
    void setUp() {
        testStore = StoreEntity.builder()
                .id(1)
                .name("테스트 가게")
                .location("테스트 주소")
                .phoneNumber("010-1234-5678")
                .build();

        testDate = LocalDate.of(2024, 1, 15);
        testRequest = new SalesAnalyticsRequest();
        // 기본값으로 오늘 날짜 사용
    }

    @Test
    @DisplayName("매출 분석 성공 테스트")
    void getAnalyticsSuccess() {
        // given
        Integer storeId = 1;
        given(storeRepository.findById(storeId)).willReturn(Optional.of(testStore));
        
        // 매출 데이터 모킹
        given(transactionRepository.sumAmountByStoreAndTypeAndCategoryAndDateRange(
                eq(storeId), eq(TransactionType.INCOME), eq(TransactionCategory.CARD), any(LocalDateTime.class), any(LocalDateTime.class)))
                .willReturn(new BigDecimal("100000"));
        given(transactionRepository.sumAmountByStoreAndTypeAndCategoryAndDateRange(
                eq(storeId), eq(TransactionType.INCOME), eq(TransactionCategory.CASH), any(LocalDateTime.class), any(LocalDateTime.class)))
                .willReturn(new BigDecimal("50000"));
        given(transactionRepository.sumAmountByStoreAndTypeAndCategoryAndDateRange(
                eq(storeId), eq(TransactionType.INCOME), eq(TransactionCategory.BANK_TRANSFER), any(LocalDateTime.class), any(LocalDateTime.class)))
                .willReturn(new BigDecimal("30000"));
        given(transactionRepository.sumAmountByStoreAndTypeAndCategoryAndDateRange(
                eq(storeId), eq(TransactionType.INCOME), eq(TransactionCategory.OTHER_INCOME), any(LocalDateTime.class), any(LocalDateTime.class)))
                .willReturn(new BigDecimal("20000"));

        // 비용 데이터 모킹
        given(transactionRepository.sumAmountByStoreAndTypeAndCategoryAndDateRange(
                eq(storeId), eq(TransactionType.EXPENSE), eq(TransactionCategory.MATERIAL_COST), any(LocalDateTime.class), any(LocalDateTime.class)))
                .willReturn(new BigDecimal("80000"));
        given(transactionRepository.sumAmountByStoreAndTypeAndCategoryAndDateRange(
                eq(storeId), eq(TransactionType.EXPENSE), eq(TransactionCategory.FIXED_COST), any(LocalDateTime.class), any(LocalDateTime.class)))
                .willReturn(new BigDecimal("30000"));
        given(transactionRepository.sumAmountByStoreAndTypeAndCategoryAndDateRange(
                eq(storeId), eq(TransactionType.EXPENSE), eq(TransactionCategory.UTILITY_COST), any(LocalDateTime.class), any(LocalDateTime.class)))
                .willReturn(new BigDecimal("10000"));
        given(transactionRepository.sumAmountByStoreAndTypeAndCategoryAndDateRange(
                eq(storeId), eq(TransactionType.EXPENSE), eq(TransactionCategory.LABOR_COST), any(LocalDateTime.class), any(LocalDateTime.class)))
                .willReturn(new BigDecimal("50000"));
        given(transactionRepository.sumAmountByStoreAndTypeAndCategoryAndDateRange(
                eq(storeId), eq(TransactionType.EXPENSE), eq(TransactionCategory.TAX), any(LocalDateTime.class), any(LocalDateTime.class)))
                .willReturn(new BigDecimal("15000"));
        given(transactionRepository.sumAmountByStoreAndTypeAndCategoryAndDateRange(
                eq(storeId), eq(TransactionType.EXPENSE), eq(TransactionCategory.REFUND), any(LocalDateTime.class), any(LocalDateTime.class)))
                .willReturn(new BigDecimal("5000"));
        given(transactionRepository.sumAmountByStoreAndTypeAndCategoryAndDateRange(
                eq(storeId), eq(TransactionType.EXPENSE), eq(TransactionCategory.OTHER_EXPENSE), any(LocalDateTime.class), any(LocalDateTime.class)))
                .willReturn(new BigDecimal("10000"));

        // when
        SalesAnalyticsResponse response = salesAnalyticsService.getAnalytics(storeId, testRequest);

        // then
        assertThat(response.getStoreId()).isEqualTo(storeId);
        assertThat(response.getStoreName()).isEqualTo("테스트 가게");
        
        // 매출 검증
        assertThat(response.getTotalRevenue()).isEqualTo(new BigDecimal("200000")); // 100000+50000+30000+20000
        assertThat(response.getCardRevenue()).isEqualTo(new BigDecimal("100000"));
        assertThat(response.getCashRevenue()).isEqualTo(new BigDecimal("50000"));
        assertThat(response.getBankTransferRevenue()).isEqualTo(new BigDecimal("30000"));
        assertThat(response.getOtherRevenue()).isEqualTo(new BigDecimal("20000"));
        
        // 비용 검증
        assertThat(response.getTotalExpense()).isEqualTo(new BigDecimal("200000")); // 80000+30000+10000+50000+15000+5000+10000
        assertThat(response.getMaterialCost()).isEqualTo(new BigDecimal("80000"));
        assertThat(response.getFixedCost()).isEqualTo(new BigDecimal("30000"));
        assertThat(response.getUtilityCost()).isEqualTo(new BigDecimal("10000"));
        assertThat(response.getLaborCost()).isEqualTo(new BigDecimal("50000"));
        assertThat(response.getTaxAmount()).isEqualTo(new BigDecimal("15000"));
        assertThat(response.getRefundAmount()).isEqualTo(new BigDecimal("5000"));
        assertThat(response.getOtherExpense()).isEqualTo(new BigDecimal("10000"));
        
        // 순이익 및 이익률 검증
        assertThat(response.getNetProfit()).isEqualTo(new BigDecimal("0")); // 200000 - 200000
        assertThat(response.getProfitMargin()).isEqualTo(new BigDecimal("0.0000")); // 0 / 200000 * 100
        
        // Repository 호출 검증
        verify(storeRepository).findById(storeId);
    }

    @Test
    @DisplayName("존재하지 않는 가게 ID로 조회 시 예외 발생")
    void getAnalyticsWithNonExistentStore() {
        // given
        Integer nonExistentStoreId = 999;
        given(storeRepository.findById(nonExistentStoreId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> salesAnalyticsService.getAnalytics(nonExistentStoreId, testRequest))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("이익률 계산 테스트 - 매출이 0인 경우")
    void profitMarginCalculationWithZeroRevenue() {
        // given
        Integer storeId = 1;
        given(storeRepository.findById(storeId)).willReturn(Optional.of(testStore));
        
        // 모든 금액을 0으로 설정
        given(transactionRepository.sumAmountByStoreAndTypeAndCategoryAndDateRange(
                any(Integer.class), any(TransactionType.class), any(TransactionCategory.class), any(LocalDateTime.class), any(LocalDateTime.class)))
                .willReturn(BigDecimal.ZERO);

        // when
        SalesAnalyticsResponse response = salesAnalyticsService.getAnalytics(storeId, testRequest);

        // then
        assertThat(response.getTotalRevenue()).isEqualTo(BigDecimal.ZERO);
        assertThat(response.getTotalExpense()).isEqualTo(BigDecimal.ZERO);
        assertThat(response.getNetProfit()).isEqualTo(BigDecimal.ZERO);
        assertThat(response.getProfitMargin()).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("이익률 계산 테스트 - 양수 순이익")
    void profitMarginCalculationWithPositiveProfit() {
        // given
        Integer storeId = 1;
        given(storeRepository.findById(storeId)).willReturn(Optional.of(testStore));
        
        // 나머지 카테고리는 0으로 설정
        given(transactionRepository.sumAmountByStoreAndTypeAndCategoryAndDateRange(
                any(Integer.class), any(TransactionType.class), any(TransactionCategory.class), any(LocalDateTime.class), any(LocalDateTime.class)))
                .willReturn(BigDecimal.ZERO);
        
        // 매출 1,000,000원, 비용 600,000원 (순이익 400,000원, 이익률 40%)
        given(transactionRepository.sumAmountByStoreAndTypeAndCategoryAndDateRange(
                eq(storeId), eq(TransactionType.INCOME), eq(TransactionCategory.CARD), any(LocalDateTime.class), any(LocalDateTime.class)))
                .willReturn(new BigDecimal("1000000"));
        given(transactionRepository.sumAmountByStoreAndTypeAndCategoryAndDateRange(
                eq(storeId), eq(TransactionType.EXPENSE), eq(TransactionCategory.MATERIAL_COST), any(LocalDateTime.class), any(LocalDateTime.class)))
                .willReturn(new BigDecimal("600000"));

        // when
        SalesAnalyticsResponse response = salesAnalyticsService.getAnalytics(storeId, testRequest);

        // then
        assertThat(response.getTotalRevenue()).isEqualTo(new BigDecimal("1000000"));
        assertThat(response.getTotalExpense()).isEqualTo(new BigDecimal("600000"));
        assertThat(response.getNetProfit()).isEqualTo(new BigDecimal("400000"));
        assertThat(response.getProfitMargin()).isEqualTo(new BigDecimal("40.0000")); // (400000/1000000) * 100
    }
}