package com.app.boilerplate.Shared.Authorization.Model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class AuthorityModel {
    private int mask;
    private String type;
    private boolean granting;
    private String id;
}
