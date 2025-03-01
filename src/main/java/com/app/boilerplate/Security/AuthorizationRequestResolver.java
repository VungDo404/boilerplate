package com.app.boilerplate.Security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;

public class AuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {
    private final OAuth2AuthorizationRequestResolver authorizationRequestResolver;

    public AuthorizationRequestResolver(OAuth2AuthorizationRequestResolver defaultResolver) {
        this.authorizationRequestResolver = defaultResolver;
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
        OAuth2AuthorizationRequest authorizationRequest = authorizationRequestResolver.resolve(request);
        return modifyAuthorizationRequest(authorizationRequest);
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
        OAuth2AuthorizationRequest authorizationRequest = authorizationRequestResolver.resolve(request, clientRegistrationId);
        return modifyAuthorizationRequest(authorizationRequest);
    }

    private OAuth2AuthorizationRequest modifyAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest) {
        if (authorizationRequest == null) {
            return null;
        }

        final var additionalParams = new HashMap<>(authorizationRequest.getAdditionalParameters());
        final var registrationId = extractRegistrationId(authorizationRequest);

        if ("google".equals(registrationId)) {
            additionalParams.put("prompt", "select_account");
            String customAuthorizationRequestUri = UriComponentsBuilder
                .fromUriString(authorizationRequest.getAuthorizationRequestUri())
                .queryParam("prompt", "select_account")
                .build(true).toUriString();
            return OAuth2AuthorizationRequest.from(authorizationRequest)
                .additionalParameters(additionalParams)
                .authorizationRequestUri(customAuthorizationRequestUri)
                .build();
        }
        return OAuth2AuthorizationRequest.from(authorizationRequest)
            .additionalParameters(additionalParams)
            .build();
    }

    private String extractRegistrationId(OAuth2AuthorizationRequest authorizationRequest) {
        final var uri = authorizationRequest.getAuthorizationRequestUri();
        return uri.substring(uri.lastIndexOf('/') + 1);
    }
}
