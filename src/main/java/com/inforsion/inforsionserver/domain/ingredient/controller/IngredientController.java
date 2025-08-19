package com.inforsion.inforsionserver.domain.ingredient.controller;

import com.inforsion.inforsionserver.domain.ingredient.dto.request.IngredientCreateRequest;
import com.inforsion.inforsionserver.domain.ingredient.dto.request.IngredientSearchRequest;
import com.inforsion.inforsionserver.domain.ingredient.dto.request.IngredientUpdateRequest;
import com.inforsion.inforsionserver.domain.ingredient.dto.response.IngredientResponse;
import com.inforsion.inforsionserver.domain.ingredient.service.IngredientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "Ingredient", description = "재료 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ingredients")
public class IngredientController {

    private final IngredientService ingredientService;

    @Operation(summary = "재료 생성", description = "새로운 재료를 생성합니다.")
    @PostMapping
    public ResponseEntity<IngredientResponse> createIngredient(
            @Valid @RequestBody IngredientCreateRequest request) {
        IngredientResponse response = ingredientService.createIngredient(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "재료 단건 조회", description = "특정 재료의 상세 정보를 조회합니다.")
    @GetMapping("/{ingredientId}")
    public ResponseEntity<IngredientResponse> getIngredient(@PathVariable Integer ingredientId) {
        IngredientResponse response = ingredientService.getIngredient(ingredientId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "모든 재료 조회", description = "모든 재료 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<IngredientResponse>> getAllIngredients() {
        List<IngredientResponse> response = ingredientService.getAllIngredients();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "상품별 재료 조회", description = "특정 상품에 사용되는 재료 목록을 조회합니다.")
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<IngredientResponse>> getIngredientsByProduct(@PathVariable Integer productId) {
        List<IngredientResponse> response = ingredientService.getIngredientsByProduct(productId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "가게별 재료 조회", description = "특정 가게에서 사용하는 모든 재료를 조회합니다.")
    @GetMapping("/store/{storeId}")
    public ResponseEntity<List<IngredientResponse>> getIngredientsByStore(@PathVariable Integer storeId) {
        List<IngredientResponse> response = ingredientService.getIngredientsByStore(storeId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "재료 검색", description = "조건에 따라 재료를 검색합니다.")
    @PostMapping("/search")
    public ResponseEntity<List<IngredientResponse>> searchIngredients(
            @Valid @RequestBody IngredientSearchRequest request) {
        List<IngredientResponse> response = ingredientService.searchIngredients(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "재료 정보 수정", description = "특정 재료의 정보를 수정합니다.")
    @PutMapping("/{ingredientId}")
    public ResponseEntity<IngredientResponse> updateIngredient(
            @PathVariable Integer ingredientId,
            @Valid @RequestBody IngredientUpdateRequest request) {
        IngredientResponse response = ingredientService.updateIngredient(ingredientId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "재료 삭제", description = "특정 재료를 삭제합니다.")
    @DeleteMapping("/{ingredientId}")
    public ResponseEntity<Void> deleteIngredient(@PathVariable Integer ingredientId) {
        ingredientService.deleteIngredient(ingredientId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "공통 재료 조회", description = "여러 상품에 공통으로 사용되는 재료를 조회합니다.")
    @PostMapping("/common")
    public ResponseEntity<List<IngredientResponse>> getCommonIngredients(
            @RequestBody List<Integer> productIds) {
        List<IngredientResponse> response = ingredientService.getCommonIngredients(productIds);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "재고 부족 재료 조회", description = "재고가 부족한 재료 목록을 조회합니다.")
    @GetMapping("/low-stock/{storeId}")
    public ResponseEntity<List<IngredientResponse>> getLowStockIngredients(
            @PathVariable Integer storeId,
            @RequestParam Double threshold) {
        List<IngredientResponse> response = ingredientService.getLowStockIngredients(storeId, threshold);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "상품별 재료 개수 조회", description = "특정 상품에 사용되는 재료의 개수를 조회합니다.")
    @GetMapping("/count/product/{productId}")
    public ResponseEntity<Long> countIngredientsByProduct(@PathVariable Integer productId) {
        Long count = ingredientService.countIngredientsByProduct(productId);
        return ResponseEntity.ok(count);
    }

    @Operation(summary = "재료 이미지 업로드", description = "특정 재료의 이미지를 업로드합니다.")
    @PostMapping("/{ingredientId}/image")
    public ResponseEntity<IngredientResponse> uploadIngredientImage(
            @PathVariable Integer ingredientId,
            @RequestParam("file") MultipartFile file) {
        IngredientResponse response = ingredientService.uploadIngredientImage(ingredientId, file);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "재료 이미지 삭제", description = "특정 재료의 이미지를 삭제합니다.")
    @DeleteMapping("/{ingredientId}/image")
    public ResponseEntity<Void> deleteIngredientImage(@PathVariable Integer ingredientId) {
        ingredientService.deleteIngredientImage(ingredientId);
        return ResponseEntity.noContent().build();
    }
}
