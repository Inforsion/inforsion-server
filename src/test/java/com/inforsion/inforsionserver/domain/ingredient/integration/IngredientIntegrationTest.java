package com.inforsion.inforsionserver.domain.ingredient.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inforsion.inforsionserver.domain.ingredient.dto.request.IngredientCreateRequest;
import com.inforsion.inforsionserver.domain.ingredient.dto.request.IngredientUpdateRequest;
import com.inforsion.inforsionserver.domain.ingredient.entity.IngredientEntity;
import com.inforsion.inforsionserver.domain.ingredient.repository.IngredientRepository;
import com.inforsion.inforsionserver.domain.inventory.entity.InventoryEntity;
import com.inforsion.inforsionserver.domain.inventory.repository.InventoryRepository;
import com.inforsion.inforsionserver.domain.product.entity.ProductEntity;
import com.inforsion.inforsionserver.domain.product.repository.ProductRepository;
import com.inforsion.inforsionserver.domain.store.entity.StoreEntity;
import com.inforsion.inforsionserver.domain.store.repository.StoreRepository;
import com.inforsion.inforsionserver.domain.user.entity.UserEntity;
import com.inforsion.inforsionserver.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class IngredientIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StoreRepository storeRepository;

    private ProductEntity product;
    private InventoryEntity inventory;
    private IngredientEntity ingredient;

    @BeforeEach
    void setUp() {
        // Clear all repositories before each test
        ingredientRepository.deleteAll();
        inventoryRepository.deleteAll();
        productRepository.deleteAll();
        storeRepository.deleteAll();
        userRepository.deleteAll();

        UserEntity user = UserEntity.builder()
                .email("test@example.com")
                .password("password")
                .build();
        userRepository.save(user);

        StoreEntity store = StoreEntity.builder()
                .name("Test Store")
                .location("Test Location")
                .user(user)
                .build();
        storeRepository.save(store);

        product = ProductEntity.builder()
                .name("Test Product")
                .price(java.math.BigDecimal.valueOf(100.0))
                .store(store)
                .build();
        productRepository.save(product);

        inventory = InventoryEntity.builder()
                .name("Test Inventory")
                .quantity(java.math.BigDecimal.valueOf(100.0))
                .unit("g")
                .store(store)
                .build();
        inventoryRepository.save(inventory);

        ingredient = IngredientEntity.builder()
                .amountPerProduct(java.math.BigDecimal.valueOf(10.0))
                .unit("g")
                .description("Test Ingredient")
                .product(product)
                .inventory(inventory)
                .isActive(true)
                .build();
        ingredientRepository.save(ingredient);
    }

    @Test
    @DisplayName("재료 생성 통합 테스트 - 성공")
    void createIngredientIntegrationTest_success() throws Exception {
        IngredientCreateRequest request = new IngredientCreateRequest();
        request.setProductId(product.getId());
        request.setInventoryId(inventory.getId());
        request.setAmountPerProduct(java.math.BigDecimal.valueOf(5.0));
        request.setUnit("ml");
        request.setDescription("New Ingredient for Integration Test");

        mockMvc.perform(post("/api/v1/ingredients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.description").value("New Ingredient for Integration Test"));

        assertThat(ingredientRepository.findAll()).hasSize(2);
    }

    @Test
    @DisplayName("재료 단건 조회 통합 테스트 - 성공")
    void getIngredientIntegrationTest_success() throws Exception {
        mockMvc.perform(get("/api/v1/ingredients/{ingredientId}", ingredient.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ingredient.getId()))
                .andExpect(jsonPath("$.description").value(ingredient.getDescription()));
    }

    @Test
    @DisplayName("재료 검색 통합 테스트 - 상품 ID로 검색")
    void searchIngredientsIntegrationTest_byProductId() throws Exception {
        mockMvc.perform(get("/api/v1/ingredients/search")
                        .param("productId", product.getId().toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(ingredient.getId()));
    }

    @Test
    @DisplayName("재료 정보 수정 통합 테스트 - 성공")
    void updateIngredientIntegrationTest_success() throws Exception {
        IngredientUpdateRequest request = new IngredientUpdateRequest();
        request.setAmountPerProduct(java.math.BigDecimal.valueOf(15.0));
        request.setUnit("kg");
        request.setDescription("Updated Description for Integration Test");
        request.setIsActive(false);

        mockMvc.perform(put("/api/v1/ingredients/{ingredientId}", ingredient.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ingredient.getId()))
                .andExpect(jsonPath("$.description").value("Updated Description for Integration Test"))
                .andExpect(jsonPath("$.isActive").value(false));

        IngredientEntity updatedIngredient = ingredientRepository.findById(ingredient.getId()).orElseThrow();
        assertThat(updatedIngredient.getAmountPerProduct()).isEqualTo(java.math.BigDecimal.valueOf(15.0));
        assertThat(updatedIngredient.getUnit()).isEqualTo("kg");
        assertThat(updatedIngredient.getDescription()).isEqualTo("Updated Description for Integration Test");
        assertThat(updatedIngredient.getIsActive()).isEqualTo(false);
    }

    @Test
    @DisplayName("재료 삭제 통합 테스트 - 성공")
    void deleteIngredientIntegrationTest_success() throws Exception {
        mockMvc.perform(delete("/api/v1/ingredients/{ingredientId}", ingredient.getId()))
                .andExpect(status().isNoContent());

        assertThat(ingredientRepository.findById(ingredient.getId())).isEmpty();
    }
}
