package com.inforsion.inforsionserver.domain.order.repository;

import com.inforsion.inforsionserver.domain.order.dto.request.OrderRequestDto;
import com.inforsion.inforsionserver.domain.order.entity.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderRepositoryCustom {
    Page<OrderEntity> findOrders(Integer storeId, Pageable pageable);
    Long updateOrder(Integer orderId, OrderRequestDto orderRequestDto);
    Long deleteOrder(Integer OrderId);
}
