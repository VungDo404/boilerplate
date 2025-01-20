package com.app.boilerplate.Security;

import com.app.boilerplate.Shared.Authentication.AccessJwt;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {
	private final JdbcTemplate jdbcTemplate;
	private final String QUERY = """
		    SELECT s.sid
			FROM acl_sid s
			WHERE s.principal = 0
		 			AND EXISTS(
						SELECT 1
						FROM acl_sid u
						WHERE u.sid = ?
							AND u.principal = 1
							AND s.priority <= u.priority
				)
		""";

	@Override
	public AbstractAuthenticationToken convert(Jwt jwt) {
		if (jwt instanceof AccessJwt accessJwt) {
			final var sid = String.format("%s:%s",
				accessJwt.getProvider()
					.name(),
				accessJwt.getUsername());
			Collection<GrantedAuthority> authorities = extractAuthorities(sid);
			return new JwtAuthenticationToken(jwt, authorities);
		}
		return new JwtAuthenticationToken(jwt, Collections.singleton(new SimpleGrantedAuthority("ROLE_ANONYMOUS")));
	}

	private Collection<GrantedAuthority> extractAuthorities(String sid) {
		try {
			return jdbcTemplate.query(
				QUERY,
				(rs, rowNum) -> new SimpleGrantedAuthority(rs.getString("sid")),
				sid
			);
		} catch (DataAccessException e) {
			return Collections.emptyList();
		}
	}
}
