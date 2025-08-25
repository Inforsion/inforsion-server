package com.inforsion.inforsionserver.domain.ocr.service;

import com.inforsion.inforsionserver.domain.ocr.dto.OcrEditRequest;
import com.inforsion.inforsionserver.domain.ocr.dto.OcrEditResponse;
import com.inforsion.inforsionserver.domain.ocr.mysql.entity.OcrEditedResultEntity;
import com.inforsion.inforsionserver.domain.ocr.mongo.entity.OcrResultEntity;
import com.inforsion.inforsionserver.domain.ocr.mongo.repository.OcrResultRepository;
import com.inforsion.inforsionserver.domain.ocr.mysql.repository.OcrEditedResultRepository;
import com.inforsion.inforsionserver.domain.user.entity.UserEntity;
import com.inforsion.inforsionserver.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OcrEditService {

    private final OcrResultRepository ocrResultRepository;
    private final OcrEditedResultRepository ocrEditedResultRepository;
    private final UserRepository userRepository;

    public OcrEditResponse getOcrDataForEdit(String ocrResultId) {
        OcrResultEntity ocrResult = ocrResultRepository.findById(ocrResultId)
                .orElseThrow(() -> new RuntimeException("OCR 결과를 찾을 수 없습니다: " + ocrResultId));

        Optional<OcrEditedResultEntity> existingEdit = ocrEditedResultRepository.findByMongoOcrResultId(ocrResultId);

        return OcrEditResponse.builder()
                .mongoOcrResultId(ocrResult.getId())
                .originalFileName(ocrResult.getOriginalFileName())
                .originalText(ocrResult.getExtractedText())
                .originalLines(ocrResult.getExtractedLines())
                .editedText(existingEdit.map(OcrEditedResultEntity::getEditedText).orElse(ocrResult.getExtractedText()))
                .editedLines(existingEdit.map(OcrEditedResultEntity::getEditedLines).orElse(ocrResult.getExtractedLines()))
                .notes(existingEdit.map(OcrEditedResultEntity::getNotes).orElse(""))
                .receiptItems(existingEdit.map(OcrEditedResultEntity::getReceiptItems).orElse(ocrResult.getReceiptItems()))
                .ocrEngine(ocrResult.getOcrEngine())
                .confidence(ocrResult.getConfidence())
                .processingTimeMs(ocrResult.getProcessingTimeMs())
                .fileSizeBytes(ocrResult.getFileSizeBytes())
                .userId(existingEdit.map(edit -> edit.getUser().getId()).orElse(null))
                .userName(existingEdit.map(edit -> edit.getUser().getUsername()).orElse(null))
                .createdAt(ocrResult.getCreatedAt())
                .editedAt(existingEdit.map(OcrEditedResultEntity::getUpdatedAt).orElse(null))
                .build();
    }

    public OcrEditResponse saveEditedOcrResult(OcrEditRequest request) {
        // MongoDB에서 원본 OCR 결과 조회
        OcrResultEntity ocrResult = ocrResultRepository.findById(request.getOcrResultId())
                .orElseThrow(() -> new RuntimeException("OCR 결과를 찾을 수 없습니다: " + request.getOcrResultId()));

        // 사용자 조회
        UserEntity user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + request.getUserId()));

        // 기존 편집 결과가 있는지 확인
        Optional<OcrEditedResultEntity> existingEdit = ocrEditedResultRepository.findByMongoOcrResultId(request.getOcrResultId());

        OcrEditedResultEntity editedResult;
        
        if (existingEdit.isPresent()) {
            // 기존 결과 업데이트
            editedResult = existingEdit.get();
            editedResult.updateEditedContent(request.getEditedText(), request.getEditedLines(), request.getNotes(), request.getReceiptItems());
            log.info("기존 OCR 편집 결과 업데이트 - ID: {}, MongoDB OCR ID: {}", editedResult.getId(), request.getOcrResultId());
        } else {
            // 새로운 편집 결과 생성
            editedResult = OcrEditedResultEntity.builder()
                    .mongoOcrResultId(request.getOcrResultId())
                    .originalFileName(ocrResult.getOriginalFileName())
                    .originalText(ocrResult.getExtractedText())
                    .originalLines(ocrResult.getExtractedLines())
                    .editedText(request.getEditedText())
                    .editedLines(request.getEditedLines())
                    .notes(request.getNotes())
                    .receiptItems(request.getReceiptItems())
                    .ocrEngine(ocrResult.getOcrEngine())
                    .confidence(ocrResult.getConfidence())
                    .processingTimeMs(ocrResult.getProcessingTimeMs())
                    .fileSizeBytes(ocrResult.getFileSizeBytes())
                    .user(user)
                    .build();
            log.info("새로운 OCR 편집 결과 생성 - MongoDB OCR ID: {}, 사용자 ID: {}", request.getOcrResultId(), request.getUserId());
        }

        editedResult = ocrEditedResultRepository.save(editedResult);
        
        log.info("OCR 편집 결과 MySQL 저장 완료 - ID: {}", editedResult.getId());

        return OcrEditResponse.builder()
                .mongoOcrResultId(editedResult.getMongoOcrResultId())
                .originalFileName(editedResult.getOriginalFileName())
                .originalText(editedResult.getOriginalText())
                .originalLines(editedResult.getOriginalLines())
                .editedText(editedResult.getEditedText())
                .editedLines(editedResult.getEditedLines())
                .notes(editedResult.getNotes())
                .receiptItems(editedResult.getReceiptItems())
                .ocrEngine(editedResult.getOcrEngine())
                .confidence(editedResult.getConfidence())
                .processingTimeMs(editedResult.getProcessingTimeMs())
                .fileSizeBytes(editedResult.getFileSizeBytes())
                .userId(editedResult.getUser().getId())
                .userName(editedResult.getUser().getUsername())
                .createdAt(ocrResult.getCreatedAt())
                .editedAt(editedResult.getUpdatedAt())
                .build();
    }

    @Transactional(readOnly = true)
    public Page<OcrEditedResultEntity> getAllEditedResults(Pageable pageable) {
        return ocrEditedResultRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    @Transactional(readOnly = true)
    public Page<OcrEditedResultEntity> getUserEditedResults(Integer userId, Pageable pageable) {
        return ocrEditedResultRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    @Transactional(readOnly = true)
    public Optional<OcrEditedResultEntity> getEditedResultById(Long id) {
        return ocrEditedResultRepository.findById(id);
    }

    public void deleteEditedResult(Long id) {
        if (!ocrEditedResultRepository.existsById(id)) {
            throw new RuntimeException("편집된 OCR 결과를 찾을 수 없습니다: " + id);
        }
        ocrEditedResultRepository.deleteById(id);
        log.info("OCR 편집 결과 삭제 완료 - ID: {}", id);
    }
}