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
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "Ingredient", description = "재료 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ingredients")
public class IngredientController {

    private final IngredientService ingredientService;

    @Operation(
            summary = "재료 생성", 
            description = "새로운 재료를 생성합니다. 재료명과 상품 ID 조합은 유니크해야 합니다."
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
            summary = "모든 재료 조회", 
            description = "시스템에 등록된 모든 재료 목록을 조회합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", 
                    description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = IngredientResponse.class))
            )
    })
    @GetMapping
    public ResponseEntity<List<IngredientResponse>> getAllIngredients() {
        List<IngredientResponse> response = ingredientService.getAllIngredients();
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "상품별 재료 조회", 
            description = "특정 상품에 사용되는 모든 재료 목록을 조회합니다. 레시피 관리 및 원가 계산에 활용됩니다."
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
            summary = "가게별 재료 조회", 
            description = "특정 가게에서 판매하는 모든 상품에 사용되는 재료를 중복 제거하여 조회합니다. 통합 발주 관리에 활용됩니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", 
                    description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = IngredientResponse.class))
            )
    })
    @GetMapping("/store/{storeId}")
    public ResponseEntity<List<IngredientResponse>> getIngredientsByStore(
            @Parameter(description = "가게 ID", required = true, example = "1")
            @PathVariable Integer storeId) {
        List<IngredientResponse> response = ingredientService.getIngredientsByStore(storeId);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "재료 검색", 
            description = "동적 조건을 사용하여 재료를 검색합니다. 재료명, 상품ID, 가게ID, 단위, 활성화 상태 등의 조건을 조합하여 사용할 수 있습니다."
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
            description = "특정 재료의 정보를 수정합니다. null이 아닌 필드만 업데이트되며, 이미지는 별도 엔드포인트로 처리합니다."
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
            description = "특정 재료를 삭제합니다. 재고가 존재하는 재료는 삭제할 수 없습니다."
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
            ),
            @ApiResponse(
                    responseCode = "400", 
                    description = "재고가 존재하여 삭제 불가",
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

    @Operation(
            summary = "공통 재료 조회", 
            description = "여러 상품에 공통으로 사용되는 재료를 조회합니다. 메뉴 조합 추천, 대량 발주 계획에 활용됩니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", 
                    description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = IngredientResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400", 
                    description = "잘못된 상품 ID 목록",
                    content = @Content
            )
    })
    @PostMapping("/common")
    public ResponseEntity<List<IngredientResponse>> getCommonIngredients(
            @Parameter(description = "상품 ID 목록", required = true, example = "[1, 2, 3]")
            @RequestBody List<Integer> productIds) {
        List<IngredientResponse> response = ingredientService.getCommonIngredients(productIds);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "재고 부족 재료 조회", 
            description = "특정 가게에서 재고량이 임계값 이하인 재료들을 조회합니다. 재고 알림 시스템, 자동 발주 시스템에 활용됩니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", 
                    description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = IngredientResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400", 
                    description = "잘못된 임계값",
                    content = @Content
            )
    })
    @GetMapping("/low-stock/{storeId}")
    public ResponseEntity<List<IngredientResponse>> getLowStockIngredients(
            @Parameter(description = "가게 ID", required = true, example = "1")
            @PathVariable Integer storeId,
            @Parameter(description = "재고 부족 임계값", required = true, example = "10.0")
            @RequestParam Double threshold) {
        List<IngredientResponse> response = ingredientService.getLowStockIngredients(storeId, threshold);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "상품별 재료 개수 조회", 
            description = "특정 상품에 사용되는 재료의 총 개수를 조회합니다. 상품의 복잡도 분석, 원가 계산 복잡도 측정에 활용됩니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", 
                    description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = Long.class))
            ),
            @ApiResponse(
                    responseCode = "404", 
                    description = "상품을 찾을 수 없음",
                    content = @Content
            )
    })
    @GetMapping("/count/product/{productId}")
    public ResponseEntity<Long> countIngredientsByProduct(
            @Parameter(description = "상품 ID", required = true, example = "1")
            @PathVariable Integer productId) {
        Long count = ingredientService.countIngredientsByProduct(productId);
        return ResponseEntity.ok(count);
    }

    @Operation(
            summary = "재료 이미지 업로드", 
            description = "특정 재료의 이미지를 S3에 업로드합니다. 지원 형식: JPEG, PNG, GIF. 최대 파일 크기: 10MB. 기존 이미지가 있는 경우 새 이미지로 교체됩니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", 
                    description = "이미지 업로드 성공",
                    content = @Content(schema = @Schema(implementation = IngredientResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400", 
                    description = "잘못된 파일 형식 또는 크기 초과",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404", 
                    description = "재료를 찾을 수 없음",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500", 
                    description = "S3 업로드 실패",
                    content = @Content
            )
    })
    @PostMapping("/{ingredientId}/image")
    public ResponseEntity<IngredientResponse> uploadIngredientImage(
            @Parameter(description = "재료 ID", required = true, example = "1")
            @PathVariable Integer ingredientId,
            @Parameter(description = "업로드할 이미지 파일", required = true)
            @RequestParam("file") MultipartFile file) {
        IngredientResponse response = ingredientService.uploadIngredientImage(ingredientId, file);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "재료 이미지 삭제", 
            description = "특정 재료의 이미지를 S3에서 삭제하고, 데이터베이스에서 이미지 정보를 제거합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204", 
                    description = "이미지 삭제 성공",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404", 
                    description = "재료를 찾을 수 없음",
                    content = @Content
            )
    })
    @DeleteMapping("/{ingredientId}/image")
    public ResponseEntity<Void> deleteIngredientImage(
            @Parameter(description = "재료 ID", required = true, example = "1")
            @PathVariable Integer ingredientId) {
        ingredientService.deleteIngredientImage(ingredientId);
        return ResponseEntity.noContent().build();
    }
}
