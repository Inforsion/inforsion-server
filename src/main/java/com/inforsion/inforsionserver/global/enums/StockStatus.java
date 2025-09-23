package com.inforsion.inforsionserver.global.enums;

public enum StockStatus {
    SUFFICIENT("sufficient"),
    LOW("low"),
    OUT_OF_STOCK("out_of_stock");

    private final String value;

    StockStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}