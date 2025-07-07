package com.app.boilerplate.Security;

import com.app.boilerplate.Config.TokenAuthConfig;
import com.app.boilerplate.Service.Authentication.AuthService;
import com.app.boilerplate.Service.User.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final AuthService authService;
    private final UserService userService;
    @Value("${client.oauth2.redirect-url}")
    private String redirectUrl;
    private final TokenAuthConfig tokenAuthConfig;
    @Override
    public void onAuthenticationSuccess(final HttpServletRequest request, final HttpServletResponse response, final Authentication authentication) throws IOException, ServletException {
        final var principal = (AuthenticationOAuth2User) authentication.getPrincipal();
        final var oAuth2UserInfo = principal.getOauth2UserInfo();
        SecurityContextHolder.clearContext();
        SecurityContextHolder.getContext().setAuthentication(
            new AnonymousAuthenticationToken(
                "anonymousKey",
                "anonymousUser",
                AuthorityUtils.createAuthorityList("ROLE_AUTHENTICATION_ANONYMOUS")
            )
                                                            );
        final var user = userService.getOrCreateExternalUserIfNotExists(oAuth2UserInfo, oAuth2UserInfo.getLoginProvider());
        final var result = authService.processLoginResult(user, response, tokenAuthConfig.getRefreshTokenExpirationInSeconds());
        final var url = UriComponentsBuilder.fromUriString(redirectUrl)
            .queryParam("token", result.getAccessToken())
            .queryParam("expired", result.getExpiresInSeconds())
            .build().toUriString();
        response.sendRedirect(url);

    }
}
