package com.inforsion.inforsionserver.domain.transaction.repository;

import com.inforsion.inforsionserver.domain.store.entity.StoreEntity;
import com.inforsion.inforsionserver.domain.transaction.entity.StoreTransactionEntity;
import com.inforsion.inforsionserver.domain.user.entity.UserEntity;
import com.inforsion.inforsionserver.global.config.QueryDslConfig;
import com.inforsion.inforsionserver.global.enums.TransactionCategory;
import com.inforsion.inforsionserver.global.enums.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("StoreTransactionRepository 테스트")
@Import({QueryDslConfig.class})
class StoreTransactionRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private StoreTransactionRepository transactionRepository;

    private StoreEntity testStore;
    private UserEntity testUser;
    private LocalDateTime baseDate;

    @BeforeEach
    void setUp() {
        // 테스트용 사용자 생성
        testUser = UserEntity.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password")
                .build();
        entityManager.persist(testUser);

        // 테스트용 가게 생성
        testStore = StoreEntity.builder()
                .name("테스트 가게")
                .location("테스트 주소")
                .phoneNumber("010-1234-5678")
                .user(testUser)
                .build();
        entityManager.persist(testStore);

        baseDate = LocalDateTime.of(2024, 1, 15, 10, 0);

        // 테스트 데이터 생성
        createTestTransactions();
        entityManager.flush();
    }

    private void createTestTransactions() {
        // 매출 데이터
        createTransaction("카드 매출", TransactionType.INCOME, TransactionCategory.CARD, 
                new BigDecimal("100000"), baseDate);
        createTransaction("현금 매출", TransactionType.INCOME, TransactionCategory.CASH, 
                new BigDecimal("50000"), baseDate.plusHours(1));
        
        // 비용 데이터
        createTransaction("재료비", TransactionType.EXPENSE, TransactionCategory.MATERIAL_COST, 
                new BigDecimal("30000"), baseDate.plusHours(2));
        createTransaction("세금", TransactionType.EXPENSE, TransactionCategory.TAX, 
                new BigDecimal("10000"), baseDate.plusHours(3));
        createTransaction("환불", TransactionType.EXPENSE, TransactionCategory.REFUND, 
                new BigDecimal("5000"), baseDate.plusHours(4));
        
        // 기간 외 데이터 (전날)
        createTransaction("전날 매출", TransactionType.INCOME, TransactionCategory.CARD, 
                new BigDecimal("20000"), baseDate.minusDays(1));
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
        entityManager.persist(transaction);
    }

    @Test
    @DisplayName("기간별 거래 내역 조회 테스트")
    void findByStoreIdAndDateRange() {
        // given
        LocalDateTime startDate = baseDate.minusHours(1);
        LocalDateTime endDate = baseDate.plusHours(5);

        // when
        List<StoreTransactionEntity> transactions = transactionRepository
                .findByStoreIdAndDateRange(testStore.getId(), startDate, endDate);

        // then
        assertThat(transactions).hasSize(5); // 기간 내 5건
        assertThat(transactions.get(0).getTransactionDate())
                .isAfterOrEqualTo(transactions.get(1).getTransactionDate()); // 내림차순 정렬
    }

    @Test
    @DisplayName("거래 유형별 금액 합계 계산 테스트")
    void sumAmountByStoreAndTypeAndDateRange() {
        // given
        LocalDateTime startDate = baseDate.minusHours(1);
        LocalDateTime endDate = baseDate.plusHours(5);

        // when
        BigDecimal incomeSum = transactionRepository
                .sumAmountByStoreAndTypeAndDateRange(testStore.getId(), TransactionType.INCOME, startDate, endDate);
        BigDecimal expenseSum = transactionRepository
                .sumAmountByStoreAndTypeAndDateRange(testStore.getId(), TransactionType.EXPENSE, startDate, endDate);

        // then
        assertThat(incomeSum).isEqualByComparingTo(new BigDecimal("150000")); // 100000 + 50000
        assertThat(expenseSum).isEqualByComparingTo(new BigDecimal("45000")); // 30000 + 10000 + 5000
    }

    @Test
    @DisplayName("거래 카테고리별 금액 합계 계산 테스트")
    void sumAmountByStoreAndCategoryAndDateRange() {
        // given
        LocalDateTime startDate = baseDate.minusHours(1);
        LocalDateTime endDate = baseDate.plusHours(5);

        // when
        BigDecimal cardSum = transactionRepository
                .sumAmountByStoreAndCategoryAndDateRange(testStore.getId(), TransactionCategory.CARD, startDate, endDate);
        BigDecimal cashSum = transactionRepository
                .sumAmountByStoreAndCategoryAndDateRange(testStore.getId(), TransactionCategory.CASH, startDate, endDate);
        BigDecimal materialSum = transactionRepository
                .sumAmountByStoreAndCategoryAndDateRange(testStore.getId(), TransactionCategory.MATERIAL_COST, startDate, endDate);
        BigDecimal taxSum = transactionRepository
                .sumAmountByStoreAndCategoryAndDateRange(testStore.getId(), TransactionCategory.TAX, startDate, endDate);
        BigDecimal refundSum = transactionRepository
                .sumAmountByStoreAndCategoryAndDateRange(testStore.getId(), TransactionCategory.REFUND, startDate, endDate);

        // then
        assertThat(cardSum).isEqualByComparingTo(new BigDecimal("100000"));
        assertThat(cashSum).isEqualByComparingTo(new BigDecimal("50000"));
        assertThat(materialSum).isEqualByComparingTo(new BigDecimal("30000"));
        assertThat(taxSum).isEqualByComparingTo(new BigDecimal("10000"));
        assertThat(refundSum).isEqualByComparingTo(new BigDecimal("5000"));
    }

    @Test
    @DisplayName("데이터가 없을 때 0 반환 테스트")
    void returnZeroWhenNoData() {
        // given
        LocalDateTime futureDate = baseDate.plusDays(10);
        LocalDateTime futureEndDate = baseDate.plusDays(11);

        // when
        BigDecimal result = transactionRepository
                .sumAmountByStoreAndTypeAndDateRange(testStore.getId(), TransactionType.INCOME, futureDate, futureEndDate);

        // then
        assertThat(result).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("다른 가게 데이터 제외 테스트")
    void excludeOtherStoreData() {
        // given
        StoreEntity anotherStore = StoreEntity.builder()
                .name("다른 가게")
                .location("다른 주소")
                .phoneNumber("010-9876-5432")
                .user(testUser)
                .build();
        entityManager.persist(anotherStore);
        
        createTransactionForStore("다른 가게 매출", TransactionType.INCOME, TransactionCategory.CARD,
                new BigDecimal("999999"), baseDate, anotherStore);
        entityManager.flush();

        LocalDateTime startDate = baseDate.minusHours(1);
        LocalDateTime endDate = baseDate.plusHours(5);

        // when
        BigDecimal result = transactionRepository
                .sumAmountByStoreAndTypeAndDateRange(testStore.getId(), TransactionType.INCOME, startDate, endDate);

        // then
        assertThat(result).isEqualByComparingTo(new BigDecimal("150000")); // 다른 가게 데이터 제외
    }

    private void createTransactionForStore(String name, TransactionType type, TransactionCategory category,
                                         BigDecimal amount, LocalDateTime transactionDate, StoreEntity store) {
        StoreTransactionEntity transaction = StoreTransactionEntity.builder()
                .name(name)
                .type(type)
                .category(category)
                .amount(amount)
                .transactionDate(transactionDate)
                .store(store)
                .build();
        entityManager.persist(transaction);
    }
}