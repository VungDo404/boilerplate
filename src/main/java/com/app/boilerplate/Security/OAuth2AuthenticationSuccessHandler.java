package com.app.boilerplate.Security;

import com.app.boilerplate.Service.Authentication.AuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final AuthService authService;
    @Value("${client.oauth2.redirect-url}")
    private String redirectUrl;
    @Override
    public void onAuthenticationSuccess(final HttpServletRequest request, final HttpServletResponse response, final Authentication authentication) throws IOException, ServletException {
        final var principal = (AuthenticationOAuth2User) authentication.getPrincipal();
        final var result = authService.processLoginResult(principal.getUser(), response);
        final var url = UriComponentsBuilder.fromUriString(redirectUrl)
            .queryParam("token", result.getAccessToken())
            .queryParam("expired", result.getExpiresInSeconds())
            .build().toUriString();
        response.sendRedirect(url);

    }
}
