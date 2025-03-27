package com.app.boilerplate.Util;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class TranslatorUtil {
    private final MessageSource messageSource;

    public String getMessage(String code, Object... args) {
        final var locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(code, args, locale);
    }
}
