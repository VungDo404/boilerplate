package com.app.boilerplate.Config;

import com.app.boilerplate.Util.TranslatorUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

@RequiredArgsConstructor
@Configuration
public class ErrorConfig {
    private final TranslatorUtil translatorUtil;
    @Bean
    public ErrorAttributes errorAttributes() {
        return new DefaultErrorAttributes() {
            @Override
            public Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options) {

                Map<String, Object> errorAttributes = super.getErrorAttributes(webRequest, options);
                final var message = (String) errorAttributes.remove("message");
                final var msg = translatorUtil.getMessage(message);

                errorAttributes.put("title", errorAttributes.remove("error"));
                errorAttributes.put("detail", msg);
                errorAttributes.put("instance", errorAttributes.remove("path"));
                errorAttributes.put("type", "about:blank");

                return errorAttributes;
            }
        };
    }
}
