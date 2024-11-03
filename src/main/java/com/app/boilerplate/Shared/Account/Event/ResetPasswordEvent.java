package com.app.boilerplate.Shared.Account.Event;

import com.app.boilerplate.Domain.User.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Locale;
@Getter
@AllArgsConstructor
public class ResetPasswordEvent {
	private User user;
	private Locale locale;
}
