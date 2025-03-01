package com.app.boilerplate.Service.Authentication;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Transactional
@Service
public class OAuth2Service {
    private final OAuth2AuthorizedClientService authorizedClientService;

    public List<Map<String, Object>> getEmails(String accessToken) {
        final var userInfoEndpointUri = "https://api.github.com/user/emails";

        final var restTemplate = new RestTemplate();
        final var headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        final var entity = new HttpEntity<>(headers);

        final var response = restTemplate.exchange(
            userInfoEndpointUri,
            HttpMethod.GET,
            entity,
            new ParameterizedTypeReference<List<Map<String, Object>>>() {});

        return response.getBody();
    }
}