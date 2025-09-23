package com.inforsion.inforsionserver.global.enums;

public enum InventoryLogType {
    RESTOCK("Restock"),
    DEDUCTION("Deduction"),
    MANUAL_ADJUSTMENT("Manual_adjustment"),
    WASTE("Waste");

    private final String value;

    InventoryLogType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}