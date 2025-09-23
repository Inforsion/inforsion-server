package com.inforsion.inforsionserver.global.enums;

public enum SocialProvider {
    GOOGLE("Google"),
    NAVER("Naver"),
    KAKAO("Kakao");

    private final String value;

    SocialProvider(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}