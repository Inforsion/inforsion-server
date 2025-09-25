package com.inforsion.inforsionserver.domain.inventory.repository;

import com.inforsion.inforsionserver.domain.inventory.entity.InventoryLogEntity;
import com.inforsion.inforsionserver.global.enums.InventoryLogType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InventoryLogRepository extends JpaRepository<InventoryLogEntity, Integer>, InventoryLogRepositoryCustom {

    List<InventoryLogEntity> findByInventoryIdOrderByCreatedAtDesc(Integer inventoryId);

    List<InventoryLogEntity> findByLogTypeOrderByCreatedAtDesc(InventoryLogType logType);

    List<InventoryLogEntity> findByInventoryIdAndLogTypeOrderByCreatedAtDesc(Integer inventoryId, InventoryLogType logType);

    @Query("SELECT il FROM InventoryLogEntity il WHERE il.inventory.id = :inventoryId AND il.createdAt BETWEEN :startDate AND :endDate ORDER BY il.createdAt DESC")
    List<InventoryLogEntity> findByInventoryIdAndCreatedAtBetween(@Param("inventoryId") Integer inventoryId,
                                                                 @Param("startDate") LocalDateTime startDate,
                                                                 @Param("endDate") LocalDateTime endDate);

    @Query("SELECT il FROM InventoryLogEntity il WHERE il.inventory.store.id = :storeId ORDER BY il.createdAt DESC")
    List<InventoryLogEntity> findByStoreIdOrderByCreatedAtDesc(@Param("storeId") Integer storeId);

    @Query("SELECT il FROM InventoryLogEntity il WHERE il.inventory.store.id = :storeId AND il.createdAt BETWEEN :startDate AND :endDate ORDER BY il.createdAt DESC")
    List<InventoryLogEntity> findByStoreIdAndCreatedAtBetween(@Param("storeId") Integer storeId,
                                                             @Param("startDate") LocalDateTime startDate,
                                                             @Param("endDate") LocalDateTime endDate);
}