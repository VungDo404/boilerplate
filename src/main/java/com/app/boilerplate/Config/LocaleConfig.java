package com.app.boilerplate.Config;

import io.micrometer.common.lang.NonNull;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.List;
import java.util.Locale;

@RequiredArgsConstructor
@Configuration
public class LocaleConfig extends AcceptHeaderLocaleResolver implements WebMvcConfigurer {
	private final MessageSource messageSource;
	@NonNull
	@Override
	public Locale resolveLocale(HttpServletRequest request) {
		final var languageHeader = request.getHeader("Accept-Language");
		return StringUtils.hasLength(languageHeader) ? Locale.lookup(Locale.LanguageRange.parse(languageHeader),
			List.of(Locale.ENGLISH, Locale.forLanguageTag("vi"))) :
		Locale.getDefault();
	}
	
	@Bean
	public LocalValidatorFactoryBean getValidator() {
		final var bean = new LocalValidatorFactoryBean();
		bean.setValidationMessageSource(messageSource);
		return bean;
	}
}
