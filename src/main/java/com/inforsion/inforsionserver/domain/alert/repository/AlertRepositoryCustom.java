package com.inforsion.inforsionserver.domain.alert.repository;

import com.inforsion.inforsionserver.domain.alert.entity.AlertEntity;
import com.inforsion.inforsionserver.global.enums.AlertType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface AlertRepositoryCustom {
    
    Page<AlertEntity> findAlertsByStoreIdWithPaging(Integer storeId, Pageable pageable);
    
    Page<AlertEntity> findUnreadAlertsByUserIdWithPaging(Integer userId, Pageable pageable);
    
    List<AlertEntity> findAlertsByStoreAndTypeAndDateRange(Integer storeId, AlertType alertType, 
                                                          LocalDateTime startDate, LocalDateTime endDate);
    
    void markAllAlertsAsReadByUserId(Integer userId);
    
    Long countUnreadAlertsByStoreId(Integer storeId);
}