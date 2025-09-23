package com.inforsion.inforsionserver.global.enums;

public enum DocumentType {
    SALES_RECEIPT("Sales_receipt"),
    SUPPLY_INVOICE("Supply_invoice");

    private final String value;

    DocumentType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}