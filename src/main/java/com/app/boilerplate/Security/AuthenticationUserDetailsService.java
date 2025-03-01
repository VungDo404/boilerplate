package com.app.boilerplate.Security;

import com.app.boilerplate.Service.User.UserService;
import com.app.boilerplate.Shared.Authentication.LoginProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component("userDetailsService")
public class AuthenticationUserDetailsService implements UserDetailsService {
	private final UserService userService;

	@Override
	public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException, DisabledException,
		LockedException, AccountExpiredException, CredentialsExpiredException, BadCredentialsException {
		return userService.getUserByUsernameAndProvider(username, LoginProvider.LOCAL);
	}
}
