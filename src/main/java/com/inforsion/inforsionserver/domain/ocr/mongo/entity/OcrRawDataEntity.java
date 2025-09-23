package com.inforsion.inforsionserver.domain.ocr.mongo.entity;

import com.inforsion.inforsionserver.global.enums.DocumentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "ocr_raw_data")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OcrRawDataEntity {

    @Id
    private String id;

    private Integer rawDataId; // MySQL의 raw_data_id와 연동

    private Integer storeId;

    private DocumentType documentType;

    private String rawOcrText;

    private String parsedItem; // JSON 형태

    private String supplierName;

    private LocalDateTime documentDate;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}