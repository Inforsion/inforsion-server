package com.inforsion.inforsionserver.domain.store.controller;

import com.inforsion.inforsionserver.domain.store.dto.request.CustomFieldCreateRequest;
import com.inforsion.inforsionserver.domain.store.dto.request.CustomFieldUpdateRequest;
import com.inforsion.inforsionserver.domain.store.dto.response.CustomFieldResponse;
import com.inforsion.inforsionserver.domain.store.service.CustomFieldService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stores/{storeId}/custom-fields")
@RequiredArgsConstructor
@Tag(name = "Custom Fields", description = "커스텀 필드 관리 API")
public class CustomFieldController {

    private final CustomFieldService customFieldService;

    @PostMapping
    @Operation(summary = "커스텀 필드 생성", description = "새로운 커스텀 필드를 생성합니다.")
    @ApiResponse(responseCode = "201", description = "커스텀 필드 생성 성공")
    public ResponseEntity<CustomFieldResponse> createCustomField(
            @Parameter(description = "가게 ID") @PathVariable Integer storeId,
            @Valid @RequestBody CustomFieldCreateRequest request) {
        
        CustomFieldResponse response = customFieldService.createCustomField(storeId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "커스텀 필드 목록 조회", description = "가게의 모든 활성화된 커스텀 필드를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "커스텀 필드 목록 조회 성공")
    public ResponseEntity<List<CustomFieldResponse>> getCustomFields(
            @Parameter(description = "가게 ID") @PathVariable Integer storeId) {
        
        List<CustomFieldResponse> response = customFieldService.getCustomFieldsByStore(storeId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{fieldId}")
    @Operation(summary = "커스텀 필드 상세 조회", description = "특정 커스텀 필드의 상세 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "커스텀 필드 조회 성공")
    public ResponseEntity<CustomFieldResponse> getCustomField(
            @Parameter(description = "가게 ID") @PathVariable Integer storeId,
            @Parameter(description = "커스텀 필드 ID") @PathVariable Integer fieldId) {
        
        CustomFieldResponse response = customFieldService.getCustomField(storeId, fieldId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{fieldId}")
    @Operation(summary = "커스텀 필드 수정", description = "기존 커스텀 필드를 수정합니다.")
    @ApiResponse(responseCode = "200", description = "커스텀 필드 수정 성공")
    public ResponseEntity<CustomFieldResponse> updateCustomField(
            @Parameter(description = "가게 ID") @PathVariable Integer storeId,
            @Parameter(description = "커스텀 필드 ID") @PathVariable Integer fieldId,
            @Valid @RequestBody CustomFieldUpdateRequest request) {
        
        CustomFieldResponse response = customFieldService.updateCustomField(storeId, fieldId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{fieldId}")
    @Operation(summary = "커스텀 필드 삭제", description = "기존 커스텀 필드를 삭제합니다.")
    @ApiResponse(responseCode = "204", description = "커스텀 필드 삭제 성공")
    public ResponseEntity<Void> deleteCustomField(
            @Parameter(description = "가게 ID") @PathVariable Integer storeId,
            @Parameter(description = "커스텀 필드 ID") @PathVariable Integer fieldId) {
        
        customFieldService.deleteCustomField(storeId, fieldId);
        return ResponseEntity.noContent().build();
    }
}