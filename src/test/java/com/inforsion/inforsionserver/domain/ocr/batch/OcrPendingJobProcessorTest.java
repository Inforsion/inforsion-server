package com.inforsion.inforsionserver.domain.ocr.batch;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inforsion.inforsionserver.domain.ocr.dto.OcrProcessingRequestDto;
import com.inforsion.inforsionserver.domain.ocr.dto.ProductMatchingResultDto;
import com.inforsion.inforsionserver.domain.ocr.mysql.entity.OcrJobEntity;
import com.inforsion.inforsionserver.domain.ocr.mysql.repository.OcrJobRepository;
import com.inforsion.inforsionserver.domain.ocr.service.OcrProcessingService;
import com.inforsion.inforsionserver.global.enums.DocumentType;
import com.inforsion.inforsionserver.global.enums.OcrJobStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OcrPendingJobProcessorTest {

    @Mock
    private OcrJobRepository ocrJobRepository;

    @Mock
    private OcrProcessingService ocrProcessingService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private OcrPendingJobProcessor processor;

    @BeforeEach
    void setUp() {
        processor = new OcrPendingJobProcessor(ocrJobRepository, ocrProcessingService, objectMapper);
    }

    @Test
    void processPendingJobs_returnsZero_whenNoPendingJobs() {
        when(ocrJobRepository.findByStatus(OcrJobStatus.PENDING)).thenReturn(Collections.emptyList());

        int processedCount = processor.processPendingJobs();

        assertThat(processedCount).isZero();
        verifyNoInteractions(ocrProcessingService);
    }

    @Test
    void processSingleJob_completesJob_whenValidPayload() throws Exception {
        OcrProcessingRequestDto requestDto = OcrProcessingRequestDto.builder()
                .storeId(99)
                .documentType(DocumentType.SALES_RECEIPT)
                .rawOcrText("americano 4500")
                .parsedItems(List.of())
                .build();

        String jobUuid = "test-job-uuid";
        OcrJobEntity jobEntity = OcrJobEntity.builder()
                .jobUuid(jobUuid)
                .status(OcrJobStatus.PENDING)
                .requestPayloadJson(objectMapper.writeValueAsString(requestDto))
                .build();

        when(ocrJobRepository.findByJobUuid(jobUuid)).thenReturn(Optional.of(jobEntity));
        when(ocrProcessingService.processOcrData(any(OcrProcessingRequestDto.class)))
                .thenReturn(ProductMatchingResultDto.builder()
                        .rawDataId(12345)
                        .matchedItems(List.of())
                        .build());

        boolean result = processor.processSingleJob(jobUuid);

        assertThat(result).isTrue();
        assertThat(jobEntity.getStatus()).isEqualTo(OcrJobStatus.COMPLETED);
        assertThat(jobEntity.getRawDataId()).isEqualTo(12345);
        assertThat(jobEntity.getMatchingResultJson()).isNotBlank();

        ArgumentCaptor<OcrProcessingRequestDto> requestCaptor = ArgumentCaptor.forClass(OcrProcessingRequestDto.class);
        verify(ocrProcessingService).processOcrData(requestCaptor.capture());
        assertThat(requestCaptor.getValue().getStoreId()).isEqualTo(99);
    }
}
