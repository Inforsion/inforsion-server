package com.inforsion.inforsionserver.Store;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long>{
	 // 가게 이름 검색
    List<Store> findByNameContainingIgnoreCase(String name);

    // 활성화된 가게만 조회
    List<Store> findByIsActiveTrue();
}
