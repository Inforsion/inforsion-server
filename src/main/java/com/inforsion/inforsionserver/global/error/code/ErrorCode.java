package com.inforsion.inforsionserver.global.error.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // Common Errors
    INTERNAL_SERVER_ERROR("C001", "서버 내부 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_INPUT_VALUE("C002", "잘못된 입력값입니다.", HttpStatus.BAD_REQUEST),
    METHOD_NOT_ALLOWED("C003", "지원하지 않는 HTTP 메서드입니다.", HttpStatus.METHOD_NOT_ALLOWED),
    INVALID_TYPE_VALUE("C004", "잘못된 타입 값입니다.", HttpStatus.BAD_REQUEST),
    HANDLE_ACCESS_DENIED("C005", "접근이 거부되었습니다.", HttpStatus.FORBIDDEN),

    // User Errors
    USER_NOT_FOUND("U001", "사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    DUPLICATE_EMAIL("U002", "이미 사용 중인 이메일입니다.", HttpStatus.CONFLICT),
    DUPLICATE_USERNAME("U003", "이미 사용 중인 사용자명입니다.", HttpStatus.CONFLICT),
    INVALID_PASSWORD("U004", "비밀번호가 일치하지 않습니다.", HttpStatus.UNAUTHORIZED),
    USER_ALREADY_EXISTS("U005", "이미 존재하는 사용자입니다.", HttpStatus.CONFLICT),

    // Store Errors
    STORE_NOT_FOUND("S001", "가게를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    STORE_ACCESS_DENIED("S002", "가게에 접근할 권한이 없습니다.", HttpStatus.FORBIDDEN),
    STORE_ALREADY_EXISTS("S003", "이미 존재하는 가게입니다.", HttpStatus.CONFLICT),
    STORE_NOT_ACTIVE("S004", "비활성화된 가게입니다.", HttpStatus.BAD_REQUEST),

    // Product Errors
    PRODUCT_NOT_FOUND("P001", "상품을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    PRODUCT_OUT_OF_STOCK("P002", "상품이 품절되었습니다.", HttpStatus.BAD_REQUEST),
    PRODUCT_ALREADY_EXISTS("P003", "이미 존재하는 상품입니다.", HttpStatus.CONFLICT),
    INVALID_PRICE("P004", "잘못된 가격입니다.", HttpStatus.BAD_REQUEST),

    // Ingredient Errors
    INGREDIENT_NOT_FOUND("I001", "재료를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    INGREDIENT_ALREADY_EXISTS("I002", "이미 존재하는 재료입니다.", HttpStatus.CONFLICT),
    INSUFFICIENT_INGREDIENT("I003", "재료가 부족합니다.", HttpStatus.BAD_REQUEST),

    // Inventory Errors
    INVENTORY_NOT_FOUND("V001", "재고를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    INVENTORY_INSUFFICIENT("V002", "재고가 부족합니다.", HttpStatus.BAD_REQUEST),
    INVENTORY_EXPIRED("V003", "유통기한이 만료된 재고입니다.", HttpStatus.BAD_REQUEST),

    // Authentication Errors
    AUTHENTICATION_FAILED("A001", "인증에 실패했습니다.", HttpStatus.UNAUTHORIZED),
    JWT_TOKEN_EXPIRED("A002", "JWT 토큰이 만료되었습니다.", HttpStatus.UNAUTHORIZED),
    JWT_TOKEN_INVALID("A003", "유효하지 않은 JWT 토큰입니다.", HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_EXPIRED("A004", "리프레시 토큰이 만료되었습니다.", HttpStatus.UNAUTHORIZED),

    // Validation Errors
    VALIDATION_FAILED("V001", "입력값 검증에 실패했습니다.", HttpStatus.BAD_REQUEST),
    MISSING_REQUIRED_FIELD("V002", "필수 입력값이 누락되었습니다.", HttpStatus.BAD_REQUEST),
    INVALID_EMAIL_FORMAT("V003", "이메일 형식이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
    INVALID_PHONE_FORMAT("V004", "전화번호 형식이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),

    // Transaction Errors
    TRANSACTION_NOT_FOUND("T001", "거래를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    INVALID_TRANSACTION_AMOUNT("T002", "잘못된 거래 금액입니다.", HttpStatus.BAD_REQUEST),
    TRANSACTION_ALREADY_PROCESSED("T003", "이미 처리된 거래입니다.", HttpStatus.BAD_REQUEST),
    INVALID_TRANSACTION_CATEGORY("T004", "거래 유형과 카테고리가 일치하지 않습니다.", HttpStatus.BAD_REQUEST),

    // Access Errors
    ACCESS_DENIED("AC001", "접근이 거부되었습니다.", HttpStatus.FORBIDDEN);

    private final String code;
    private final String message;
    private final HttpStatus status;
}