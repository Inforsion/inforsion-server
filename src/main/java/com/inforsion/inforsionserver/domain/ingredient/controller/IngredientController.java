package com.inforsion.inforsionserver.domain.ingredient.controller;

import com.inforsion.inforsionserver.domain.ingredient.dto.request.IngredientCreateRequest;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Ingredient", description = "재료 관리 API")
@RestController
@RequestMapping("/api/v1/ingredients")
@RequiredArgsConstructor
public class IngredientController {

    private final IngredientService ingredientService;

    @Operation(
            summary = "재료 등록",
            description = "재료 마스터를 등록합니다. 같은 매장에 중복된 재료명은 등록할 수 없습니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "재료 등록 성공",
                    content = @Content(schema = @Schema(implementation = IngredientResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "입력값 오류",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "중복된 재료명",
                    content = @Content
            )
    })
    @PostMapping
    public ResponseEntity<IngredientResponse> createIngredient(
            @Parameter(description = "재료 등록 요청 데이터", required = true)
            @Valid @RequestBody IngredientCreateRequest request
    ) {
        IngredientResponse response = ingredientService.createIngredient(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "재료 단건 조회", description = "재료 ID로 재료 정보를 조회합니다.")
    @GetMapping("/{ingredientId}")
    public ResponseEntity<IngredientResponse> getIngredient(
            @Parameter(description = "재료 ID", required = true) @PathVariable Integer ingredientId
    ) {
        return ResponseEntity.ok(ingredientService.getIngredient(ingredientId));
    }

    @Operation(summary = "매장별 재료 목록 조회", description = "특정 매장의 활성화된 재료 목록을 페이징 처리하여 조회합니다.")
    @GetMapping("/store/{storeId}")
    public ResponseEntity<Page<IngredientResponse>> getIngredientsByStore(
            @Parameter(description = "매장 ID", required = true) @PathVariable Integer storeId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(ingredientService.getIngredientsByStore(storeId, pageable));
    }

    @Operation(summary = "재료 수정", description = "재료 정보를 부분 수정합니다. 필요한 필드만 전달하면 됩니다.")
    @PutMapping("/{ingredientId}")
    public ResponseEntity<IngredientResponse> updateIngredient(
            @Parameter(description = "재료 ID", required = true) @PathVariable Integer ingredientId,
            @Parameter(description = "수정 요청 데이터", required = true)
            @Valid @RequestBody IngredientUpdateRequest request
    ) {
        IngredientResponse response = ingredientService.updateIngredient(ingredientId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "재료 삭제", description = "재료를 소프트 삭제합니다 (isActive = false).")
    @DeleteMapping("/{ingredientId}")
    public ResponseEntity<Void> deleteIngredient(
            @Parameter(description = "재료 ID", required = true) @PathVariable Integer ingredientId
    ) {
        ingredientService.deleteIngredient(ingredientId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "재료 이미지 업로드", description = "ingredients 경로로 S3에 업로드합니다.")
    @PostMapping(value = "/{ingredientId}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<IngredientResponse> uploadImage(
            @Parameter(description = "재료 ID", required = true) @PathVariable Integer ingredientId,
            @Parameter(description = "업로드할 이미지", required = true) @RequestPart("image") MultipartFile imageFile
    ) {
        IngredientResponse response = ingredientService.uploadImage(ingredientId, imageFile);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "재료 이미지 수정", description = "기존 이미지를 삭제하고 새 파일을 업로드합니다.")
    @PutMapping(value = "/{ingredientId}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<IngredientResponse> updateImage(
            @Parameter(description = "재료 ID", required = true) @PathVariable Integer ingredientId,
            @Parameter(description = "새 이미지", required = true) @RequestPart("image") MultipartFile imageFile
    ) {
        IngredientResponse response = ingredientService.updateImage(ingredientId, imageFile);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "재료 이미지 조회", description = "S3 URL을 반환합니다. 이미지가 없으면 404를 반환합니다.")
    @GetMapping("/{ingredientId}/image")
    public ResponseEntity<IngredientResponse> getImage(
            @Parameter(description = "재료 ID", required = true) @PathVariable Integer ingredientId
    ) {
        IngredientResponse response = ingredientService.getImage(ingredientId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "재료 이미지 삭제", description = "S3에서 파일을 삭제하고 DB의 이미지 URL을 비웁니다.")
    @DeleteMapping("/{ingredientId}/image")
    public ResponseEntity<Void> deleteImage(
            @Parameter(description = "재료 ID", required = true) @PathVariable Integer ingredientId
    ) {
        ingredientService.deleteImage(ingredientId);
        return ResponseEntity.noContent().build();
    }
}