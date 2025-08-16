package com.inforsion.inforsionserver.domain.store.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CustomFieldValueResponse {

    private Integer customFieldId;
    private String fieldName;
    private String fieldType;
    private String value;
    private Boolean isRequired;
}