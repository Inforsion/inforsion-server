package com.inforsion.inforsionserver.management;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Lombok
@NoArgsConstructor // 매개변수가 없는 기본 생성자
@AllArgsConstructor // 모든 필드를 매개변수로 받는 생성자
public class SalesDTO {
	private Long sales;
	private LocalDateTime sale_date;
}
