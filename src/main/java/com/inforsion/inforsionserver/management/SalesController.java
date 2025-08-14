package com.inforsion.inforsionserver.management;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class SalesController {
	
	@GetMapping("/sales")
	public String sales() {
		return "sales";
	}
}
