package com.inforsion.inforsionserver.domain.ingredient.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inforsion.inforsionserver.domain.ingredient.dto.request.IngredientCreateRequest;
import com.inforsion.inforsionserver.domain.ingredient.dto.request.IngredientSearchRequest;
import com.inforsion.inforsionserver.domain.ingredient.dto.request.IngredientUpdateRequest;
import com.inforsion.inforsionserver.domain.ingredient.dto.response.IngredientResponse;
import com.inforsion.inforsionserver.domain.ingredient.service.IngredientService;
import com.inforsion.inforsionserver.TestSecurityConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(IngredientController.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestSecurityConfig.class)
class IngredientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IngredientService ingredientService;

    private IngredientResponse createIngredientResponse() {
        return IngredientResponse.builder()
                .id(1)
                .inventoryId(1)
                .inventoryName("원두")
                .amountPerProduct(BigDecimal.valueOf(10.0))
                .unit("kg")
                .description("Test Description")
                .isActive(true)
                .productId(1)
                .productName("아메리카노")
                .storeId(1)
                .storeName("테스트 카페")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("재료 생성 - 성공")
    void createIngredient_success() throws Exception {
        IngredientCreateRequest request = IngredientCreateRequest.builder()
                .productId(1)
                .inventoryId(1)
                .amountPerProduct(BigDecimal.valueOf(5.0))
                .unit("g")
                .description("New Description")
                .build();

        IngredientResponse response = createIngredientResponse();
        given(ingredientService.createIngredient(any(IngredientCreateRequest.class))).willReturn(response);

        mockMvc.perform(post("/api/v1/ingredients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(response.getId()))
                .andExpect(jsonPath("$.inventoryName").value(response.getInventoryName()))
                .andExpect(jsonPath("$.productName").value(response.getProductName()))
                .andExpect(jsonPath("$.description").value(response.getDescription()));
    }

    @Test
    @DisplayName("재료 생성 - 유효성 검증 실패 (필수 필드 누락)")
    void createIngredient_validation_fail() throws Exception {
        IngredientCreateRequest request = IngredientCreateRequest.builder()
                .productId(null) // 필수 필드 누락
                .inventoryId(1)
                .amountPerProduct(BigDecimal.valueOf(5.0))
                .unit("g")
                .description("New Description")
                .build();

        mockMvc.perform(post("/api/v1/ingredients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("재료 단건 조회 - 성공")
    void getIngredient_success() throws Exception {
        IngredientResponse response = createIngredientResponse();
        given(ingredientService.getIngredient(any(Integer.class))).willReturn(response);

        mockMvc.perform(get("/api/v1/ingredients/{ingredientId}", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response.getId()))
                .andExpect(jsonPath("$.inventoryName").value(response.getInventoryName()))
                .andExpect(jsonPath("$.productName").value(response.getProductName()))
                .andExpect(jsonPath("$.description").value(response.getDescription()));
    }

    @Test
    @DisplayName("상품별 재료 목록 조회 - 성공")
    void getIngredientsByProduct_success() throws Exception {
        List<IngredientResponse> responses = List.of(createIngredientResponse());
        given(ingredientService.getIngredientsByProduct(any(Integer.class))).willReturn(responses);

        mockMvc.perform(get("/api/v1/ingredients/product/{productId}", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(responses.get(0).getId()))
                .andExpect(jsonPath("$[0].inventoryName").value(responses.get(0).getInventoryName()))
                .andExpect(jsonPath("$[0].productName").value(responses.get(0).getProductName()));
    }

    @Test
    @DisplayName("재료 검색 - 성공")
    void searchIngredients_success() throws Exception {
        IngredientSearchRequest request = IngredientSearchRequest.builder()
                .productId(1)
                .unit("g")
                .build();

        IngredientResponse response = createIngredientResponse();
        given(ingredientService.searchIngredients(any(IngredientSearchRequest.class))).willReturn(Collections.singletonList(response));

        mockMvc.perform(post("/api/v1/ingredients/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(response.getId()))
                .andExpect(jsonPath("$[0].inventoryName").value(response.getInventoryName()))
                .andExpect(jsonPath("$[0].productName").value(response.getProductName()))
                .andExpect(jsonPath("$[0].description").value(response.getDescription()));
    }

    @Test
    @DisplayName("재료 정보 수정 - 성공")
    void updateIngredient_success() throws Exception {
        IngredientUpdateRequest request = IngredientUpdateRequest.builder()
                .inventoryId(2)
                .amountPerProduct(BigDecimal.valueOf(15.0))
                .unit("ml")
                .description("Updated Description")
                .build();

        IngredientResponse response = createIngredientResponse();
        given(ingredientService.updateIngredient(eq(1), any(IngredientUpdateRequest.class))).willReturn(response);

        mockMvc.perform(put("/api/v1/ingredients/{ingredientId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response.getId()))
                .andExpect(jsonPath("$.inventoryName").value(response.getInventoryName()))
                .andExpect(jsonPath("$.description").value(response.getDescription()));
    }

    @Test
    @DisplayName("재료 삭제 - 성공")
    void deleteIngredient_success() throws Exception {
        doNothing().when(ingredientService).deleteIngredient(any(Integer.class));

        mockMvc.perform(delete("/api/v1/ingredients/{ingredientId}", 1))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("재료 생성 - 음수 값 검증")
    void createIngredient_negativeAmount_fail() throws Exception {
        IngredientCreateRequest request = IngredientCreateRequest.builder()
                .productId(1)
                .inventoryId(1)
                .amountPerProduct(BigDecimal.valueOf(-5.0)) // 음수 값
                .unit("g")
                .description("New Description")
                .build();

        mockMvc.perform(post("/api/v1/ingredients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("재료 검색 - 빈 결과")
    void searchIngredients_empty_result() throws Exception {
        IngredientSearchRequest request = IngredientSearchRequest.builder()
                .productId(999) // 존재하지 않는 상품 ID
                .build();

        given(ingredientService.searchIngredients(any(IngredientSearchRequest.class))).willReturn(Collections.emptyList());

        mockMvc.perform(post("/api/v1/ingredients/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }
}