package com.inforsion.inforsionserver.global.enums;

public enum AlertType {
    LOW_STOCK("low_stock"),
    SYSTEM("system");

    private final String value;

    AlertType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}