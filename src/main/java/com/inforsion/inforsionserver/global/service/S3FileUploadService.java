package com.inforsion.inforsionserver.global.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * S3 파일 업로드 서비스
 * 
 * AWS S3에 파일을 업로드하고 관리하는 서비스입니다.
 * 이미지 파일 업로드, 삭제, URL 생성 등의 기능을 제공합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class S3FileUploadService {

    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Value("${cloud.aws.region.static}")
    private String region;

    // 허용되는 이미지 파일 확장자
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "gif", "webp");
    
    // 최대 파일 크기 (10MB)
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    /**
     * 이미지 파일을 S3에 업로드합니다.
     * 
     * @param file 업로드할 파일
     * @param directory S3 내 디렉토리 (예: "ingredients", "products")
     * @return 업로드된 파일의 S3 URL
     * @throws IllegalArgumentException 파일이 유효하지 않은 경우
     * @throws RuntimeException S3 업로드 실패 시
     */
    public String uploadImageFile(MultipartFile file, String directory) {
        validateFile(file);
        
        String fileName = generateFileName(file.getOriginalFilename());
        String s3Key = directory + "/" + fileName;
        
        try {
            // S3에 파일 업로드
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .contentType(file.getContentType())
                    .contentLength(file.getSize())
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            
            // 업로드된 파일의 URL 생성
            String fileUrl = generateFileUrl(s3Key);
            
            log.info("파일 업로드 성공: {} -> {}", file.getOriginalFilename(), fileUrl);
            return fileUrl;
            
        } catch (IOException e) {
            log.error("파일 업로드 중 IO 오류 발생: {}", file.getOriginalFilename(), e);
            throw new RuntimeException("파일 업로드 중 오류가 발생했습니다.", e);
        } catch (S3Exception e) {
            log.error("S3 업로드 실패: {}", file.getOriginalFilename(), e);
            throw new RuntimeException("S3 업로드에 실패했습니다.", e);
        }
    }

    /**
     * S3에서 파일을 삭제합니다.
     * 
     * @param fileUrl 삭제할 파일의 S3 URL
     * @throws RuntimeException S3 삭제 실패 시
     */
    public void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return;
        }
        
        try {
            String s3Key = extractS3KeyFromUrl(fileUrl);
            
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .build();
                    
            s3Client.deleteObject(deleteObjectRequest);
            
            log.info("파일 삭제 성공: {}", fileUrl);
            
        } catch (S3Exception e) {
            log.error("S3 파일 삭제 실패: {}", fileUrl, e);
            throw new RuntimeException("S3 파일 삭제에 실패했습니다.", e);
        }
    }

    /**
     * 파일 유효성을 검증합니다.
     * 
     * @param file 검증할 파일
     * @throws IllegalArgumentException 파일이 유효하지 않은 경우
     */
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어있습니다.");
        }
        
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("파일 크기가 10MB를 초과할 수 없습니다.");
        }
        
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new IllegalArgumentException("파일명이 없습니다.");
        }
        
        String extension = getFileExtension(originalFilename).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException("지원하지 않는 파일 형식입니다. 허용되는 형식: " + ALLOWED_EXTENSIONS);
        }
    }

    /**
     * 고유한 파일명을 생성합니다.
     * 
     * @param originalFilename 원본 파일명
     * @return 생성된 고유 파일명
     */
    private String generateFileName(String originalFilename) {
        String extension = getFileExtension(originalFilename);
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        
        return timestamp + "_" + uuid + "." + extension;
    }

    /**
     * 파일 확장자를 추출합니다.
     * 
     * @param filename 파일명
     * @return 확장자
     */
    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf(".");
        if (lastDotIndex == -1) {
            throw new IllegalArgumentException("파일 확장자가 없습니다.");
        }
        return filename.substring(lastDotIndex + 1);
    }

    /**
     * S3 파일 URL을 생성합니다.
     * 
     * @param s3Key S3 객체 키
     * @return 생성된 파일 URL
     */
    private String generateFileUrl(String s3Key) {
        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, s3Key);
    }

    /**
     * S3 URL에서 객체 키를 추출합니다.
     * 
     * @param fileUrl S3 파일 URL
     * @return 추출된 S3 객체 키
     */
    private String extractS3KeyFromUrl(String fileUrl) {
        String bucketUrl = String.format("https://%s.s3.%s.amazonaws.com/", bucketName, region);
        if (fileUrl.startsWith(bucketUrl)) {
            return fileUrl.substring(bucketUrl.length());
        }
        throw new IllegalArgumentException("유효하지 않은 S3 URL입니다: " + fileUrl);
    }

    /**
     * S3 URL에서 S3 키를 추출합니다 (public 메서드)
     * 
     * @param fileUrl S3 파일 URL
     * @return S3 객체 키
     */
    public String getS3KeyFromUrl(String fileUrl) {
        return extractS3KeyFromUrl(fileUrl);
    }
}