package com.app.boilerplate.Util;

import org.springframework.context.support.ResourceBundleMessageSource;

import java.util.Locale;

public interface Translator {
	ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
	default String translate(String msgCode, Locale locale, Object... args) {
		return messageSource.getMessage(msgCode, args, msgCode ,locale);
	}
	default String translateEnglish(String msgCode, Object... args) {
		return translate(msgCode, Locale.ENGLISH, args);
	}
}
