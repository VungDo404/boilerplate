package com.app.boilerplate.Decorator.EnumValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EnumValidator.class)
@Documented
public @interface ValidEnum {
    Class<? extends Enum<?>> enumClass();

    String message() default "{validation.enum.invalid}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
