package com.inforsion.inforsionserver.domain.store.dto.response;

import com.inforsion.inforsionserver.global.enums.CustomFieldType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CustomFieldResponse {

    private Integer id;
    private String fieldName;
    private String description;
    private CustomFieldType fieldType;
    private Boolean isRequired;
    private Boolean isActive;
    private Integer displayOrder;
    private String defaultValue;
    private String validationRules;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}