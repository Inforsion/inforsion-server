package com.inforsion.inforsionserver.domain.transaction.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inforsion.inforsionserver.domain.transaction.dto.response.SalesAnalyticsResponse;
import com.inforsion.inforsionserver.domain.transaction.service.SalesAnalyticsService;
import com.inforsion.inforsionserver.global.error.exception.BusinessException;
import com.inforsion.inforsionserver.global.error.code.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SalesAnalyticsController.class)
@DisplayName("SalesAnalyticsController 테스트")
class SalesAnalyticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SalesAnalyticsService salesAnalyticsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    @DisplayName("매출 분석 조회 API 성공 테스트")
    void getSalesAnalyticsSuccess() throws Exception {
        // given
        Integer storeId = 1;
        
        Map<String, BigDecimal> revenueByCategory = new HashMap<>();
        revenueByCategory.put("CARD", new BigDecimal("100000"));
        revenueByCategory.put("CASH", new BigDecimal("50000"));
        
        Map<String, BigDecimal> expenseByCategory = new HashMap<>();
        expenseByCategory.put("MATERIAL_COST", new BigDecimal("30000"));
        expenseByCategory.put("TAX", new BigDecimal("10000"));
        
        SalesAnalyticsResponse mockResponse = SalesAnalyticsResponse.builder()
                .storeId(storeId)
                .storeName("테스트 가게")
                .calculatedAt(LocalDateTime.now())
                .totalRevenue(new BigDecimal("150000"))
                .cardRevenue(new BigDecimal("100000"))
                .cashRevenue(new BigDecimal("50000"))
                .bankTransferRevenue(BigDecimal.ZERO)
                .otherRevenue(BigDecimal.ZERO)
                .totalExpense(new BigDecimal("40000"))
                .materialCost(new BigDecimal("30000"))
                .fixedCost(BigDecimal.ZERO)
                .utilityCost(BigDecimal.ZERO)
                .laborCost(BigDecimal.ZERO)
                .taxAmount(new BigDecimal("10000"))
                .refundAmount(BigDecimal.ZERO)
                .otherExpense(BigDecimal.ZERO)
                .netProfit(new BigDecimal("110000"))
                .profitMargin(new BigDecimal("73.3333"))
                .revenueByCategory(revenueByCategory)
                .expenseByCategory(expenseByCategory)
                .build();
        
        given(salesAnalyticsService.getAnalytics(eq(storeId), any()))
                .willReturn(mockResponse);

        // when & then
        mockMvc.perform(get("/api/stores/{storeId}/analytics/sales", storeId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.storeId").value(storeId))
                .andExpect(jsonPath("$.storeName").value("테스트 가게"))
                .andExpect(jsonPath("$.totalRevenue").value(150000))
                .andExpect(jsonPath("$.cardRevenue").value(100000))
                .andExpect(jsonPath("$.cashRevenue").value(50000))
                .andExpect(jsonPath("$.totalExpense").value(40000))
                .andExpect(jsonPath("$.materialCost").value(30000))
                .andExpect(jsonPath("$.taxAmount").value(10000))
                .andExpect(jsonPath("$.netProfit").value(110000))
                .andExpect(jsonPath("$.profitMargin").value(73.3333))
                .andExpect(jsonPath("$.revenueByCategory.CARD").value(100000))
                .andExpect(jsonPath("$.revenueByCategory.CASH").value(50000))
                .andExpect(jsonPath("$.expenseByCategory.MATERIAL_COST").value(30000))
                .andExpect(jsonPath("$.expenseByCategory.TAX").value(10000));
    }

    @Test
    @WithMockUser
    @DisplayName("매출 분석 조회 API - 기간 지정 테스트")
    void getSalesAnalyticsWithDateRange() throws Exception {
        // given
        Integer storeId = 1;
        String startDate = "2024-01-01";
        String endDate = "2024-01-31";
        
        SalesAnalyticsResponse mockResponse = SalesAnalyticsResponse.builder()
                .storeId(storeId)
                .storeName("테스트 가게")
                .calculatedAt(LocalDateTime.now())
                .totalRevenue(new BigDecimal("500000"))
                .totalExpense(new BigDecimal("300000"))
                .netProfit(new BigDecimal("200000"))
                .profitMargin(new BigDecimal("40.0000"))
                .cardRevenue(new BigDecimal("300000"))
                .cashRevenue(new BigDecimal("200000"))
                .bankTransferRevenue(BigDecimal.ZERO)
                .otherRevenue(BigDecimal.ZERO)
                .materialCost(new BigDecimal("200000"))
                .fixedCost(new BigDecimal("50000"))
                .utilityCost(new BigDecimal("30000"))
                .laborCost(BigDecimal.ZERO)
                .taxAmount(new BigDecimal("20000"))
                .refundAmount(BigDecimal.ZERO)
                .otherExpense(BigDecimal.ZERO)
                .revenueByCategory(new HashMap<>())
                .expenseByCategory(new HashMap<>())
                .build();
        
        given(salesAnalyticsService.getAnalytics(eq(storeId), any()))
                .willReturn(mockResponse);

        // when & then
        mockMvc.perform(get("/api/stores/{storeId}/analytics/sales", storeId)
                        .param("startDate", startDate)
                        .param("endDate", endDate))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.storeId").value(storeId))
                .andExpect(jsonPath("$.totalRevenue").value(500000))
                .andExpect(jsonPath("$.totalExpense").value(300000))
                .andExpect(jsonPath("$.netProfit").value(200000))
                .andExpect(jsonPath("$.profitMargin").value(40.0000));
    }

    @Test
    @WithMockUser
    @DisplayName("매출 분석 조회 API - 존재하지 않는 가게")
    void getSalesAnalyticsWithNonExistentStore() throws Exception {
        // given
        Integer nonExistentStoreId = 999;
        
        given(salesAnalyticsService.getAnalytics(eq(nonExistentStoreId), any()))
                .willThrow(new BusinessException(ErrorCode.STORE_NOT_FOUND));

        // when & then
        mockMvc.perform(get("/api/stores/{storeId}/analytics/sales", nonExistentStoreId))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    @DisplayName("매출 분석 조회 API - 잘못된 날짜 형식")
    void getSalesAnalyticsWithInvalidDateFormat() throws Exception {
        // given
        Integer storeId = 1;
        String invalidDate = "invalid-date";
        
        SalesAnalyticsResponse mockResponse = SalesAnalyticsResponse.builder()
                .storeId(storeId)
                .storeName("테스트 가게")
                .calculatedAt(LocalDateTime.now())
                .totalRevenue(BigDecimal.ZERO)
                .totalExpense(BigDecimal.ZERO)
                .netProfit(BigDecimal.ZERO)
                .profitMargin(BigDecimal.ZERO)
                .cardRevenue(BigDecimal.ZERO)
                .cashRevenue(BigDecimal.ZERO)
                .bankTransferRevenue(BigDecimal.ZERO)
                .otherRevenue(BigDecimal.ZERO)
                .materialCost(BigDecimal.ZERO)
                .fixedCost(BigDecimal.ZERO)
                .utilityCost(BigDecimal.ZERO)
                .laborCost(BigDecimal.ZERO)
                .taxAmount(BigDecimal.ZERO)
                .refundAmount(BigDecimal.ZERO)
                .otherExpense(BigDecimal.ZERO)
                .revenueByCategory(new HashMap<>())
                .expenseByCategory(new HashMap<>())
                .build();
        
        given(salesAnalyticsService.getAnalytics(eq(storeId), any()))
                .willReturn(mockResponse);

        // when & then - 잘못된 날짜 형식은 무시되고 기본값 사용
        mockMvc.perform(get("/api/stores/{storeId}/analytics/sales", storeId)
                        .param("startDate", invalidDate)
                        .param("endDate", "2024-01-31"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    @DisplayName("매출 분석 조회 API - 기본 날짜 사용")
    void getSalesAnalyticsWithDefaultDate() throws Exception {
        // given
        Integer storeId = 1;
        
        SalesAnalyticsResponse mockResponse = SalesAnalyticsResponse.builder()
                .storeId(storeId)
                .storeName("테스트 가게")
                .calculatedAt(LocalDateTime.now())
                .totalRevenue(new BigDecimal("100000"))
                .totalExpense(new BigDecimal("50000"))
                .netProfit(new BigDecimal("50000"))
                .profitMargin(new BigDecimal("50.0000"))
                .cardRevenue(new BigDecimal("100000"))
                .cashRevenue(BigDecimal.ZERO)
                .bankTransferRevenue(BigDecimal.ZERO)
                .otherRevenue(BigDecimal.ZERO)
                .materialCost(new BigDecimal("50000"))
                .fixedCost(BigDecimal.ZERO)
                .utilityCost(BigDecimal.ZERO)
                .laborCost(BigDecimal.ZERO)
                .taxAmount(BigDecimal.ZERO)
                .refundAmount(BigDecimal.ZERO)
                .otherExpense(BigDecimal.ZERO)
                .revenueByCategory(new HashMap<>())
                .expenseByCategory(new HashMap<>())
                .build();
        
        given(salesAnalyticsService.getAnalytics(eq(storeId), any()))
                .willReturn(mockResponse);

        // when & then
        mockMvc.perform(get("/api/stores/{storeId}/analytics/sales", storeId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.storeId").value(storeId))
                .andExpect(jsonPath("$.totalRevenue").value(100000))
                .andExpect(jsonPath("$.netProfit").value(50000))
                .andExpect(jsonPath("$.profitMargin").value(50.0000));
    }
}