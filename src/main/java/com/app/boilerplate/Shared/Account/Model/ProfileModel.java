package com.app.boilerplate.Shared.Account.Model;

import com.app.boilerplate.Shared.Authentication.LoginProvider;
import com.app.boilerplate.Shared.Authorization.Model.AuthorityModel;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.net.URL;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ProfileModel {
    private UUID userId;
    private List<AuthorityModel> authorities;
    private String displayName;
    private String username;
    private URL avatar;
    private LoginProvider provider;
}
