package com.inforsion.inforsionserver.Sales;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SalesService {
	private final SalesRepository salesRepository;
	
	@Autowired
	public SalesService(SalesRepository salesRepository) {
		this.salesRepository = salesRepository;
	}
	
	public Sales createSales(Sales sales) {
		return salesRepository.save(sales);
	}
}
