package com.inforsion.inforsionserver.Inventory;

import java.time.LocalDate;

import com.inforsion.inforsionserver.Store.Store;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "inventory", indexes = {
		@Index(columnList = "store_id, expireDate"),
		@Index(columnList = "store_id, quantity")
})
public class Inventory {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne // Store와 Inventory는 N:1 관계입니다.
	@JoinColumn(name = "store_id", nullable = false) // 외래 키(Foreign Key) 설정
	private Store store;
	
	private int quantity;
	
	private LocalDate expireDate;
	
	
	
}