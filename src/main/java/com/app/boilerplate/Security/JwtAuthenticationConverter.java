package com.app.boilerplate.Security;

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
		FROM authority a
		JOIN acl_sid s
		ON a.sid_id = s.id
		WHERE a.user_id = UUID_TO_BIN(?)""";
	@Override
	public AbstractAuthenticationToken convert(Jwt jwt) {
		Collection<GrantedAuthority> authorities = extractAuthorities(jwt.getSubject());
		return new JwtAuthenticationToken(jwt, authorities);
	}

	private Collection<GrantedAuthority> extractAuthorities(String id) {
		try {
			return jdbcTemplate.query(
				QUERY,
				(rs, rowNum) -> new SimpleGrantedAuthority(rs.getString("sid")),
				id
			);
		} catch (DataAccessException e) {
			// Handle the exception appropriately for your use case
			return Collections.emptyList();
		}
	}
}
