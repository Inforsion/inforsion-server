package com.inforsion.inforsionserver.domain.ingredient_dev.controller;

import com.inforsion.inforsionserver.domain.ingredient_dev.dto.request.IngredientDevCreateRequest;
import com.inforsion.inforsionserver.domain.ingredient_dev.dto.request.IngredientDevUpdateRequest;
import com.inforsion.inforsionserver.domain.ingredient_dev.dto.response.IngredientDevResponse;
import com.inforsion.inforsionserver.domain.ingredient_dev.service.IngredientDevService;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Ingredient Dev", description = "재료 관리 신규 API - 단일 재료 등록")
@RestController
@RequestMapping("/api/v1/ingredient-dev")
@RequiredArgsConstructor
public class IngredientDevController {

    private final IngredientDevService ingredientDevService;

    @Operation(
            summary = "재료 등록",
            description = "재료명, 재고 가격, 1개당 재고 용량, 재고 수를 등록합니다. 이미지 업로드는 추후 활성화 예정입니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "재료 등록 성공",
                    content = @Content(schema = @Schema(implementation = IngredientDevResponse.class))
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
    public ResponseEntity<IngredientDevResponse> createIngredient(
            @Parameter(description = "재료 등록 요청 데이터", required = true)
            @Valid @RequestBody IngredientDevCreateRequest request
            // @RequestPart(value = "image", required = false) MultipartFile imageFile
    ) {
        IngredientDevResponse response = ingredientDevService.createIngredient(request /*, imageFile */);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "재료 단건 조회")
    @GetMapping("/{ingredientId}")
    public ResponseEntity<IngredientDevResponse> getIngredient(
            @Parameter(description = "재료 ID", required = true) @PathVariable Integer ingredientId
    ) {
        return ResponseEntity.ok(ingredientDevService.getIngredient(ingredientId));
    }

    @Operation(summary = "재료 수정", description = "필요한 필드만 부분 수정 가능합니다.")
    @PutMapping("/{ingredientId}")
    public ResponseEntity<IngredientDevResponse> updateIngredient(
            @Parameter(description = "재료 ID", required = true) @PathVariable Integer ingredientId,
            @Parameter(description = "수정 요청 데이터", required = true)
            @Valid @RequestBody IngredientDevUpdateRequest request
    ) {
        IngredientDevResponse response = ingredientDevService.updateIngredient(ingredientId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "재료 삭제", description = "이미지는 별도 관리 정책에 따라 S3 정리하지 않습니다.")
    @DeleteMapping("/{ingredientId}")
    public ResponseEntity<Void> deleteIngredient(
            @Parameter(description = "재료 ID", required = true) @PathVariable Integer ingredientId
    ) {
        ingredientDevService.deleteIngredient(ingredientId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "재료 이미지 업로드", description = "ingredients-dev 경로로 S3에 업로드합니다.")
    @PostMapping(value = "/{ingredientId}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<IngredientDevResponse> uploadImage(
            @Parameter(description = "재료 ID", required = true) @PathVariable Integer ingredientId,
            @Parameter(description = "업로드할 이미지", required = true) @RequestPart("image") MultipartFile imageFile
    ) {
        IngredientDevResponse response = ingredientDevService.uploadImage(ingredientId, imageFile);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "재료 이미지 수정", description = "기존 이미지를 삭제하고 새 파일을 업로드합니다.")
    @PutMapping(value = "/{ingredientId}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<IngredientDevResponse> updateImage(
            @Parameter(description = "재료 ID", required = true) @PathVariable Integer ingredientId,
            @Parameter(description = "새 이미지", required = true) @RequestPart("image") MultipartFile imageFile
    ) {
        IngredientDevResponse response = ingredientDevService.updateImage(ingredientId, imageFile);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "재료 이미지 조회", description = "S3 URL을 반환합니다. 이미지가 없으면 404를 반환합니다.")
    @GetMapping("/{ingredientId}/image")
    public ResponseEntity<IngredientDevResponse> getImage(
            @Parameter(description = "재료 ID", required = true) @PathVariable Integer ingredientId
    ) {
        IngredientDevResponse response = ingredientDevService.getImage(ingredientId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "재료 이미지 삭제", description = "S3에서 파일을 삭제하고 DB의 이미지 URL을 비웁니다.")
    @DeleteMapping("/{ingredientId}/image")
    public ResponseEntity<Void> deleteImage(
            @Parameter(description = "재료 ID", required = true) @PathVariable Integer ingredientId
    ) {
        ingredientDevService.deleteImage(ingredientId);
        return ResponseEntity.noContent().build();
    }
}
