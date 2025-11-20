package com.inforsion.inforsionserver.domain.ocr.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class OcrBatchJobScheduler {

    private final JobLauncher jobLauncher;
    @Qualifier("ocrPendingJob")
    private final Job ocrPendingJob;

    @Scheduled(fixedDelayString = "${ocr.batch.interval-ms:60000}")
    public void runOcrPendingJob() {
        try {
            JobParameters parameters = new JobParametersBuilder()
                    .addString("runId", UUID.randomUUID().toString())
                    .addLong("timestamp", System.currentTimeMillis())
                    .toJobParameters();
            jobLauncher.run(ocrPendingJob, parameters);
        } catch (JobExecutionAlreadyRunningException | JobRestartException |
                 JobInstanceAlreadyCompleteException | JobParametersInvalidException e) {
            log.warn("OCR 배치 작업을 실행할 수 없습니다: {}", e.getMessage());
        } catch (Exception e) {
            log.error("OCR 배치 작업 실행 중 알 수 없는 오류가 발생했습니다.", e);
        }
    }
}
