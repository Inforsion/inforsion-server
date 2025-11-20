package com.inforsion.inforsionserver.domain.ocr.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OcrPendingJobTasklet implements Tasklet {

    private final OcrPendingJobProcessor pendingJobProcessor;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        int processedCount = pendingJobProcessor.processPendingJobs();
        log.debug("Spring Batch OCR Tasklet 실행 - 처리 건수: {}", processedCount);
        return RepeatStatus.FINISHED;
    }
}
