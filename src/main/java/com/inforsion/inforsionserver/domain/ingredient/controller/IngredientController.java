package com.inforsion.inforsionserver.domain.ingredient.controller;

import com.inforsion.inforsionserver.domain.ingredient.dto.request.IngredientCreateRequest;
import com.inforsion.inforsionserver.domain.ingredient.dto.request.IngredientSearchRequest;
import com.inforsion.inforsionserver.domain.ingredient.dto.request.IngredientUpdateRequest;
import com.inforsion.inforsionserver.domain.ingredient.dto.response.IngredientResponse;
import com.inforsion.inforsionserver.domain.ingredient.service.IngredientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Ingredient", description = "메뉴 재료 관리 API - 메뉴(상품)에 들어가는 재료 정보를 관리합니다")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ingredients")
public class IngredientController {

    private final IngredientService ingredientService;

    @Operation(
            summary = "재료 생성", 
            description = "메뉴에 들어갈 새로운 재료를 생성합니다. 재료명과 상품 ID 조합은 유니크해야 합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201", 
                    description = "재료 생성 성공",
                    content = @Content(schema = @Schema(implementation = IngredientResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400", 
                    description = "잘못된 입력값",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404", 
                    description = "상품을 찾을 수 없음",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "409", 
                    description = "이미 존재하는 재료",
                    content = @Content
            )
    })
    @PostMapping
    public ResponseEntity<IngredientResponse> createIngredient(
            @Parameter(description = "재료 생성 요청 데이터", required = true)
            @Valid @RequestBody IngredientCreateRequest request) {
        IngredientResponse response = ingredientService.createIngredient(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "재료 단건 조회", 
            description = "재료 ID로 특정 재료의 상세 정보를 조회합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", 
                    description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = IngredientResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404", 
                    description = "재료를 찾을 수 없음",
                    content = @Content
            )
    })
    @GetMapping("/{ingredientId}")
    public ResponseEntity<IngredientResponse> getIngredient(
            @Parameter(description = "재료 ID", required = true, example = "1")
            @PathVariable Integer ingredientId) {
        IngredientResponse response = ingredientService.getIngredient(ingredientId);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "상품별 재료 조회", 
            description = "특정 메뉴(상품)에 사용되는 모든 재료 목록을 조회합니다. 레시피 관리에 활용됩니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", 
                    description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = IngredientResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404", 
                    description = "상품을 찾을 수 없음",
                    content = @Content
            )
    })
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<IngredientResponse>> getIngredientsByProduct(
            @Parameter(description = "상품 ID", required = true, example = "1")
            @PathVariable Integer productId) {
        List<IngredientResponse> response = ingredientService.getIngredientsByProduct(productId);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "재료 검색", 
            description = "동적 조건을 사용하여 재료를 검색합니다. 재료명, 상품ID, 단위, 활성화 상태 등의 조건을 조합하여 사용할 수 있습니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", 
                    description = "검색 성공",
                    content = @Content(schema = @Schema(implementation = IngredientResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400", 
                    description = "잘못된 검색 조건",
                    content = @Content
            )
    })
    @PostMapping("/search")
    public ResponseEntity<List<IngredientResponse>> searchIngredients(
            @Parameter(description = "검색 조건 데이터", required = true)
            @Valid @RequestBody IngredientSearchRequest request) {
        List<IngredientResponse> response = ingredientService.searchIngredients(request);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "재료 정보 수정", 
            description = "특정 재료의 정보를 수정합니다. null이 아닌 필드만 업데이트됩니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", 
                    description = "수정 성공",
                    content = @Content(schema = @Schema(implementation = IngredientResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400", 
                    description = "잘못된 입력값",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404", 
                    description = "재료를 찾을 수 없음",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "409", 
                    description = "이미 존재하는 재료명",
                    content = @Content
            )
    })
    @PutMapping("/{ingredientId}")
    public ResponseEntity<IngredientResponse> updateIngredient(
            @Parameter(description = "재료 ID", required = true, example = "1")
            @PathVariable Integer ingredientId,
            @Parameter(description = "재료 수정 데이터", required = true)
            @Valid @RequestBody IngredientUpdateRequest request) {
        IngredientResponse response = ingredientService.updateIngredient(ingredientId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "재료 삭제", 
            description = "특정 재료를 삭제합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204", 
                    description = "삭제 성공",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404", 
                    description = "재료를 찾을 수 없음",
                    content = @Content
            )
    })
    @DeleteMapping("/{ingredientId}")
    public ResponseEntity<Void> deleteIngredient(
            @Parameter(description = "재료 ID", required = true, example = "1")
            @PathVariable Integer ingredientId) {
        ingredientService.deleteIngredient(ingredientId);
        return ResponseEntity.noContent().build();
    }

}