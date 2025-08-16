package com.inforsion.inforsionserver.domain.store.repository;

import com.inforsion.inforsionserver.domain.store.entity.TransactionCustomFieldValueEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionCustomFieldValueRepository extends JpaRepository<TransactionCustomFieldValueEntity, Integer> {
    
    List<TransactionCustomFieldValueEntity> findByTransactionId(Integer transactionId);
    
    Optional<TransactionCustomFieldValueEntity> findByTransactionIdAndCustomFieldId(Integer transactionId, Integer customFieldId);
    
    void deleteByTransactionId(Integer transactionId);
    
    void deleteByCustomFieldId(Integer customFieldId);
}