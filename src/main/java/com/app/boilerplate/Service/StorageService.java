package com.app.boilerplate.Service;

import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;

@RequiredArgsConstructor
@Transactional
@Service
public class StorageService {
    private final S3Template s3Template;

    public String uploadFile(MultipartFile file, String bucketName) throws IOException {
        final var key = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        s3Template.upload(bucketName, key, file.getInputStream());
        return key;
    }

    public void delete(String objectKey, String bucketName) {
        s3Template.deleteObject(bucketName, objectKey);
    }

    public URL getPresignedUrl(String objectKey, String bucketName) {
        return s3Template.createSignedGetURL(bucketName, objectKey, Duration.ofMinutes(30));
    }

}