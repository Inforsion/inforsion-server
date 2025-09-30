package com.inforsion.inforsionserver.global.enums;

public enum OcrJobStatus {
    PENDING("대기 중"),
    PROCESSING("처리 중"),
    COMPLETED("완료"),
    FAILED("실패");
    
    private final String description;
    
    OcrJobStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}