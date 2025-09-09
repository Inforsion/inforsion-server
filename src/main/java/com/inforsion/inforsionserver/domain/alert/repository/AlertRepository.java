package com.inforsion.inforsionserver.domain.alert.repository;

import com.inforsion.inforsionserver.domain.alert.entity.AlertEntity;
import com.inforsion.inforsionserver.global.enums.AlertType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AlertRepository extends JpaRepository<AlertEntity, Integer>, AlertRepositoryCustom {

    List<AlertEntity> findByUserIdOrderByCreatedAtDesc(Integer userId);

    List<AlertEntity> findByUserIdAndIsReadOrderByCreatedAtDesc(Integer userId, Boolean isRead);

    List<AlertEntity> findByStoreIdOrderByCreatedAtDesc(Integer storeId);

    List<AlertEntity> findByStoreIdAndIsReadOrderByCreatedAtDesc(Integer storeId, Boolean isRead);

    List<AlertEntity> findByAlertTypeAndStoreIdOrderByCreatedAtDesc(AlertType alertType, Integer storeId);

    @Query("SELECT COUNT(a) FROM AlertEntity a WHERE a.user.id = :userId AND a.isRead = false")
    Long countUnreadByUserId(@Param("userId") Integer userId);

    @Query("SELECT a FROM AlertEntity a WHERE a.store.id = :storeId AND a.createdAt BETWEEN :startDate AND :endDate ORDER BY a.createdAt DESC")
    List<AlertEntity> findByStoreIdAndCreatedAtBetween(@Param("storeId") Integer storeId, 
                                                      @Param("startDate") LocalDateTime startDate, 
                                                      @Param("endDate") LocalDateTime endDate);
}