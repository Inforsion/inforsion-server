package com.inforsion.inforsionserver.domain.store.controller;

import com.inforsion.inforsionserver.domain.store.dto.StoreDto;
import com.inforsion.inforsionserver.domain.store.service.StoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
