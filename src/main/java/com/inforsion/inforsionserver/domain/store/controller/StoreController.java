package com.inforsion.inforsionserver.domain.store.controller;

import com.inforsion.inforsionserver.domain.store.dto.external.StoreAddressSearchDto;
import com.inforsion.inforsionserver.domain.store.dto.request.StoreCreateRequest;
import com.inforsion.inforsionserver.domain.store.dto.response.StoreResponse;
import com.inforsion.inforsionserver.domain.store.dto.request.StoreUpdateRequest;
import com.inforsion.inforsionserver.domain.store.service.StoreService;
import com.inforsion.inforsionserver.global.auth.AuthenticatedUserProvider;
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

import java.util.List;

@Tag(name = "Store", description = "가게 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/stores")
public class StoreController {

    private final StoreService storeService;
    private final AuthenticatedUserProvider authenticatedUserProvider;

    @Operation(summary = "가게 생성", description = "현재 로그인한 사용자의 새로운 가게를 생성합니다.")
    @PostMapping
    public ResponseEntity<StoreResponse> createStore(
            @RequestHeader("Authorization") String authorizationHeader,
            @Valid @RequestBody StoreCreateRequest request) {

        Integer userId = authenticatedUserProvider.getUserId(authorizationHeader);
        StoreResponse response = storeService.createStore(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @Operation(summary = "카카오 주소 검색", description = "카카오맵 주소 검색 API를 사용해 주소를 조회합니다.")
    @GetMapping("/address-search")
    public ResponseEntity<StoreAddressSearchDto.Response> searchAddresses(
            @RequestHeader("Authorization") String authorizationHeader,
            @Parameter(description = "검색어", required = true, example = "판교역로 242")
            @RequestParam String query,
            @Parameter(description = "페이지 번호 (1부터 시작)", example = "1")
            @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "페이지당 결과 수", example = "10")
            @RequestParam(required = false) Integer size
    ) {
        authenticatedUserProvider.getUserId(authorizationHeader);
        StoreAddressSearchDto.Response response = storeService.searchAddresses(query, page, size);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "내 가게 목록 조회", 
            description = "현재 로그인한 사용자가 소유한 모든 가게 목록을 조회합니다. isActive 파라미터로 활성 상태를 필터링할 수 있습니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", 
                    description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = StoreResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404", 
                    description = "사용자를 찾을 수 없음",
                    content = @Content
            )
        })
    @GetMapping("/my")
    public ResponseEntity<List<StoreResponse>> getMyStores(
            @RequestHeader("Authorization") String authorizationHeader,
            @Parameter(description = "활성 상태 필터 (true: 활성, false: 비활성, null: 전체)", example = "true")
            @RequestParam(required = false) Boolean isActive) {

        Integer userId = authenticatedUserProvider.getUserId(authorizationHeader);
        List<StoreResponse> responses;
        if (isActive != null) {
            responses = storeService.getStoresByUserIdAndStatus(userId, isActive);
        } else {
            responses = storeService.getStoresByUserId(userId);
        }

        return ResponseEntity.ok(responses);
    }
    @Operation(summary = "가게 단건 조회", description = "특정 가게의 상세 정보를 조회합니다.")
    @GetMapping("/{storeId}")
    public ResponseEntity<StoreResponse> getStore(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Integer storeId) {
        Integer userId = authenticatedUserProvider.getUserId(authorizationHeader);
        StoreResponse response = storeService.getStore(storeId, userId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "가게 정보 수정", description = "특정 가게의 정보를 수정합니다.")
    @PutMapping("/{storeId}")
    public ResponseEntity<StoreResponse> updateStore(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Integer storeId,
            @Valid @RequestBody StoreUpdateRequest request) {
        Integer userId = authenticatedUserProvider.getUserId(authorizationHeader);
        StoreResponse response = storeService.updateStore(storeId, userId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "가게 삭제", description = "특정 가게를 삭제합니다.")
    @DeleteMapping("/{storeId}")
    public ResponseEntity<Void> deleteStore(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Integer storeId) {
        Integer userId = authenticatedUserProvider.getUserId(authorizationHeader);
        storeService.deleteStore(storeId, userId);
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
                    content = @Content(schema = @Schema(implementation = StoreResponse.class))
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
    @PostMapping(value = "/{storeId}/thumbnail", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<StoreResponse> uploadStoreThumbnail(
            @RequestHeader("Authorization") String authorizationHeader,
            @Parameter(description = "가게 ID", required = true, example = "1")
            @PathVariable Integer storeId,
            @Parameter(description = "업로드할 썸네일 이미지 파일", required = true)
            @RequestParam("file") MultipartFile file) {
        Integer userId = authenticatedUserProvider.getUserId(authorizationHeader);
        StoreResponse response = storeService.uploadStoreThumbnail(storeId, userId, file);
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
            @RequestHeader("Authorization") String authorizationHeader,
            @Parameter(description = "가게 ID", required = true, example = "1")
            @PathVariable Integer storeId) {
        Integer userId = authenticatedUserProvider.getUserId(authorizationHeader);
        storeService.deleteStoreThumbnail(storeId, userId);
        return ResponseEntity.noContent().build();
    }
}
