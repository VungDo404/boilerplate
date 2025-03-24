package com.app.boilerplate.Controller;

import com.app.boilerplate.Decorator.FileValidator.ValidFile;
import com.app.boilerplate.Service.StorageService;
import com.app.boilerplate.Util.PermissionUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@Tag(name = "Storage")
@RequiredArgsConstructor
@RequestMapping("/file")
@RestController
public class StorageController {
    private final StorageService storageService;

    @PreAuthorize("hasPermission(" + PermissionUtil.ROOT + ", '" + PermissionUtil.FILE + "', '" + PermissionUtil.CREATE +
        "')")
    @PostMapping(value = "/upload/image", consumes = MULTIPART_FORM_DATA_VALUE)
    public String upload(
        @ValidFile(maxSize = "9MB", allowedContentTypes = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE})
        @RequestPart(value = "file")
        MultipartFile file,
        @RequestHeader("X-Bucket-Name") String bucketName) throws IOException {
        return storageService.uploadFile(file, bucketName);
    }

    @PreAuthorize("hasPermission(" + PermissionUtil.ROOT + ", '" + PermissionUtil.FILE + "', '" + PermissionUtil.DELETE +
        "')")
    @DeleteMapping("/{fileName}")
    public void delete(@RequestHeader("X-Bucket-Name") String bucketName, @PathVariable String fileName) {
        storageService.delete(fileName, bucketName);
    }

}