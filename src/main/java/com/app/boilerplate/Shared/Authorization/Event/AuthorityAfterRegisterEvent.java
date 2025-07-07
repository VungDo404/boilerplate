package com.app.boilerplate.Shared.Authorization.Event;

import com.app.boilerplate.Domain.User.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthorityAfterRegisterEvent {
    private User user;
}
