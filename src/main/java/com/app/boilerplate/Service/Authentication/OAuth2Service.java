package com.app.boilerplate.Service.Authentication;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Transactional
@Service
public class OAuth2Service {

    public List<Map<String, Object>> getEmails(String accessToken) {
        final var userInfoEndpointUri = "https://api.github.com/user/emails";

        final var restClient = RestClient.create();

        return restClient.get()
            .uri(userInfoEndpointUri)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
            .retrieve()
            .body(new ParameterizedTypeReference<>() {});
    }
}