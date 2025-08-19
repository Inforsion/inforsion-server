package com.inforsion.inforsionserver.global.error.exception;

import com.inforsion.inforsionserver.global.error.code.ErrorCode;

/**
 * 유효하지 않은 파일 관련 예외 클래스
 * 
 * 파일 형식, 크기, 내용 등이 유효하지 않을 때 발생하는 예외를 처리합니다.
 */
public class InvalidFileException extends BusinessException {

    public InvalidFileException() {
        super(ErrorCode.INVALID_FILE_FORMAT);
    }

    public InvalidFileException(String message) {
        super(ErrorCode.INVALID_FILE_FORMAT, message);
    }

    public InvalidFileException(String message, Throwable cause) {
        super(ErrorCode.INVALID_FILE_FORMAT, message, cause);
    }
}