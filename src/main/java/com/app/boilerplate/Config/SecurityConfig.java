package com.app.boilerplate.Config;

import com.app.boilerplate.Security.AuthenticationUserDetailsService;
import com.app.boilerplate.Security.JwtAuthenticationConverter;
import com.app.boilerplate.Security.PreAuthenticationChecker;
import com.app.boilerplate.Shared.Authentication.AccessJwt;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.acls.AclPermissionEvaluator;
import org.springframework.security.acls.domain.*;
import org.springframework.security.acls.jdbc.BasicLookupStrategy;
import org.springframework.security.acls.jdbc.JdbcMutableAclService;
import org.springframework.security.acls.jdbc.LookupStrategy;
import org.springframework.security.acls.model.PermissionGrantingStrategy;
import org.springframework.security.acls.model.Sid;
import org.springframework.security.acls.model.SidRetrievalStrategy;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
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
		"/webjars" + "/**",
		"/swagger-ui.html"

	};

	@Bean
	@Order(1)
	public SecurityFilterChain swaggerSecurityFilterChain(HttpSecurity http,
														  AuthenticationSuccessHandler successHandler) throws Exception {
		http
			.securityMatcher("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/swagger/login")
			.authorizeHttpRequests(auth -> auth
				.requestMatchers("/swagger/login")
				.permitAll()
				.anyRequest()
				.authenticated())
			.formLogin(form -> form
				.loginPage("/swagger/login")
				.successHandler(successHandler)
				.failureUrl("/swagger/login?error=true")
				.permitAll())
			.logout(logout -> logout
				.logoutUrl("/swagger/logout")
				.logoutSuccessUrl("/swagger/login?logout=true"))
			.csrf(AbstractHttpConfigurer::disable)
			.sessionManagement(session -> session
				.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED));
		return http.build();
	}

	@Bean
	@Order(2)
	public SecurityFilterChain restApiSecurityFilterChain(HttpSecurity http, JwtDecoder jwtDecoder,
														  JwtAuthenticationConverter authenticationConverter) throws Exception {
		http
			.authorizeHttpRequests(auth -> auth
				.requestMatchers(HttpMethod.POST, POST_PUBLIC_URL)
				.permitAll()
				.anyRequest()
				.authenticated())
			.csrf(AbstractHttpConfigurer::disable)
			.sessionManagement(session -> session
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.oauth2ResourceServer(oauth2 -> oauth2
				.jwt(jwt -> jwt
					.decoder(jwtDecoder)
					.jwtAuthenticationConverter(authenticationConverter)
				)
			);
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

	@Bean
	public JdbcMutableAclService aclService(SpringCacheBasedAclCache aclCache, LookupStrategy lookupStrategy,
											DataSource dataSource) {
		final var mutableAclService = new JdbcMutableAclService(dataSource, lookupStrategy, aclCache);
		mutableAclService.setSidIdentityQuery("SELECT LAST_INSERT_ID()");
		mutableAclService.setClassIdentityQuery("SELECT LAST_INSERT_ID()");
		mutableAclService.setAclClassIdSupported(true);

		return mutableAclService;
	}


	@Bean
	public AclAuthorizationStrategy aclAuthorizationStrategy() {
		return new AclAuthorizationStrategyImpl(new SimpleGrantedAuthority("ROLE_APPLICATION_ADMINISTRATOR"));
	}

	@Bean
	public PermissionGrantingStrategy permissionGrantingStrategy() {
		return new DefaultPermissionGrantingStrategy(new ConsoleAuditLogger());
	}

	@Bean
	public SpringCacheBasedAclCache aclCache(CacheManager cacheManager,
											 PermissionGrantingStrategy permissionGrantingStrategy,
											 AclAuthorizationStrategy aclAuthorizationStrategy) {
		Cache redisCache = cacheManager.getCache("acl_cache");
		if (redisCache == null) {
			throw new IllegalStateException("Cache named 'acl_cache' must be configured in RedisCacheManager.");
		}
		return new SpringCacheBasedAclCache(redisCache, permissionGrantingStrategy, aclAuthorizationStrategy);
	}

	@Bean
	public LookupStrategy lookupStrategy(SpringCacheBasedAclCache aclCache, DataSource dataSource) {
		return new BasicLookupStrategy(
			dataSource,
			aclCache,
			aclAuthorizationStrategy(),
			new ConsoleAuditLogger()
		);
	}

	@Bean
	public MethodSecurityExpressionHandler defaultMethodSecurityExpressionHandler(JdbcMutableAclService aclService) {
		DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
		AclPermissionEvaluator permissionEvaluator = new AclPermissionEvaluator(aclService);
		permissionEvaluator.setSidRetrievalStrategy(sidRetrievalStrategy());
		expressionHandler.setPermissionEvaluator(permissionEvaluator);
		return expressionHandler;
	}

	@Bean
	public SidRetrievalStrategy sidRetrievalStrategy() {
		return authentication -> {
			List<Sid> sids = new ArrayList<>();
			if (authentication == null) {
				return sids;
			}

			// Get the principal (your User object)
			Object principal = authentication.getPrincipal();
			if (principal instanceof AccessJwt jwt) {
				// Create SID in format "LOCAL:email"
				String sidValue = String.format("%s:%s",
					jwt.getProvider()
						.name(),
					jwt.getUsername());
				sids.add(new PrincipalSid(sidValue));
			}

			// Add roles if needed
			authentication.getAuthorities()
				.forEach(authority ->
					sids.add(new GrantedAuthoritySid(authority.getAuthority()))
				);

			return sids;
		};
	}


}
