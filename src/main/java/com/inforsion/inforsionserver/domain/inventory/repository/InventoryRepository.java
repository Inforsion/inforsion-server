package com.inforsion.inforsionserver.domain.inventory.repository;

import com.inforsion.inforsionserver.domain.inventory.entity.InventoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InventoryRepository extends JpaRepository<InventoryEntity, Integer>, QuerydslPredicateExecutor<InventoryEntity>, InventoryRepositoryCustom {

}