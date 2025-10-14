package com.inforsion.inforsionserver.domain.order.repository;

import com.inforsion.inforsionserver.domain.order.entity.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Integer> {
    Page<OrderEntity> findAllByTransactionStoreId(Integer storeId, Pageable pageable);
}
