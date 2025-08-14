package com.inforsion.inforsionserver.domain.store.repository;

import com.inforsion.inforsionserver.domain.store.entity.StoreEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface StoreRepositoryCustom {
    
    Page<StoreEntity> findActiveStoresByLocation(String location, Pageable pageable);
    
    List<StoreEntity> findStoresWithProducts();
    
    Long countActiveStoresByUserId(Integer userId);
    
    List<StoreEntity> searchStoresByKeyword(String keyword);
}