package com.inforsion.inforsionserver.domain.store.repository;

import com.inforsion.inforsionserver.domain.store.entity.StoreTransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StoreTransactionRepository extends JpaRepository<StoreTransactionEntity, Integer> {
    
    List<StoreTransactionEntity> findByStoreIdOrderByTransactionDateDesc(Integer storeId);
}