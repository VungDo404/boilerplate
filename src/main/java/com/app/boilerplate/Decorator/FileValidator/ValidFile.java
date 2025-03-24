package com.app.boilerplate.Decorator.FileValidator;

import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidFile {
    String message() default "{validation.file.invalid}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    String maxSize() default "8MB";
    String[] allowedContentTypes() default {};
}
