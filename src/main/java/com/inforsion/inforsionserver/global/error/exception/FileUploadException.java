package com.inforsion.inforsionserver.global.error.exception;

import com.inforsion.inforsionserver.global.error.code.ErrorCode;

/**
 * 파일 업로드 관련 예외 클래스
 * 
 * S3 업로드, 파일 검증, 파일 처리 과정에서 발생하는 예외를 처리합니다.
 */
public class FileUploadException extends BusinessException {

    public FileUploadException() {
        super(ErrorCode.FILE_UPLOAD_FAILED);
    }

    public FileUploadException(String message) {
        super(ErrorCode.FILE_UPLOAD_FAILED, message);
    }

    public FileUploadException(String message, Throwable cause) {
        super(ErrorCode.FILE_UPLOAD_FAILED, message, cause);
    }
}