package com.inforsion.inforsionserver.domain.store.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CustomFieldUpdateRequest {

    @NotNull(message = "커스텀 필드 ID는 필수입니다")
    private Integer customFieldId;

    private String fieldName;

    private String description;

    private Boolean isRequired;

    private Boolean isActive;

    private Integer displayOrder;

    private String defaultValue;

    private String validationRules;
}