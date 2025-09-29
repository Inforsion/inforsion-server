package com.inforsion.inforsionserver.domain.ocr.mysql.repository;

import com.inforsion.inforsionserver.domain.ocr.dto.OcrJobStatus;
import com.inforsion.inforsionserver.domain.ocr.mysql.entity.OcrJobEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OcrJobRepository extends JpaRepository<OcrJobEntity, Integer> {

    Optional<OcrJobEntity> findByJobUuid(String jobUuid);

    List<OcrJobEntity> findByStoreIdOrderByCreatedAtDesc(Integer storeId);

    List<OcrJobEntity> findByStatus(OcrJobStatus status);

    @Query("SELECT o FROM OcrJobEntity o WHERE o.status = :status AND o.createdAt < :before")
    List<OcrJobEntity> findByStatusAndCreatedAtBefore(@Param("status") OcrJobStatus status, @Param("before") LocalDateTime before);

    @Query("SELECT COUNT(o) FROM OcrJobEntity o WHERE o.store.id = :storeId AND o.status = :status")
    Long countByStoreIdAndStatus(@Param("storeId") Integer storeId, @Param("status") OcrJobStatus status);
}