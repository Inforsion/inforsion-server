package com.inforsion.inforsionserver.domain.store.dto.request;

import com.inforsion.inforsionserver.global.enums.CustomFieldType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CustomFieldCreateRequest {

    @NotBlank(message = "필드명은 필수입니다")
    @Size(max = 100, message = "필드명은 100자 이하여야 합니다")
    private String fieldName;

    @Size(max = 500, message = "설명은 500자 이하여야 합니다")
    private String description;

    @NotNull(message = "필드 타입은 필수입니다")
    private CustomFieldType fieldType;

    private Boolean isRequired = false;

    private Integer displayOrder;

    @Size(max = 1000, message = "기본값은 1000자 이하여야 합니다")
    private String defaultValue;

    private String validationRules;
}