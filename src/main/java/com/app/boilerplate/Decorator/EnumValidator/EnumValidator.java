package com.app.boilerplate.Decorator.EnumValidator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;

public class EnumValidator implements ConstraintValidator<ValidEnum, Enum<?>> {
    private Enum<?>[] enumValues;
    @Override
    public void initialize(ValidEnum annotation) {
        enumValues = annotation.enumClass().getEnumConstants();
    }
    @Override
    public boolean isValid(final Enum<?> value, final ConstraintValidatorContext context) {
        if (value == null) return true;
        final var isValid = Arrays.asList(enumValues).contains(value);
        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addConstraintViolation();
        }
        return isValid;
    }
}
