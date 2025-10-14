package com.inforsion.inforsionserver.domain.order.service;

import com.inforsion.inforsionserver.domain.order.dto.request.OrderRequestDto;
import com.inforsion.inforsionserver.domain.order.dto.response.OrderResponseDto;
import com.inforsion.inforsionserver.domain.order.entity.OrderEntity;
import com.inforsion.inforsionserver.domain.order.repository.OrderRepository;
import com.inforsion.inforsionserver.domain.store.entity.StoreEntity;
import com.inforsion.inforsionserver.domain.store.repository.StoreRepository;
import com.inforsion.inforsionserver.domain.transaction.dto.request.TransactionConditionDto;
import com.inforsion.inforsionserver.domain.transaction.dto.response.StoreSalesFinancialDto;
import com.inforsion.inforsionserver.domain.transaction.entity.TransactionEntity;
import com.inforsion.inforsionserver.domain.transaction.repository.TransactionRepository;
import com.inforsion.inforsionserver.global.enums.OrderStatus;
import com.inforsion.inforsionserver.global.enums.PeriodType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final TransactionRepository transactionRepository;
    private final StoreRepository storeRepository;
    private static final long SALES_DAYS = 7L;

    /**
     * 주문 생성
     * **/
    @Transactional
    public OrderResponseDto createOrders(@Valid OrderRequestDto orderRequestDto){
        TransactionEntity transaction = transactionRepository.findById(orderRequestDto.getTransactionId())
                .orElseThrow(() -> new IllegalArgumentException("거래를 찾을 수 없습니다."));

        OrderEntity orderEntity = OrderEntity.builder()
                .transaction(transaction)
                .quantity(orderRequestDto.getQuantity())
                .unitPrice(orderRequestDto.getUnitPrice())
                .totalPrice(orderRequestDto.getTotalPrice())
                .orderStatus(orderRequestDto.getOrderStatus())
                .build();

        OrderEntity saved = orderRepository.save(orderEntity);
        return OrderResponseDto.fromEntity(saved);
    }

    /**
     * 주문 조회
     * **/
    @Transactional(readOnly = true)
    public Page<OrderResponseDto> findOrders(Integer storeId, Pageable pageable) {
        return orderRepository.findAllByTransactionStoreId(storeId, pageable)
                .map(OrderResponseDto::fromEntity);
    }

    /**
     * 주문 수정
     * **/
    @Transactional
    public OrderResponseDto updateOrder(Integer orderId, OrderRequestDto orderRequestDto){
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다. orderId = " + orderId));
        order.setQuantity(orderRequestDto.getQuantity());
        order.setUnitPrice(orderRequestDto.getUnitPrice());
        order.setTotalPrice(orderRequestDto.getTotalPrice());
        order.setOrderStatus(orderRequestDto.getOrderStatus());

        OrderEntity updated = orderRepository.save(order);

        return OrderResponseDto.fromEntity(updated);
    }

    @Transactional
    public void deleteOrder(Integer orderId) {
        OrderEntity entity = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다. orderId = " + orderId));

        orderRepository.delete(entity);

    }

    /**
     * 거래가 취소되었을 경우 -> 주문 취소, 매출 삭제 : 재계산 해야하므로
     * **/
    @Transactional
    public List<StoreSalesFinancialDto> cancelOrderAndRecalculate(Integer orderId){
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(()-> new IllegalArgumentException("주문을 찾을 수 없습니다. + orderId"));

        order.setOrderStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        List<TransactionEntity> transactions = transactionRepository.findAllByOrdersId(orderId);
        transactionRepository.deleteAll(transactions);

        TransactionConditionDto condition = new TransactionConditionDto();
        condition.setStoreId(order.getTransaction().getStore().getId());

        condition.setStartDate(LocalDate.now().minusDays(SALES_DAYS));
        condition.setEndDate(LocalDate.now());

        return transactionRepository.getStoreFinancials(condition, PeriodType.DAY);
    }


}
