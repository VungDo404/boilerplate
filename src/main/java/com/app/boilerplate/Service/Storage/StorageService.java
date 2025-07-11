package com.app.boilerplate.Service.Storage;

import com.app.boilerplate.Util.StorageConsts;
import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;
import org.apache.tika.Tika;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

@RequiredArgsConstructor
@Transactional
@Service
public class StorageService {
    private final S3Template s3Template;

    @Value("${spring.cloud.aws.s3.endpoint}")
    private String endpoint;

    public String uploadFile(MultipartFile file, String bucketName) throws IOException, MimeTypeException {
        final var key = System.currentTimeMillis() + getExtension(file);
        s3Template.upload(bucketName, key, file.getInputStream());
        return key;
    }

    public String uploadFile(MultipartFile file, String bucketName, String dir) throws IOException, MimeTypeException {
        final var key = dir + System.currentTimeMillis() + getExtension(file);
        s3Template.upload(bucketName, key, file.getInputStream());
        return key;
    }

    @Async
    public void delete(String objectKey, String bucketName) {
        s3Template.deleteObject(bucketName, objectKey);
    }

    public URL getPresignedUrl(String objectKey, String bucketName) {
        return s3Template.createSignedGetURL(bucketName, objectKey, Duration.ofMinutes(30));
    }

    public URL getPublicUrl(String objectKey) throws MalformedURLException {
        return new URL(endpoint + "/" + StorageConsts.PUBLIC + "/" + objectKey);
    }

    public boolean isS3Url(String imageUrl) {
        return imageUrl.startsWith(endpoint + "/" + StorageConsts.PUBLIC + "/");
    }

    public String extractObjectKey(String fileUrl, String bucketName) {
        return fileUrl.replace(endpoint + "/" + bucketName + "/", "");
    }

    private String getExtension(MultipartFile file) throws IOException, MimeTypeException {
        Tika tika = new Tika();
        String mimeType = tika.detect(file.getInputStream());
        MimeTypes allTypes = MimeTypes.getDefaultMimeTypes();
        MimeType mime = allTypes.forName(mimeType);
        return mime.getExtension();
    }

}