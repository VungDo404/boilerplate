package com.app.boilerplate.Shared.Authentication.Model;

import com.app.boilerplate.Shared.Authentication.TwoFactorProvider;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SendTwoFactorCodeModel {
    private String secret;
    private TwoFactorProvider provider;
    private String uri;
}
