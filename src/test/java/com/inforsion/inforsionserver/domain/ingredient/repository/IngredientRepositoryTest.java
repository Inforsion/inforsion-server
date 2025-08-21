package com.inforsion.inforsionserver.domain.ingredient.repository;

import com.inforsion.inforsionserver.domain.ingredient.dto.request.IngredientSearchRequest;
import com.inforsion.inforsionserver.domain.ingredient.entity.IngredientEntity;
import com.inforsion.inforsionserver.domain.inventory.entity.InventoryEntity;
import com.inforsion.inforsionserver.domain.product.entity.ProductEntity;
import com.inforsion.inforsionserver.domain.store.entity.StoreEntity;
import com.inforsion.inforsionserver.domain.user.entity.UserEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.context.TestConfiguration;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import({IngredientRepositoryTest.TestConfig.class, IngredientRepositoryImpl.class})
class IngredientRepositoryTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        public JPAQueryFactory jpaQueryFactory(EntityManager entityManager) {
            return new JPAQueryFactory(entityManager);
        }
    }

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private IngredientRepository ingredientRepository;

    @Test
    @DisplayName("재료 저장 및 조회")
    void saveAndFindIngredient() {
        // Given
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        
        UserEntity user = UserEntity.builder()
                .username("testuser" + uuid)
                .email("test" + uuid + "@example.com")
                .password("password")
                .build();
        user = entityManager.persistAndFlush(user);

        StoreEntity store = StoreEntity.builder()
                .name("Test Store " + uuid)
                .location("Test Location")
                .description("Test Description")
                .user(user)
                .businessRegistrationNumber("BRN" + uuid)
                .build();
        store = entityManager.persistAndFlush(store);

        ProductEntity product = ProductEntity.builder()
                .name("Test Product " + uuid)
                .description("Test Description")
                .price(BigDecimal.valueOf(1000))
                .category("TEST")
                .store(store)
                .build();
        product = entityManager.persistAndFlush(product);

        InventoryEntity inventory = InventoryEntity.builder()
                .name("Test Inventory " + uuid)
                .quantity(BigDecimal.valueOf(100))
                .unit("g")
                .store(store)
                .build();
        inventory = entityManager.persistAndFlush(inventory);

        IngredientEntity ingredient = IngredientEntity.builder()
                .amountPerProduct(BigDecimal.valueOf(10))
                .unit("g")
                .description("Test Ingredient")
                .product(product)
                .inventory(inventory)
                .isActive(true)
                .build();

        // When
        IngredientEntity savedIngredient = ingredientRepository.save(ingredient);
        entityManager.flush();

        // Then
        assertThat(savedIngredient.getId()).isNotNull();
        
        IngredientEntity foundIngredient = ingredientRepository.findById(savedIngredient.getId()).orElse(null);
        assertThat(foundIngredient).isNotNull();
        assertThat(foundIngredient.getDescription()).isEqualTo("Test Ingredient");
        assertThat(foundIngredient.getAmountPerProduct()).isEqualByComparingTo(BigDecimal.valueOf(10));
    }

    @Test
    @DisplayName("상품 ID로 재료 조회")
    void findByProductId() {
        // Given
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        
        UserEntity user = UserEntity.builder()
                .username("testuser" + uuid)
                .email("test" + uuid + "@example.com")
                .password("password")
                .build();
        user = entityManager.persistAndFlush(user);

        StoreEntity store = StoreEntity.builder()
                .name("Test Store " + uuid)
                .location("Test Location")
                .description("Test Description")
                .user(user)
                .businessRegistrationNumber("BRN" + uuid)
                .build();
        store = entityManager.persistAndFlush(store);

        ProductEntity product = ProductEntity.builder()
                .name("Test Product " + uuid)
                .description("Test Description")
                .price(BigDecimal.valueOf(1000))
                .category("TEST")
                .store(store)
                .build();
        product = entityManager.persistAndFlush(product);

        InventoryEntity inventory1 = InventoryEntity.builder()
                .name("Test Inventory 1 " + uuid)
                .quantity(BigDecimal.valueOf(100))
                .unit("g")
                .store(store)
                .build();
        inventory1 = entityManager.persistAndFlush(inventory1);

        InventoryEntity inventory2 = InventoryEntity.builder()
                .name("Test Inventory 2 " + uuid)
                .quantity(BigDecimal.valueOf(200))
                .unit("ml")
                .store(store)
                .build();
        inventory2 = entityManager.persistAndFlush(inventory2);

        IngredientEntity ingredient1 = IngredientEntity.builder()
                .amountPerProduct(BigDecimal.valueOf(10))
                .unit("g")
                .description("Test Ingredient 1")
                .product(product)
                .inventory(inventory1)
                .isActive(true)
                .build();
        ingredientRepository.save(ingredient1);

        IngredientEntity ingredient2 = IngredientEntity.builder()
                .amountPerProduct(BigDecimal.valueOf(20))
                .unit("ml")
                .description("Test Ingredient 2")
                .product(product)
                .inventory(inventory2)
                .isActive(true)
                .build();
        ingredientRepository.save(ingredient2);

        entityManager.flush();

        // When
        List<IngredientEntity> ingredients = ingredientRepository.findByProductId(product.getId());

        // Then
        assertThat(ingredients).hasSize(2);
        assertThat(ingredients).extracting(IngredientEntity::getDescription)
                .containsExactlyInAnyOrder("Test Ingredient 1", "Test Ingredient 2");
    }

    @Test
    @DisplayName("QueryDSL 동적 검색 테스트")
    void searchIngredients_queryDsl() {
        // Given
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        
        UserEntity user = UserEntity.builder()
                .username("testuser" + uuid)
                .email("test" + uuid + "@example.com")
                .password("password")
                .build();
        user = entityManager.persistAndFlush(user);

        StoreEntity store = StoreEntity.builder()
                .name("Test Store " + uuid)
                .location("Test Location")
                .description("Test Description")
                .user(user)
                .businessRegistrationNumber("BRN" + uuid)
                .build();
        store = entityManager.persistAndFlush(store);

        ProductEntity product = ProductEntity.builder()
                .name("Test Product " + uuid)
                .description("Test Description")
                .price(BigDecimal.valueOf(1000))
                .category("TEST")
                .store(store)
                .build();
        product = entityManager.persistAndFlush(product);

        InventoryEntity inventory = InventoryEntity.builder()
                .name("Test Inventory " + uuid)
                .quantity(BigDecimal.valueOf(100))
                .unit("g")
                .store(store)
                .build();
        inventory = entityManager.persistAndFlush(inventory);

        IngredientEntity ingredient = IngredientEntity.builder()
                .amountPerProduct(BigDecimal.valueOf(10))
                .unit("g")
                .description("Test Ingredient")
                .product(product)
                .inventory(inventory)
                .isActive(true)
                .build();
        ingredientRepository.save(ingredient);
        entityManager.flush();

        // When
        IngredientSearchRequest request = IngredientSearchRequest.builder()
                .productId(product.getId())
                .unit("g")
                .isActive(true)
                .build();

        List<IngredientEntity> results = ingredientRepository.searchIngredients(request);

        // Then
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getDescription()).isEqualTo("Test Ingredient");
        assertThat(results.get(0).getUnit()).isEqualTo("g");
        assertThat(results.get(0).getIsActive()).isTrue();
    }
}