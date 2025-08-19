package com.inforsion.inforsionserver.domain.store.controller;

import com.inforsion.inforsionserver.domain.store.dto.StoreDto;
import com.inforsion.inforsionserver.domain.store.service.StoreService;
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

@Tag(name = "Store", description = "가게 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/stores")
public class StoreController {

    private final StoreService storeService;

    @Operation(summary = "가게 생성", description = "특정 사용자에 대한 새로운 가게를 생성합니다.")
    // TODO: userId는 추후 Spring Security의 @AuthenticationPrincipal 등을 통해 직접 받아오도록 수정해야 합니다.
    @PostMapping("/{userId}")
    public ResponseEntity<StoreDto.Response> createStore(
            @PathVariable Integer userId,
            @RequestBody StoreDto.CreateRequest request) {
        StoreDto.Response response = storeService.createStore(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "가게 단건 조회", description = "특정 가게의 상세 정보를 조회합니다.")
    @GetMapping("/{storeId}")
    public ResponseEntity<StoreDto.Response> getStore(@PathVariable Integer storeId) {
        StoreDto.Response response = storeService.getStore(storeId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "가게 정보 수정", description = "특정 가게의 정보를 수정합니다.")
    @PutMapping("/{storeId}")
    public ResponseEntity<StoreDto.Response> updateStore(
            @PathVariable Integer storeId,
            @RequestBody StoreDto.UpdateRequest request) {
        StoreDto.Response response = storeService.updateStore(storeId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "가게 삭제", description = "특정 가게를 삭제합니다.")
    @DeleteMapping("/{storeId}")
    public ResponseEntity<Void> deleteStore(@PathVariable Integer storeId) {
        storeService.deleteStore(storeId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "가게 썸네일 이미지 업로드", 
            description = "특정 가게의 썸네일 이미지를 S3에 업로드합니다. 지원 형식: JPEG, PNG, GIF. 최대 파일 크기: 10MB. 기존 이미지가 있는 경우 새 이미지로 교체됩니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", 
                    description = "썸네일 업로드 성공",
                    content = @Content(schema = @Schema(implementation = StoreDto.Response.class))
            ),
            @ApiResponse(
                    responseCode = "400", 
                    description = "잘못된 파일 형식 또는 크기 초과",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404", 
                    description = "가게를 찾을 수 없음",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500", 
                    description = "S3 업로드 실패",
                    content = @Content
            )
    })
    @PostMapping("/{storeId}/thumbnail")
    public ResponseEntity<StoreDto.Response> uploadStoreThumbnail(
            @Parameter(description = "가게 ID", required = true, example = "1")
            @PathVariable Integer storeId,
            @Parameter(description = "업로드할 썸네일 이미지 파일", required = true)
            @RequestParam("file") MultipartFile file) {
        StoreDto.Response response = storeService.uploadStoreThumbnail(storeId, file);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "가게 썸네일 이미지 삭제", 
            description = "특정 가게의 썸네일 이미지를 S3에서 삭제하고, 데이터베이스에서 이미지 정보를 제거합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204", 
                    description = "썸네일 삭제 성공",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404", 
                    description = "가게를 찾을 수 없음",
                    content = @Content
            )
    })
    @DeleteMapping("/{storeId}/thumbnail")
    public ResponseEntity<Void> deleteStoreThumbnail(
            @Parameter(description = "가게 ID", required = true, example = "1")
            @PathVariable Integer storeId) {
        storeService.deleteStoreThumbnail(storeId);
        return ResponseEntity.noContent().build();
    }
}
