package com.example.java21_test.service;

import com.example.java21_test.dto.responseDto.StatusCodeResponseDto;
import io.awspring.cloud.s3.ObjectMetadata;
import io.awspring.cloud.s3.S3Operations;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j(topic = "S3 service")
@Service
@RequiredArgsConstructor
public class S3Service {
    private final S3Operations s3Operations;
    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    private static final String TEMP_FOLDER = "temp/";
    private static final String PERMANENT_FOLDER = "permanent/";

    @Transactional
    public StatusCodeResponseDto<List<String>> upload(List<MultipartFile> files) throws IOException {
        if (files == null) {
            throw new IllegalArgumentException("사진 없이는 등록이 불가능합니다.");
        }
        List<String> locationList = new ArrayList<>();
        for (MultipartFile file : files) {
            if (!MediaType.IMAGE_PNG.toString().equals(file.getContentType()) &&
                    !MediaType.IMAGE_JPEG.toString().equals(file.getContentType())) {
                throw new IllegalArgumentException("이미지 파일만 가능합니다.");
            }
            String fileName = TEMP_FOLDER + UUID.randomUUID();
            try (InputStream inputStream = file.getInputStream()) {
                s3Operations.upload(bucket, fileName, inputStream,
                        ObjectMetadata.builder().contentType(file.getContentType()).build());
            }

            String url = "https://" + bucket + ".s3.amazonaws.com/" + fileName;
            locationList.add(url);
        }
        return new StatusCodeResponseDto<>(HttpStatus.CREATED.value(), "image upload success", locationList);
    }

    @Transactional
    public String moveTempFilesToPermanent(String content) {
        Document doc = Jsoup.parse(content);
        Elements imgs = doc.select("img");
        for (Element img : imgs) {
            String tempUrl = img.attr("src");
            String fileName = tempUrl.substring(tempUrl.lastIndexOf("/") + 1);
            String tempFileName = TEMP_FOLDER + fileName;
            String permanentFileName = PERMANENT_FOLDER + fileName;

            // S3에서 파일 이동 (복사 후 삭제)
            copyObject(bucket, tempFileName, bucket, permanentFileName);
            s3Operations.deleteObject(bucket, tempFileName);

            // URL 업데이트
            String permanentUrl = "https://" + bucket + ".s3.amazonaws.com/" + permanentFileName;
            img.attr("src", permanentUrl);
        }
        return doc.outerHtml();
    }

    @Transactional
    public void deletePermanentFile(String content) {
        Document doc = Jsoup.parse(content);
        Elements imgs = doc.select("img");
        for (Element img : imgs) {
            String tempUrl = img.attr("src");
            String fileName = tempUrl.substring(tempUrl.lastIndexOf("/") + 1);
            String permanentFileName = PERMANENT_FOLDER + fileName;

            s3Operations.deleteObject(bucket, permanentFileName);
        }
    }

    @Transactional
    public void copyObject(String sourceBucket, String sourceKey, String destinationBucket, String destinationKey) {
        CopyObjectRequest copyObjectRequest = CopyObjectRequest.builder()
                .sourceBucket(sourceBucket)
                .sourceKey(sourceKey)
                .destinationBucket(destinationBucket)
                .destinationKey(destinationKey)
                .build();
        try {
            s3Client.copyObject(copyObjectRequest);
        } catch (Exception e) {
            throw new IllegalArgumentException("image url을 확인해주세요.");
        }
    }

    // 매일 5시에 24시간 지난 temp파일 정리 최대 48시간 가량 남아있을 수 있음
    @Scheduled(cron = "0 0 5 * * ?")
    @Transactional
    public void deleteOldTempFiles() {
        ListObjectsRequest listObjectsRequest = ListObjectsRequest.builder()
                .bucket(bucket)
                .prefix(TEMP_FOLDER)
                .build();
        List<S3Object> listObjectsResponse = s3Client.listObjects(listObjectsRequest).contents();
        for (S3Object s3Object : listObjectsResponse) {
            Instant lastModified = s3Object.lastModified();
            long hours = Duration.between(lastModified, Instant.now()).toHours();
            if (hours > 24) {
                String key = s3Object.key();
                log.info(key);
                s3Operations.deleteObject(bucket, key);
            }
        }
    }
}