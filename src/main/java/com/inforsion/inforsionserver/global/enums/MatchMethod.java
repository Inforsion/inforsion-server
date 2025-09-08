package com.inforsion.inforsionserver.global.enums;

public enum MatchMethod {
    AUTO("Auto"),
    MANUAL("Manual"),
    AI_SUGGESTION("AI_suggestion");

    private final String value;

    MatchMethod(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}