package com.app.boilerplate.Config;

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
    @Bean
    public ErrorAttributes errorAttributes() {
        return new DefaultErrorAttributes() {
            @Override
            public Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options) {
                final var errorAttributes = super.getErrorAttributes(webRequest, options);

                errorAttributes.put("title", errorAttributes.remove("error"));
                errorAttributes.put("detail", errorAttributes.remove("message"));
                errorAttributes.put("instance", errorAttributes.remove("path"));
                errorAttributes.put("type", "about:blank");

                return errorAttributes;
            }
        };
    }
}
