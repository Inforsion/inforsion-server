package com.inforsion.inforsionserver.domain.ingredient.service;

import com.inforsion.inforsionserver.domain.ingredient.dto.request.IngredientCreateRequest;
import com.inforsion.inforsionserver.domain.ingredient.dto.request.IngredientSearchRequest;
import com.inforsion.inforsionserver.domain.ingredient.dto.request.IngredientUpdateRequest;
import com.inforsion.inforsionserver.domain.ingredient.dto.response.IngredientResponse;
import com.inforsion.inforsionserver.domain.ingredient.entity.IngredientEntity;
import com.inforsion.inforsionserver.domain.ingredient.repository.IngredientRepository;
import com.inforsion.inforsionserver.domain.inventory.entity.InventoryEntity;
import com.inforsion.inforsionserver.domain.inventory.repository.InventoryRepository;
import com.inforsion.inforsionserver.domain.product.entity.ProductEntity;
import com.inforsion.inforsionserver.domain.product.repository.ProductRepository;
import com.inforsion.inforsionserver.domain.store.entity.StoreEntity;
import com.inforsion.inforsionserver.global.error.exception.DuplicateIngredientException;
import com.inforsion.inforsionserver.global.error.exception.IngredientNotFoundException;
import com.inforsion.inforsionserver.global.error.exception.ProductNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IngredientServiceTest {

    @Mock
    private IngredientRepository ingredientRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private InventoryRepository inventoryRepository;

    @InjectMocks
    private IngredientService ingredientService;

    private StoreEntity store;
    private ProductEntity product;
    private InventoryEntity inventory;
    private IngredientEntity ingredient;

    @BeforeEach
    void setUp() {
        store = StoreEntity.builder()
                .id(1)
                .name("테스트 카페")
                .build();

        product = ProductEntity.builder()
                .id(1)
                .name("아메리카노")
                .store(store)
                .build();

        inventory = InventoryEntity.builder()
                .id(1)
                .name("원두")
                .build();

        ingredient = IngredientEntity.builder()
                .id(1)
                .amountPerProduct(BigDecimal.valueOf(10.0))
                .unit("g")
                .description("Test Description")
                .product(product)
                .inventory(inventory)
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("재료 생성 - 성공")
    void createIngredient_success() {
        IngredientCreateRequest request = IngredientCreateRequest.builder()
                .productId(1)
                .inventoryId(1)
                .amountPerProduct(BigDecimal.valueOf(10.0))
                .unit("g")
                .description("New Ingredient")
                .build();

        given(productRepository.findById(1)).willReturn(Optional.of(product));
        given(inventoryRepository.findById(1)).willReturn(Optional.of(inventory));
        given(ingredientRepository.existsByProductIdAndInventoryId(1, 1)).willReturn(false);
        given(ingredientRepository.save(any(IngredientEntity.class))).willReturn(ingredient);

        IngredientResponse response = ingredientService.createIngredient(request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1);
        assertThat(response.getInventoryName()).isEqualTo("원두");
        assertThat(response.getProductName()).isEqualTo("아메리카노");
        assertThat(response.getAmountPerProduct()).isEqualByComparingTo(BigDecimal.valueOf(10.0));
        verify(ingredientRepository, times(1)).save(any(IngredientEntity.class));
    }

    @Test
    @DisplayName("재료 생성 - 상품 없음")
    void createIngredient_productNotFound() {
        IngredientCreateRequest request = IngredientCreateRequest.builder()
                .productId(999)
                .inventoryId(1)
                .amountPerProduct(BigDecimal.valueOf(10.0))
                .unit("g")
                .description("Test Description")
                .build();

        given(productRepository.findById(999)).willReturn(Optional.empty());

        assertThatThrownBy(() -> ingredientService.createIngredient(request))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    @DisplayName("재료 생성 - 재고 없음")
    void createIngredient_inventoryNotFound() {
        IngredientCreateRequest request = IngredientCreateRequest.builder()
                .productId(1)
                .inventoryId(999)
                .amountPerProduct(BigDecimal.valueOf(10.0))
                .unit("g")
                .description("Test Description")
                .build();

        given(productRepository.findById(1)).willReturn(Optional.of(product));
        given(inventoryRepository.findById(999)).willReturn(Optional.empty());

        assertThatThrownBy(() -> ingredientService.createIngredient(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("재고를 찾을 수 없습니다");
    }

    @Test
    @DisplayName("재료 생성 - 중복 재료")
    void createIngredient_duplicateIngredient() {
        IngredientCreateRequest request = IngredientCreateRequest.builder()
                .productId(1)
                .inventoryId(1)
                .amountPerProduct(BigDecimal.valueOf(10.0))
                .unit("g")
                .description("Test Description")
                .build();

        given(productRepository.findById(1)).willReturn(Optional.of(product));
        given(inventoryRepository.findById(1)).willReturn(Optional.of(inventory));
        given(ingredientRepository.existsByProductIdAndInventoryId(1, 1)).willReturn(true);

        assertThatThrownBy(() -> ingredientService.createIngredient(request))
                .isInstanceOf(DuplicateIngredientException.class);
    }

    @Test
    @DisplayName("재료 단건 조회 - 성공")
    void getIngredient_success() {
        given(ingredientRepository.findById(1)).willReturn(Optional.of(ingredient));

        IngredientResponse response = ingredientService.getIngredient(1);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1);
    }

    @Test
    @DisplayName("재료 단건 조회 - 재료 없음")
    void getIngredient_notFound() {
        given(ingredientRepository.findById(99)).willReturn(Optional.empty());

        assertThatThrownBy(() -> ingredientService.getIngredient(99))
                .isInstanceOf(IngredientNotFoundException.class);
    }

    @Test
    @DisplayName("상품별 재료 조회 - 성공")
    void getIngredientsByProduct_success() {
        given(productRepository.existsById(1)).willReturn(true);
        given(ingredientRepository.findByProductId(1)).willReturn(Arrays.asList(ingredient));

        List<IngredientResponse> responses = ingredientService.getIngredientsByProduct(1);

        assertThat(responses).isNotEmpty();
        assertThat(responses.get(0).getId()).isEqualTo(1);
    }

    @Test
    @DisplayName("재료 검색 - 성공")
    void searchIngredients_success() {
        IngredientSearchRequest request = IngredientSearchRequest.builder()
                .productId(1)
                .unit("g")
                .build();

        given(ingredientRepository.searchIngredients(any(IngredientSearchRequest.class)))
                .willReturn(List.of(ingredient));

        List<IngredientResponse> responses = ingredientService.searchIngredients(request);

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getId()).isEqualTo(1);
        assertThat(responses.get(0).getUnit()).isEqualTo("g");
    }

    @Test
    @DisplayName("재료 정보 수정 - 성공")
    void updateIngredient_success() {
        IngredientUpdateRequest request = IngredientUpdateRequest.builder()
                .amountPerProduct(BigDecimal.valueOf(15.0))
                .unit("ml")
                .description("Updated Description")
                .isActive(false)
                .build();

        given(ingredientRepository.findById(1)).willReturn(Optional.of(ingredient));

        IngredientResponse response = ingredientService.updateIngredient(1, request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1);
        verify(ingredientRepository).findById(1);
    }

    @Test
    @DisplayName("재료 정보 수정 - 재료 없음")
    void updateIngredient_notFound() {
        IngredientUpdateRequest request = IngredientUpdateRequest.builder()
                .amountPerProduct(BigDecimal.valueOf(15.0))
                .build();
                
        given(ingredientRepository.findById(999)).willReturn(Optional.empty());

        assertThatThrownBy(() -> ingredientService.updateIngredient(999, request))
                .isInstanceOf(IngredientNotFoundException.class);
    }

    @Test
    @DisplayName("재료 정보 수정 - 재고 변경 시 중복")
    void updateIngredient_duplicateInventory() {
        IngredientUpdateRequest request = new IngredientUpdateRequest();
        request.setInventoryId(2); // Change to a different inventory ID

        InventoryEntity newInventory = InventoryEntity.builder().id(2).name("New Inventory").build();

        given(ingredientRepository.findById(1)).willReturn(Optional.of(ingredient));
        given(ingredientRepository.existsByProductIdAndInventoryId(ingredient.getProduct().getId(), request.getInventoryId()))
                .willReturn(true);

        assertThatThrownBy(() -> ingredientService.updateIngredient(1, request))
                .isInstanceOf(DuplicateIngredientException.class);
    }

    @Test
    @DisplayName("재료 정보 수정 - 재고 변경 성공")
    void updateIngredient_changeInventory_success() {
        IngredientUpdateRequest request = new IngredientUpdateRequest();
        request.setInventoryId(2);
        request.setAmountPerProduct(BigDecimal.valueOf(20.0));
        request.setUnit("ml");

        InventoryEntity newInventory = InventoryEntity.builder().id(2).name("New Inventory").build();

        given(ingredientRepository.findById(1)).willReturn(Optional.of(ingredient));
        given(ingredientRepository.existsByProductIdAndInventoryId(ingredient.getProduct().getId(), request.getInventoryId()))
                .willReturn(false);
        given(inventoryRepository.findById(2)).willReturn(Optional.of(newInventory));

        IngredientResponse response = ingredientService.updateIngredient(1, request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1);
        verify(ingredientRepository).findById(1);
        verify(inventoryRepository).findById(2);
    }

    @Test
    @DisplayName("재료 삭제 - 성공")
    void deleteIngredient_success() {
        given(ingredientRepository.findById(1)).willReturn(Optional.of(ingredient));
        willDoNothing().given(ingredientRepository).delete(ingredient);

        ingredientService.deleteIngredient(1);

        verify(ingredientRepository, times(1)).delete(ingredient);
    }

    @Test
    @DisplayName("재료 삭제 - 재료 없음")
    void deleteIngredient_notFound() {
        given(ingredientRepository.findById(99)).willReturn(Optional.empty());

        assertThatThrownBy(() -> ingredientService.deleteIngredient(99))
                .isInstanceOf(IngredientNotFoundException.class);
    }
}