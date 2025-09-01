package com.inforsion.inforsionserver.global.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CostCategory {
    MATERIAL("재료비"),
    FIXED("고정비"),
    UTILITY("공과금"),
    LABOR("인건비");

    private final String label;
}