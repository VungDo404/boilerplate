package com.app.boilerplate.Shared.User.Dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UserCriteriaDto {
    private String  username;
    private String email;
    private LocalDateTime emailSpecifyBefore;
    private LocalDateTime emailSpecifyAfter;
}
