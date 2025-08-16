package com.inforsion.inforsionserver.Sales;

import java.time.LocalDateTime;

import com.inforsion.inforsionserver.Store.Store;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
@Entity
public class Sales {
	
	@Id
	private Long sale;
	
	LocalDateTime sale_date;
	
	@ManyToOne
	@JoinColumn(name = "store_id")
	private Store store;
}
