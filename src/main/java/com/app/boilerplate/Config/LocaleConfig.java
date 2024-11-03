package com.app.boilerplate.Config;

import io.micrometer.common.lang.NonNull;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.List;
import java.util.Locale;

@Configuration
public class LocaleConfig extends AcceptHeaderLocaleResolver implements WebMvcConfigurer {
	@NonNull
	@Override
	public Locale resolveLocale(HttpServletRequest request) {
		final var languageHeader = request.getHeader("Accept-Language");
		return StringUtils.hasLength(languageHeader) ? Locale.lookup(Locale.LanguageRange.parse(languageHeader),
			List.of(Locale.ENGLISH, Locale.forLanguageTag("vi"))) :
		Locale.getDefault();
	}
}
