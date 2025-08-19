package com.inforsion.inforsionserver.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

/**
 * AWS S3 클라이언트 설정 클래스
 *
 * AWS S3와 연동하기 위한 클라이언트를 Bean으로 등록합니다.
 * 환경변수나 application.yml에서 AWS 인증 정보를 가져와 설정합니다.
 */
@Configuration
public class S3Config {

    @Value("${cloud.aws.credentials.access-key}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secret-key}")
    private String secretKey;

    @Value("${cloud.aws.region.static}")
    private String region;

    /**
     * AWS S3 클라이언트 Bean 생성
     *
     * AWS 인증 정보와 리전을 설정하여 S3 클라이언트를 생성합니다.
     * 이 Bean은 애플리케이션 전체에서 S3 작업에 사용됩니다.
     *
     * @return 설정된 S3Client 인스턴스
     */
    @Bean
    public S3Client s3Client() {
        // AWS 인증 정보 설정
        AwsBasicCredentials awsBasicCredentials = AwsBasicCredentials.create(accessKey, secretKey);

        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(awsBasicCredentials))
                .build();
    }
}
