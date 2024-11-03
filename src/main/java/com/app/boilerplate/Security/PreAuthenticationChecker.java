package com.app.boilerplate.Security;

import com.app.boilerplate.Domain.User.User;
import com.app.boilerplate.Util.Translator;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.stereotype.Component;

@Component("userDetailsChecker")
public class PreAuthenticationChecker implements UserDetailsChecker, Translator {
	@Override
	public void check(UserDetails userDetails) {
		if (!(userDetails instanceof User user))
			throw new ClassCastException("UserDetails must be an instance of User");
		if(user.isEnabled())
			throw new DisabledException("error.auth.disabled");
		if(!user.getAccountNonLocked())
			throw new LockedException("error.auth.locked");
		if(user.getAccountNonExpired())
			throw new AccountExpiredException("error.auth.expired");

	}
}
