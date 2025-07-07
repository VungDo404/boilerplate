package com.app.boilerplate.Security;

import com.app.boilerplate.Service.Authentication.OAuth2Service;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@RequiredArgsConstructor
@Component
public class OAuth2UserService extends DefaultOAuth2UserService {

    private final OAuth2Service oAuth2Service;
    @Override
    public AuthenticationOAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        final var oAuth2User = super.loadUser(userRequest);
        return process(userRequest, oAuth2User);
    }

    private AuthenticationOAuth2User process(OAuth2UserRequest userRequest, OAuth2User oAuth2User){
        final var provider = userRequest.getClientRegistration().getRegistrationId();
        OAuth2UserInfo oAuth2UserInfo;
        if (provider.equals("google")){
            oAuth2UserInfo = new GoogleOAuth2UserInfo(oAuth2User.getAttributes());
        }else if (provider.equals("github")){
            oAuth2User = processGetGithubEmail(userRequest, oAuth2User);
            oAuth2UserInfo = new GithubOAuth2UserInfo(oAuth2User.getAttributes());

        }else {
            throw new OAuth2AuthenticationException("Login with " + provider + " is not supported yet.");
        }
        return AuthenticationOAuth2User.builder()
            .oauth2User(oAuth2User)
            .oauth2UserInfo(oAuth2UserInfo)
            .build();
    }

    private OAuth2User processGetGithubEmail(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        if (oAuth2User.getAttributes().get("email") == null) {
            final var at = userRequest.getAccessToken().getTokenValue();
            final var emails = oAuth2Service.getEmails(at);

            if (emails != null && !emails.isEmpty()) {
                String email = null;

                for (final var emailData : emails) {
                    final var primary = (Boolean) emailData.get("primary");
                    final var verified = (Boolean) emailData.get("verified");
                    if (primary != null && primary && verified != null && verified) {
                        email = (String) emailData.get("email");
                        break;
                    }
                }

                if (email == null) {
                    for (final var emailData : emails) {
                        final var verified = (Boolean) emailData.get("verified");
                        if (verified != null && verified) {
                            email = (String) emailData.get("email");
                            break;
                        }
                    }
                }

                if (email != null) {
                    final var modifiableAttributes = new HashMap<>(oAuth2User.getAttributes());
                    modifiableAttributes.put("email", email);

                    return new DefaultOAuth2User(
                        oAuth2User.getAuthorities(),
                        modifiableAttributes,
                        "email"
                    );
                }
            }
        }
        return oAuth2User;
    }

}
