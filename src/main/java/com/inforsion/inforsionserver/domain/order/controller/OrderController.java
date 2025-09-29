package com.inforsion.inforsionserver.domain.order.controller;

import com.inforsion.inforsionserver.domain.order.dto.request.OrderRequestDto;
import com.inforsion.inforsionserver.domain.order.dto.response.OrderResponseDto;
import com.inforsion.inforsionserver.domain.order.service.OrderService;
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


@Tag(name = "Order", description = "매장 주문 관리 API")
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Operation(
            summary = "주문 조회",
            description = "주문을 조회하기 위해 사용됩니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "주문 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/{storeId}")
    public ResponseEntity<Page<OrderResponseDto>> findOrders(
            @Parameter(description = "매장 ID", required = true)
            @PathVariable Integer storeId,
            @PageableDefault(size = 10, sort = "name", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        Page<OrderResponseDto> response = orderService.findOrders(storeId, pageable);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "주문 수정",
            description = "주문 정보를 수정합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "재고 수정 성공"),
            @ApiResponse(responseCode = "404", description = "주문을 찾을 수 없음"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PutMapping("/{orderId}")
    public ResponseEntity<OrderResponseDto> updateOrder(
            @Parameter(description = "주문 ID", required = true, example = "1")
            @PathVariable Integer orderId,
            @Parameter(description = "주문 수정 요청 데이터", required = true)
            @Valid @RequestBody OrderRequestDto orderRequestDto
    ) {
        OrderResponseDto updated = orderService.updateOrder(orderId, orderRequestDto);
        return ResponseEntity.ok(updated);
    }

    @Operation(
            summary = "주문 삭제",
            description = "지정된 주문를 삭제합니다. 삭제된 주문은 복구할 수 없습니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "주문 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "주문을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> deleteOrder(
            @Parameter(description = "삭제할 주문 ID", required = true, example = "1")
            @PathVariable("orderId") Integer orderId
    ) {
        orderService.deleteOrder(orderId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "주문 생성",
            description = "주문을 생성합니다. 매장의 주문 관리를 위해 사용됩니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "주문 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })

    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrders(
            @Parameter(description = "주문 생성 요청 데이터", required = true)
            @Valid @RequestBody OrderRequestDto orderRequestDto
    ) {
        OrderResponseDto created = orderService.createOrders(orderRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

}
