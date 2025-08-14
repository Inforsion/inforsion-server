package com.inforsion.inforsionserver.domain.store.repository;

import com.inforsion.inforsionserver.domain.store.entity.StoreEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoreRepository extends JpaRepository<StoreEntity, Integer>, QuerydslPredicateExecutor<StoreEntity>, StoreRepositoryCustom {
    
    List<StoreEntity> findByUserId(Integer userId);
    
    List<StoreEntity> findByUserIdAndIsActive(Integer userId, Boolean isActive);
    
    Optional<StoreEntity> findByIdAndUserId(Integer id, Integer userId);
    
    boolean existsByNameAndLocation(String name, String location);
}