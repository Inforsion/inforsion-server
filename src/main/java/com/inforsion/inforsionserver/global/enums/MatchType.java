package com.inforsion.inforsionserver.global.enums;

public enum MatchType {
    MENU("Menu"),
    INVENTORY("Inventory");

    private final String value;

    MatchType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}