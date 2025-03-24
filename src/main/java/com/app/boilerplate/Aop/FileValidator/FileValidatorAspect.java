package com.app.boilerplate.Aop.FileValidator;

import com.app.boilerplate.Decorator.FileValidator.ValidFile;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.tika.Tika;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.UnsupportedMediaTypeStatusException;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Aspect
@RequiredArgsConstructor
@Component
public class FileValidatorAspect {
    private final Tika tika = new Tika();
    private DataSize maxSize;
    private List<MediaType> allowedContentTypes;

    @SneakyThrows
    @Around("execution(* com.app.boilerplate.Controller..*.*(.., @com.app.boilerplate.Decorator.FileValidator" +
        ".ValidFile (*), ..))")
    public Object fileValidator(ProceedingJoinPoint joinPoint) {
        final var signature = (MethodSignature) joinPoint.getSignature();
        final var method = signature.getMethod();
        final var parameters = method.getParameters();
        final var args = joinPoint.getArgs();

        for (int i = 0; i < parameters.length; i++) {
            final var parameter = parameters[i];
            if (parameter.isAnnotationPresent(ValidFile.class)) {
                final var validFileAnnotation = parameter.getAnnotation(ValidFile.class);
                final var arg = args[i];
                maxSize = DataSize.parse(validFileAnnotation.maxSize());
                allowedContentTypes = Arrays.stream(validFileAnnotation.allowedContentTypes())
                    .map(MediaType::parseMediaType)
                    .toList();

                if (arg instanceof MultipartFile file) {
                    processValidFile(file);
                }else if (arg instanceof MultipartFile[] files){
                    Arrays.stream(files).forEach(this::processValidFile);
                }
            }
        }
        return joinPoint.proceed();
    }

    private void processValidFile(MultipartFile multipartFile) {
        try {
            if (multipartFile.getSize() > maxSize.toBytes()) {
                throw new MaxUploadSizeExceededException(maxSize.toBytes());
            }

            final var detectedContentType = tika.detect(multipartFile.getInputStream());
            final var detectedMediaType = MediaType.parseMediaType(detectedContentType);

            if (!allowedContentTypes.isEmpty() && !allowedContentTypes.contains(detectedMediaType)) {
                throw new UnsupportedMediaTypeStatusException(detectedMediaType, allowedContentTypes);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to process file", e);
        }
    }
}
