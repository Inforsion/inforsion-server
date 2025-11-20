package com.inforsion.inforsionserver.domain.ocr.batch;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class OcrBatchJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final OcrPendingJobTasklet ocrPendingJobTasklet;

    @Bean
    public Job ocrPendingJob() {
        return new JobBuilder("ocrPendingJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(ocrPendingStep())
                .build();
    }

    @Bean
    public Step ocrPendingStep() {
        return new StepBuilder("ocrPendingStep", jobRepository)
                .tasklet(ocrPendingJobTasklet, transactionManager)
                .build();
    }
}
