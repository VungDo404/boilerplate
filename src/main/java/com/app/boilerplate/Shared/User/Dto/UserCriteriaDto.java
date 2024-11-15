package com.app.boilerplate.Shared.User.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class UserCriteriaDto {
    private String  username;
    private String email;
    private LocalDateTime emailSpecifyBefore;
    private LocalDateTime emailSpecifyAfter;
}
