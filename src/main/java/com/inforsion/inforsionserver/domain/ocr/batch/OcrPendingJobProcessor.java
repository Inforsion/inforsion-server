package com.inforsion.inforsionserver.domain.ocr.batch;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inforsion.inforsionserver.domain.ocr.dto.OcrProcessingRequestDto;
import com.inforsion.inforsionserver.domain.ocr.dto.ProductMatchingResultDto;
import com.inforsion.inforsionserver.domain.ocr.mysql.entity.OcrJobEntity;
import com.inforsion.inforsionserver.domain.ocr.service.OcrProcessingService;
import com.inforsion.inforsionserver.global.enums.OcrJobStatus;
import com.inforsion.inforsionserver.domain.ocr.mysql.repository.OcrJobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OcrPendingJobProcessor {

    private final OcrJobRepository ocrJobRepository;
    private final OcrProcessingService ocrProcessingService;
    private final ObjectMapper objectMapper;
    private static final int FETCH_BATCH_SIZE = 20;

    /**
     * 대기 중인 OCR 작업을 모두 처리한다.
     *
     * @return 처리에 성공한 작업 수
     */
    public int processPendingJobs() {
        int processedCount = 0;
        boolean jobsFetched = false;

        while (true) {
            List<OcrJobEntity> pendingJobs = ocrJobRepository.findByStatusOrderByCreatedAtAsc(
                    OcrJobStatus.PENDING,
                    PageRequest.of(0, FETCH_BATCH_SIZE)
            );

            if (pendingJobs.isEmpty()) {
                if (!jobsFetched) {
                    log.debug("처리할 OCR 배치 작업이 없습니다.");
                }
                break;
            }

            jobsFetched = true;

            for (OcrJobEntity job : pendingJobs) {
                if (processSingleJob(job.getJobUuid())) {
                    processedCount++;
                }
            }

            if (pendingJobs.size() < FETCH_BATCH_SIZE) {
                break;
            }
        }

        log.info("배치로 {}개의 OCR 작업을 처리했습니다.", processedCount);
        return processedCount;
    }

    /**
     * 단일 OCR Job을 처리한다.
     */
    @Transactional
    public boolean processSingleJob(String jobUuid) {
        Optional<OcrJobEntity> optionalJob = ocrJobRepository.findByJobUuidForUpdate(jobUuid);
        if (optionalJob.isEmpty()) {
            log.warn("배치 실행 중 Job UUID {}를 찾을 수 없습니다.", jobUuid);
            return false;
        }

        OcrJobEntity jobEntity = optionalJob.get();
        if (jobEntity.getStatus() == null || jobEntity.getStatus() != OcrJobStatus.PENDING) {
            log.debug("Job UUID {}는 대기 상태가 아니므로 건너뜁니다. 현재 상태: {}", jobUuid, jobEntity.getStatus());
            return false;
        }

        try {
            jobEntity.updateStatus(OcrJobStatus.PROCESSING);
            jobEntity.updateProgress(10);

            if (jobEntity.getRequestPayloadJson() == null || jobEntity.getRequestPayloadJson().isBlank()) {
                throw new IllegalStateException("요청 페이로드가 존재하지 않습니다.");
            }

            OcrProcessingRequestDto requestDto =
                    objectMapper.readValue(jobEntity.getRequestPayloadJson(), OcrProcessingRequestDto.class);

            jobEntity.updateProgress(40);

            ProductMatchingResultDto result = ocrProcessingService.processOcrData(requestDto);

            jobEntity.setRawDataId(result.getRawDataId());
            jobEntity.setMatchingResultJson(objectMapper.writeValueAsString(result));
            jobEntity.updateProgress(95);
            jobEntity.updateStatus(OcrJobStatus.COMPLETED);

            log.info("배치 OCR 작업 완료: jobUuid={}, rawDataId={}", jobUuid, result.getRawDataId());
            return true;

        } catch (Exception e) {
            log.error("배치 OCR 작업 처리 중 오류 발생: jobUuid={}, 오류={}", jobUuid, e.getMessage(), e);
            jobEntity.setError("배치 처리 오류: " + e.getMessage());
            return false;
        }
    }
}
