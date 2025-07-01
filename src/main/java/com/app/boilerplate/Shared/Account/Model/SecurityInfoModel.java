package com.app.boilerplate.Shared.Account.Model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SecurityInfoModel {
    private LocalDate passwordLastUpdate;
    private boolean twoFactorEnable;
}
