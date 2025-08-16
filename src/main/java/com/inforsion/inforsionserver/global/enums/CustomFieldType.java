package com.inforsion.inforsionserver.global.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CustomFieldType {
    TEXT("텍스트"),
    NUMBER("숫자"),
    DATE("날짜"),
    BOOLEAN("참/거짓"),
    TEXTAREA("긴 텍스트");

    private final String description;
}
