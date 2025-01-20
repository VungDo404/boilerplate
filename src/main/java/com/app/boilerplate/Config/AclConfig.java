package com.app.boilerplate.Config;

import com.app.boilerplate.Security.HierarchicalPermission;
import com.app.boilerplate.Security.HierarchicalPermissionGrantingStrategy;
import com.app.boilerplate.Service.Auth.AuthorizeService;
import com.app.boilerplate.Shared.Authentication.AccessJwt;
import com.app.boilerplate.Util.SecurityUtil;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
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
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class AclConfig {
	private final String insertSid = "insert into acl_sid (principal, sid, priority) values (?, ?, ?)";

	@Bean
	public JdbcMutableAclService aclService(SpringCacheBasedAclCache aclCache, LookupStrategy lookupStrategy,
											DataSource dataSource, SecurityUtil securityUtil, JdbcTemplate jdbcTemplate) {
		final var mutableAclService = new AuthorizeService(dataSource, lookupStrategy, aclCache, securityUtil, jdbcTemplate);
		mutableAclService.setSidIdentityQuery("SELECT LAST_INSERT_ID()");
		mutableAclService.setClassIdentityQuery("SELECT LAST_INSERT_ID()");
		mutableAclService.setInsertSidSql(insertSid);
		mutableAclService.setAclClassIdSupported(true);

		return mutableAclService;
	}


	@Bean
	public AclAuthorizationStrategy aclAuthorizationStrategy() {
		return new AclAuthorizationStrategyImpl(new SimpleGrantedAuthority("ROLE_APPLICATION_ADMINISTRATOR"));
	}

	@Bean
	public PermissionGrantingStrategy permissionGrantingStrategy(AuditLogger auditLogger) {
		return new HierarchicalPermissionGrantingStrategy(auditLogger);
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
	public PermissionFactory aclPermissionFactory() {
		return new DefaultPermissionFactory(HierarchicalPermission.class);
	}

	@Bean
	public AuditLogger auditLogger() {
		return new ConsoleAuditLogger();
	}

	@Bean
	public LookupStrategy lookupStrategy(SpringCacheBasedAclCache aclCache, DataSource dataSource,
										 AuditLogger auditLogger, PermissionFactory permissionFactory) {
		var basicLookupStrategy = new BasicLookupStrategy(
			dataSource,
			aclCache,
			aclAuthorizationStrategy(),
			auditLogger
		);
		basicLookupStrategy.setPermissionFactory(permissionFactory);
		return basicLookupStrategy;
	}

	@Bean
	public MethodSecurityExpressionHandler defaultMethodSecurityExpressionHandler(JdbcMutableAclService aclService) {
		DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
		AclPermissionEvaluator permissionEvaluator = new AclPermissionEvaluator(aclService);
		permissionEvaluator.setSidRetrievalStrategy(sidRetrievalStrategy());
		permissionEvaluator.setPermissionFactory(aclPermissionFactory());
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

			Object principal = authentication.getPrincipal();
			if (principal instanceof AccessJwt jwt) {
				String sidValue = String.format("%s:%s",
					jwt.getProvider()
						.name(),
					jwt.getUsername());
				sids.add(new PrincipalSid(sidValue));
			}

			authentication.getAuthorities()
				.forEach(authority ->
					sids.add(new GrantedAuthoritySid(authority.getAuthority()))
				);

			return sids;
		};
	}
}
