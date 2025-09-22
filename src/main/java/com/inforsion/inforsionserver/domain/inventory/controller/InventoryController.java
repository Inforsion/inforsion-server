package com.inforsion.inforsionserver.domain.inventory.controller;

import com.inforsion.inforsionserver.domain.inventory.dto.ExpiringInventoryDto;
import com.inforsion.inforsionserver.domain.inventory.dto.InventoryDto;
import com.inforsion.inforsionserver.domain.inventory.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Tag(name = "Inventory", description = "매장 재고 관리 API - 재고 내역 관리")
@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @Operation(
            summary = "특정 매장 재고 조회",
            description = "특정 매장 재고를 조회하기 위해 사용됩니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "재고 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/{storeId}")
    public ResponseEntity<Page<InventoryDto>> getInventories(
            @Parameter(description = "매장 ID", required = true)
            @PathVariable Integer storeId,
            @PageableDefault(size = 10, sort = "name", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        Page<InventoryDto> response = inventoryService.getInventories(storeId, pageable);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "재고 수정",
            description = "기존 재고 정보를 수정합니다. 재고 금액, 유형 등을 변경할 수 있습니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "재고 수정 성공"),
            @ApiResponse(responseCode = "404", description = "재고를 찾을 수 없음"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PutMapping("/{inventoryId}")
    public ResponseEntity<InventoryDto> updateTransaction(
            @Parameter(description = "재고 ID", required = true, example = "1")
            @PathVariable Integer inventoryId,
            @Parameter(description = "재고 수정 요청 데이터", required = true)
            @Valid @RequestBody InventoryDto inventoryDto
    ) {
        InventoryDto updateInventory = inventoryService.updateInventory(inventoryId, inventoryDto);
        return ResponseEntity.ok(inventoryDto);
    }

    @Operation(
            summary = "재고 삭제",
            description = "지정된 재고를 삭제합니다. 삭제된 재고는 복구할 수 없습니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "재고 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "재고를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @DeleteMapping("/{inventoryId}")
    public ResponseEntity<InventoryDto> deleteInventory(
            @Parameter(description = "삭제할 재고 ID", required = true, example = "1")
            @PathVariable("inventoryId") Integer inventoryId
    ) {
        inventoryService.deleteInventory(inventoryId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "재고 생성",
            description = "재고를 생성합니다. 매장의 재고관리를 위해 사용됩니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "재고 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })

    @PostMapping
    public ResponseEntity<InventoryDto> createInventory(
            @Parameter(description = "재고 생성 요청 데이터", required = true)
            @Valid @RequestBody InventoryDto inventoryDto
    ) {
        InventoryDto created = inventoryService.createInventory(inventoryDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }


    @Operation(
            summary = "유통기한 임박 알림",
            description = "유통기한 임박한 재고를 알려줍니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "유통기한 임박 알림 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/expiring")
    public Map<Integer, List<ExpiringInventoryDto>> getExpiringItems(
            @RequestParam List<Integer>days
    ){
        Map<Integer, List<ExpiringInventoryDto>> result = new HashMap<>();

        for(Integer d: days){
            result.put(d, inventoryService.getExpiringItems(d));
        }
        return result;
    }
}
