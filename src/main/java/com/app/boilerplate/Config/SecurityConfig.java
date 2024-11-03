package com.app.boilerplate.Config;

import com.app.boilerplate.Security.AuthenticationUserDetailsService;
import com.app.boilerplate.Security.PreAuthenticationChecker;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfig {

	public static final String[] POST_PUBLIC_URL = {
		"/account/register",
		"/auth/authenticate",
		"/auth/refresh-token"
	};
	public static final String[] GET_PUBLIC_URL = {
		"/swagger-ui/**",
		"/v3/api-docs/**",
		"/swagger-resources/**",
		"/webjars" + "/**"
	};

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtDecoder jwtDecoder) throws Exception {
		http
			.authorizeHttpRequests((authorize) -> authorize
				.requestMatchers(HttpMethod.POST, POST_PUBLIC_URL)
				.permitAll()
				.requestMatchers(HttpMethod.GET, GET_PUBLIC_URL)
				.permitAll()
				.anyRequest()
				.authenticated())
			.csrf(AbstractHttpConfigurer::disable)
			.sessionManagement(
				config -> config.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.oauth2ResourceServer((oauth2) -> oauth2.jwt((jwt) -> jwt.decoder(jwtDecoder)))
			.formLogin(AbstractHttpConfigurer::disable)
			.httpBasic(Customizer.withDefaults());

		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider(AuthenticationUserDetailsService userDetailsService,
															PasswordEncoder passwordEncoder,
															PreAuthenticationChecker preAuthenticationChecker) {
		final var authProvider = new DaoAuthenticationProvider();
		authProvider.setPreAuthenticationChecks(preAuthenticationChecker);
		authProvider.setPasswordEncoder(passwordEncoder);
		authProvider.setUserDetailsService(userDetailsService);
		return authProvider;
	}


}
