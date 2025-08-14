package com.inforsion.inforsionserver.management;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import lombok.Data;

@Data
@Entity
public class Sales {
	Long sale;
	LocalDateTime sale_date;
}
