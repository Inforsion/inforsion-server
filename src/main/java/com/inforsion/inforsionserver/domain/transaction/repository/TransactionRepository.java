package com.inforsion.inforsionserver.domain.transaction.repository;

import com.inforsion.inforsionserver.domain.transaction.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, Integer>,
        TransactionRepositoryCustom {
}
