package com.inforsion.inforsionserver.domain.transaction.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inforsion.inforsionserver.domain.store.entity.StoreEntity;
import com.inforsion.inforsionserver.domain.transaction.entity.StoreTransactionEntity;
import com.inforsion.inforsionserver.domain.store.repository.StoreRepository;
import com.inforsion.inforsionserver.domain.transaction.repository.StoreTransactionRepository;
import com.inforsion.inforsionserver.domain.user.entity.UserEntity;
import com.inforsion.inforsionserver.domain.user.repository.UserRepository;
import com.inforsion.inforsionserver.global.enums.TransactionCategory;
import com.inforsion.inforsionserver.global.enums.TransactionType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.support.TransactionTemplate;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("매출 분석 통합 테스트")
class SalesAnalyticsIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private StoreTransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;


    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private ObjectMapper objectMapper;

    private StoreEntity testStore;
    private UserEntity testUser;
    private LocalDateTime baseDateTime;
    private LocalDate baseDate;


    @BeforeEach
    void setUp() {
        // 기존 데이터 정리
        transactionRepository.deleteAll();
        storeRepository.deleteAll();
        userRepository.deleteAll();
        
        baseDateTime = LocalDateTime.of(2024, 1, 15, 10, 0);
        baseDate = LocalDate.of(2024, 1, 15);
    }
    
    private void createTestData() {
        // 테스트용 사용자 생성 (고유한 이메일 사용)
        String uniqueEmail = "test" + System.currentTimeMillis() + "@example.com";
        testUser = UserEntity.builder()
                .username("testuser")
                .email(uniqueEmail)
                .password("password123")
                .build();
        userRepository.saveAndFlush(testUser);
        
        // 테스트용 가게 생성
        testStore = StoreEntity.builder()
                .name("통합테스트 가게")
                .location("통합테스트 주소")
                .phoneNumber("010-1111-2222")
                .user(testUser)
                .build();
        storeRepository.saveAndFlush(testStore);

        // 실제 비즈니스 데이터 생성 - 카페 한 달 데이터
        createRealisticTestData();
        
        // 디버그: 실제 저장된 데이터 확인
        System.out.println("=== 데이터 저장 후 확인 ===");
        System.out.println("Total transactions: " + transactionRepository.count());
        System.out.println("Store ID: " + testStore.getId());
        
        // 저장된 거래내역 확인
        transactionRepository.findAll().forEach(t -> {
            System.out.println("Transaction: " + t.getName() + ", Amount: " + t.getAmount() + ", Type: " + t.getType() + ", Category: " + t.getCategory());
        });
    }
    
    private void createRealisticTestData() {
        // 2024년 1월 15일 카페 하루 매출 데이터
        
        // === 매출 데이터 ===
        // 아침 시간대 (7-11시) - 커피, 베이커리 위주
        createRevenueTransaction("아메리카노 x15", TransactionCategory.CARD, new BigDecimal("75000"), baseDateTime.withHour(8));
        createRevenueTransaction("라떼 x10", TransactionCategory.CARD, new BigDecimal("55000"), baseDateTime.withHour(8).withMinute(30));
        createRevenueTransaction("카푸치노 x8", TransactionCategory.CASH, new BigDecimal("44000"), baseDateTime.withHour(9));
        createRevenueTransaction("크로와상 x12", TransactionCategory.CARD, new BigDecimal("48000"), baseDateTime.withHour(9).withMinute(15));
        createRevenueTransaction("베이글 x8", TransactionCategory.CASH, new BigDecimal("32000"), baseDateTime.withHour(10));
        
        // 점심 시간대 (11-14시) - 샌드위치, 샐러드
        createRevenueTransaction("샌드위치 x15", TransactionCategory.CARD, new BigDecimal("165000"), baseDateTime.withHour(12));
        createRevenueTransaction("샐러드 x8", TransactionCategory.BANK_TRANSFER, new BigDecimal("88000"), baseDateTime.withHour(12).withMinute(30));
        createRevenueTransaction("음료 추가 x20", TransactionCategory.CARD, new BigDecimal("60000"), baseDateTime.withHour(13));
        
        // 오후 시간대 (14-18시) - 디저트, 음료
        createRevenueTransaction("케이크 x10", TransactionCategory.CARD, new BigDecimal("85000"), baseDateTime.withHour(15));
        createRevenueTransaction("쿠키 x20", TransactionCategory.CASH, new BigDecimal("40000"), baseDateTime.withHour(16));
        
        // 저녁 시간대 (18-20시) - 테이크아웃 음료
        createRevenueTransaction("저녁 음료 x12", TransactionCategory.CARD, new BigDecimal("48000"), baseDateTime.withHour(19));
        
        // === 비용 데이터 ===
        // 재료비 - 원두, 우유, 식재료
        createTransaction("원두 구입", TransactionType.EXPENSE, TransactionCategory.MATERIAL_COST, new BigDecimal("180000"), baseDateTime.withHour(7));
        createTransaction("우유/시럽 구입", TransactionType.EXPENSE, TransactionCategory.MATERIAL_COST, new BigDecimal("85000"), baseDateTime.withHour(7).withMinute(30));
        createTransaction("베이커리 재료", TransactionType.EXPENSE, TransactionCategory.MATERIAL_COST, new BigDecimal("120000"), baseDateTime.withHour(8));
        
        // 고정비 - 임대료, 보험료
        createTransaction("월 임대료", TransactionType.EXPENSE, TransactionCategory.FIXED_COST, new BigDecimal("800000"), baseDateTime.withHour(9));
        createTransaction("보험료", TransactionType.EXPENSE, TransactionCategory.FIXED_COST, new BigDecimal("45000"), baseDateTime.withHour(9).withMinute(15));
        
        // 공과금 - 전기, 가스, 수도
        createTransaction("전기료", TransactionType.EXPENSE, TransactionCategory.UTILITY_COST, new BigDecimal("85000"), baseDateTime.withHour(10));
        createTransaction("가스료", TransactionType.EXPENSE, TransactionCategory.UTILITY_COST, new BigDecimal("35000"), baseDateTime.withHour(10).withMinute(30));
        createTransaction("수도료", TransactionType.EXPENSE, TransactionCategory.UTILITY_COST, new BigDecimal("25000"), baseDateTime.withHour(11));
        
        // 인건비 - 직원 급여, 사회보험
        createTransaction("직원 일급", TransactionType.EXPENSE, TransactionCategory.LABOR_COST, new BigDecimal("150000"), baseDateTime.withHour(11).withMinute(30));
        createTransaction("사회보험료", TransactionType.EXPENSE, TransactionCategory.LABOR_COST, new BigDecimal("55000"), baseDateTime.withHour(12));
        
        // 세금 - 부가세, 소득세
        createTransaction("부가세", TransactionType.EXPENSE, TransactionCategory.TAX, new BigDecimal("65000"), baseDateTime.withHour(14));
        createTransaction("사업소득세", TransactionType.EXPENSE, TransactionCategory.TAX, new BigDecimal("35000"), baseDateTime.withHour(14).withMinute(30));
        
        // 환불 - 고객 불만족으로 인한 환불
        createTransaction("음료 환불", TransactionType.EXPENSE, TransactionCategory.REFUND, new BigDecimal("8500"), baseDateTime.withHour(16));
        createTransaction("케이크 환불", TransactionType.EXPENSE, TransactionCategory.REFUND, new BigDecimal("12000"), baseDateTime.withHour(17));
        
        // 기타 비용 - 청소용품, 마케팅
        createTransaction("청소용품", TransactionType.EXPENSE, TransactionCategory.OTHER_EXPENSE, new BigDecimal("25000"), baseDateTime.withHour(18));
        createTransaction("SNS 광고비", TransactionType.EXPENSE, TransactionCategory.OTHER_EXPENSE, new BigDecimal("80000"), baseDateTime.withHour(19));
    }

    private void createTestTransactions() {
        // 매출 데이터 (기존 방식 유지)
        createRevenueTransaction("카드 매출 1", TransactionCategory.CARD, new BigDecimal("300000"), baseDateTime);
        createRevenueTransaction("카드 매출 2", TransactionCategory.CARD, new BigDecimal("200000"), baseDateTime.plusHours(1));
        createRevenueTransaction("현금 매출", TransactionCategory.CASH, new BigDecimal("150000"), baseDateTime.plusHours(2));
        createRevenueTransaction("계좌이체 매출", TransactionCategory.BANK_TRANSFER, new BigDecimal("100000"), baseDateTime.plusHours(3));

        // 비용 데이터를 StoreTransaction으로 생성
        createTransaction("재료비 1", TransactionType.EXPENSE, TransactionCategory.MATERIAL_COST, new BigDecimal("200000"), baseDateTime.plusHours(4));
        createTransaction("재료비 2", TransactionType.EXPENSE, TransactionCategory.MATERIAL_COST, new BigDecimal("100000"), baseDateTime.plusHours(5));
        createTransaction("고정비", TransactionType.EXPENSE, TransactionCategory.FIXED_COST, new BigDecimal("80000"), baseDateTime.plusHours(6));
        createTransaction("공과금", TransactionType.EXPENSE, TransactionCategory.UTILITY_COST, new BigDecimal("30000"), baseDateTime.plusHours(7));
        createTransaction("인건비", TransactionType.EXPENSE, TransactionCategory.LABOR_COST, new BigDecimal("150000"), baseDateTime.plusHours(8));
        createTransaction("세금", TransactionType.EXPENSE, TransactionCategory.TAX, new BigDecimal("50000"), baseDateTime.plusHours(9));
        createTransaction("환불", TransactionType.EXPENSE, TransactionCategory.REFUND, new BigDecimal("20000"), baseDateTime.plusHours(10));
        createTransaction("기타 비용", TransactionType.EXPENSE, TransactionCategory.OTHER_EXPENSE, new BigDecimal("10000"), baseDateTime.plusHours(11));
    }

    private void createRevenueTransaction(String name, TransactionCategory category, BigDecimal amount, LocalDateTime transactionDate) {
        StoreTransactionEntity transaction = StoreTransactionEntity.builder()
                .name(name)
                .type(TransactionType.INCOME)
                .category(category)
                .amount(amount)
                .transactionDate(transactionDate)
                .store(testStore)
                .build();
        transactionRepository.save(transaction);
    }


    private void createTransaction(String name, TransactionType type, TransactionCategory category,
                                 BigDecimal amount, LocalDateTime transactionDate) {
        StoreTransactionEntity transaction = StoreTransactionEntity.builder()
                .name(name)
                .type(type)
                .category(category)
                .amount(amount)
                .transactionDate(transactionDate)
                .store(testStore)
                .build();
        transactionRepository.save(transaction);
    }

    @Test
    @WithMockUser
    @Transactional
    @Rollback(false)
    @DisplayName("매출 분석 전체 통합 테스트")
    void getSalesAnalyticsIntegrationTest() throws Exception {
        // given
        createTestData(); // 테스트 데이터 생성
        
        String startDate = "2024-01-15";
        String endDate = "2024-01-15";

        // when & then
        mockMvc.perform(get("/api/stores/{storeId}/analytics/sales", testStore.getId())
                        .param("startDate", startDate)
                        .param("endDate", endDate))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                
                // 기본 정보 검증
                .andExpect(jsonPath("$.storeId").value(testStore.getId()))
                .andExpect(jsonPath("$.storeName").value("통합테스트 가게"))
                
                // 매출 검증 - 실제 카페 데이터 기반
                .andExpect(jsonPath("$.totalRevenue").value(740000))   // 전체 매출
                .andExpect(jsonPath("$.cardRevenue").value(536000))     // 카드 매출 합계
                .andExpect(jsonPath("$.cashRevenue").value(116000))     // 현금 매출 합계
                .andExpect(jsonPath("$.bankTransferRevenue").value(88000))  // 계좌이체 매출
                .andExpect(jsonPath("$.otherRevenue").value(0))         // 기타 수입 없음
                
                // 비용 검증 - 실제 카페 운영비 기반
                .andExpect(jsonPath("$.totalExpense").value(1805500))   // 전체 비용
                .andExpect(jsonPath("$.materialCost").value(385000))    // 재료비 (원두, 우유, 베이커리)
                .andExpect(jsonPath("$.fixedCost").value(845000))       // 고정비 (임대료, 보험료)
                .andExpect(jsonPath("$.utilityCost").value(145000))     // 공과금 (전기, 가스, 수도)
                .andExpect(jsonPath("$.laborCost").value(205000))       // 인건비 (급여, 사회보험)
                .andExpect(jsonPath("$.taxAmount").value(100000))       // 세금 (부가세, 소득세)
                .andExpect(jsonPath("$.refundAmount").value(20500))     // 환불 (음료, 케이크)
                .andExpect(jsonPath("$.otherExpense").value(105000))    // 기타 (청소용품, 광고비)
                
                // 순이익 및 이익률 검증 - 적자 상황 (740000 - 1805500 = -1065500)
                .andExpect(jsonPath("$.netProfit").value(-1065500))
                .andExpect(jsonPath("$.profitMargin").value(-144.01))    // -1065500/740000*100
                
                // 카테고리별 매출 데이터 검증
                .andExpect(jsonPath("$.revenueByCategory.CARD").value(536000))
                .andExpect(jsonPath("$.revenueByCategory.CASH").value(116000))
                .andExpect(jsonPath("$.revenueByCategory.BANK_TRANSFER").value(88000))
                .andExpect(jsonPath("$.revenueByCategory.OTHER_INCOME").value(0))
                
                // 카테고리별 비용 데이터 검증
                .andExpect(jsonPath("$.expenseByCategory.MATERIAL_COST").value(385000))
                .andExpect(jsonPath("$.expenseByCategory.FIXED_COST").value(845000))
                .andExpect(jsonPath("$.expenseByCategory.UTILITY_COST").value(145000))
                .andExpect(jsonPath("$.expenseByCategory.LABOR_COST").value(205000))
                .andExpect(jsonPath("$.expenseByCategory.TAX").value(100000))
                .andExpect(jsonPath("$.expenseByCategory.REFUND").value(20500))
                .andExpect(jsonPath("$.expenseByCategory.OTHER_EXPENSE").value(105000));
    }

    @Test
    @WithMockUser
    @Transactional
    @Rollback(false)
    @DisplayName("매출 분석 - 기간 외 데이터 제외 테스트")
    void getSalesAnalyticsExcludeOutOfRangeData() throws Exception {
        // given
        createTestData(); // 테스트 데이터 생성
        
        // 기간 외 데이터 추가 (1월 14일 데이터 - 조회 기간에 포함되지 않음)
        createRevenueTransaction("전날 매출", TransactionCategory.CARD, new BigDecimal("500000"), baseDateTime.minusDays(1));
        createTransaction("전날 비용", TransactionType.EXPENSE, TransactionCategory.MATERIAL_COST, new BigDecimal("300000"), baseDateTime.minusDays(1));


        String startDate = "2024-01-15";
        String endDate = "2024-01-15";

        // when & then
        mockMvc.perform(get("/api/stores/{storeId}/analytics/sales", testStore.getId())
                        .param("startDate", startDate)
                        .param("endDate", endDate))
                .andDo(print())
                .andExpect(status().isOk())
                // 기간 외 데이터가 제외되어 1월 15일 데이터만 집계됨
                .andExpect(jsonPath("$.totalRevenue").value(740000))    // 1월 15일 매출만
                .andExpect(jsonPath("$.totalExpense").value(1805500));  // 1월 15일 비용만
    }

    @Test
    @WithMockUser
    @Transactional
    @Rollback(false)
    @DisplayName("매출 분석 - 데이터가 없는 기간 테스트")
    void getSalesAnalyticsWithNoData() throws Exception {
        // given
        createTestData(); // 테스트 데이터 생성
        
        String futureStartDate = "2024-12-01";
        String futureEndDate = "2024-12-31";

        // when & then
        mockMvc.perform(get("/api/stores/{storeId}/analytics/sales", testStore.getId())
                        .param("startDate", futureStartDate)
                        .param("endDate", futureEndDate))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalRevenue").value(0))
                .andExpect(jsonPath("$.totalExpense").value(0))
                .andExpect(jsonPath("$.netProfit").value(0))
                .andExpect(jsonPath("$.profitMargin").value(0))
                .andExpect(jsonPath("$.cardRevenue").value(0))
                .andExpect(jsonPath("$.materialCost").value(0));
    }

    @Test
    @WithMockUser
    @DisplayName("매출 분석 - 존재하지 않는 가게 ID 테스트")
    void getSalesAnalyticsWithNonExistentStore() throws Exception {
        // given
        int nonExistentStoreId = 99999;

        // when & then
        mockMvc.perform(get("/api/stores/{storeId}/analytics/sales", nonExistentStoreId))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}