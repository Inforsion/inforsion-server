package com.inforsion.inforsionserver.domain.ocr.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OcrJobResponseDto {

    private String jobId;
    private OcrJobStatus status;
    private Integer progressPercentage;
    private String message;
    private LocalDateTime createdAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private String errorMessage;

    // 완료된 경우에만 포함되는 결과 데이터
    private ProductMatchingResultDto result;

    public static OcrJobResponseDto fromPending(String jobId) {
        return OcrJobResponseDto.builder()
                .jobId(jobId)
                .status(OcrJobStatus.PENDING)
                .progressPercentage(0)
                .message("OCR 작업이 대기열에 추가되었습니다.")
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static OcrJobResponseDto fromProcessing(String jobId, Integer progress) {
        return OcrJobResponseDto.builder()
                .jobId(jobId)
                .status(OcrJobStatus.PROCESSING)
                .progressPercentage(progress)
                .message("OCR 작업을 처리 중입니다.")
                .build();
    }

    public static OcrJobResponseDto fromCompleted(String jobId, ProductMatchingResultDto result) {
        return OcrJobResponseDto.builder()
                .jobId(jobId)
                .status(OcrJobStatus.COMPLETED)
                .progressPercentage(100)
                .message("OCR 작업이 성공적으로 완료되었습니다.")
                .completedAt(LocalDateTime.now())
                .result(result)
                .build();
    }

    public static OcrJobResponseDto fromFailed(String jobId, String errorMessage) {
        return OcrJobResponseDto.builder()
                .jobId(jobId)
                .status(OcrJobStatus.FAILED)
                .progressPercentage(0)
                .message("OCR 작업이 실패했습니다.")
                .errorMessage(errorMessage)
                .completedAt(LocalDateTime.now())
                .build();
    }
}