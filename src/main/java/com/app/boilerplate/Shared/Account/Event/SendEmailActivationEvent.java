package com.app.boilerplate.Shared.Account.Event;

import com.app.boilerplate.Domain.User.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SendEmailActivationEvent {
	private User user;
	private String token;
}
