package com.app.boilerplate.Security;

import com.app.boilerplate.Domain.User.User;
import com.app.boilerplate.Exception.LockedException;
import com.app.boilerplate.Util.DateTimeUtil;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component("userDetailsChecker")
public class PreAuthenticationChecker implements UserDetailsChecker {
	@Override
	public void check(UserDetails userDetails) {
		if (!(userDetails instanceof User user))
			throw new ClassCastException("UserDetails must be an instance of User");
		if(!user.isEnabled())
			throw new DisabledException("error.auth.disabled");
		if(user.getIsLockoutEnabled() && user.getLockoutEndDate() != null && user.getLockoutEndDate().isAfter(LocalDateTime.now()))
			throw new LockedException(DateTimeUtil.formatDateTime(user.getLockoutEndDate()));
		if(!user.getAccountNonExpired())
			throw new AccountExpiredException("error.auth.expired");

	}
}
