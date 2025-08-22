package com.inforsion.inforsionserver.domain.ocr.tesseract.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Schema(description = "OCR 처리 결과 응답")
public class OcrResponse {

    @Schema(description = "원본 파일명", example = "receipt_20241221.jpg")
    private String originalFileName;

    @Schema(description = "OCR로 추출된 전체 텍스트")
    private String extractedText;

    @Schema(description = "추출된 텍스트를 줄별로 분리한 리스트")
    private List<String> extractedLines;

    @Schema(description = "OCR 처리 소요 시간 (밀리초)", example = "1234")
    private long processingTimeMs;

    @Schema(description = "파일 크기 (바이트)", example = "2048576")
    private long fileSizeBytes;

    @Schema(description = "이미지 너비 (픽셀)", example = "1920")
    private Integer imageWidth;

    @Schema(description = "이미지 높이 (픽셀)", example = "1080")
    private Integer imageHeight;
}