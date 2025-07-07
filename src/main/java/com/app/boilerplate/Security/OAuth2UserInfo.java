package com.app.boilerplate.Security;

import com.app.boilerplate.Shared.Authentication.LoginProvider;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public abstract class OAuth2UserInfo {
    protected final Map<String, Object> attributes;

    public abstract String getId();
    public abstract String getName();
    public abstract String getEmail();
    public abstract String getImageUrl();
    public abstract LoginProvider getLoginProvider();
}
