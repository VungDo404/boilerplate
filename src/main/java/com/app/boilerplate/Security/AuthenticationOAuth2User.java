package com.app.boilerplate.Security;

import com.app.boilerplate.Domain.User.User;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

@Getter
@Setter
@Builder
public class AuthenticationOAuth2User implements OAuth2User {
	private OAuth2User oauth2User;
	private User user;

	@Override
	public Map<String, Object> getAttributes() {
		return oauth2User.getAttributes();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return oauth2User.getAuthorities();
	}

	@Override
	public String getName() {
		return oauth2User.getName();
	}
}
