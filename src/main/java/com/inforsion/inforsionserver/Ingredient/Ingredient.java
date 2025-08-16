package com.inforsion.inforsionserver.Ingredient;

import com.inforsion.inforsionserver.Store.Store;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "ingredients",
	   indexes = {@Index(columnList = "store_id, name")})
public class Ingredient {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private Store store;
	
	private String name;
	private String unit;
	private Integer warningThreshold;
	private Long costPrice;
	
}
