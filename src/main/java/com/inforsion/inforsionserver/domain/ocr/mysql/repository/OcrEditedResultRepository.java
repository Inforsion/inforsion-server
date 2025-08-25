package com.inforsion.inforsionserver.domain.ocr.mysql.repository;

import com.inforsion.inforsionserver.domain.ocr.mysql.entity.OcrEditedResultEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OcrEditedResultRepository extends JpaRepository<OcrEditedResultEntity, Long> {

    Optional<OcrEditedResultEntity> findByMongoOcrResultId(String mongoOcrResultId);

    List<OcrEditedResultEntity> findByOriginalFileName(String originalFileName);

    List<OcrEditedResultEntity> findByUserId(Integer userId);

    Page<OcrEditedResultEntity> findByUserIdOrderByCreatedAtDesc(Integer userId, Pageable pageable);

    @Query("SELECT o FROM OcrEditedResultEntity o WHERE o.createdAt BETWEEN :startDate AND :endDate")
    List<OcrEditedResultEntity> findByCreatedAtBetween(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT o FROM OcrEditedResultEntity o WHERE o.user.id = :userId AND o.createdAt BETWEEN :startDate AND :endDate")
    List<OcrEditedResultEntity> findByUserIdAndCreatedAtBetween(
            @Param("userId") Integer userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    Page<OcrEditedResultEntity> findAllByOrderByCreatedAtDesc(Pageable pageable);

    boolean existsByMongoOcrResultId(String mongoOcrResultId);
}