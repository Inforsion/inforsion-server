package com.inforsion.inforsionserver.Inventory;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long>{
	List<Inventory> findByStoreId(Long storeId);
}
