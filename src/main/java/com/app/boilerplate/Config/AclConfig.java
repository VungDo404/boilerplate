package com.app.boilerplate.Config;

import com.app.boilerplate.Security.HierarchicalPermission;
import com.app.boilerplate.Security.HierarchicalPermissionGrantingStrategy;
import com.app.boilerplate.Service.Authorization.AccessControlListService;
import com.app.boilerplate.Util.SecurityUtil;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.acls.AclPermissionEvaluator;
import org.springframework.security.acls.domain.*;
import org.springframework.security.acls.jdbc.BasicLookupStrategy;
import org.springframework.security.acls.jdbc.JdbcMutableAclService;
import org.springframework.security.acls.model.Sid;
import org.springframework.security.acls.model.SidRetrievalStrategy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class AclConfig {

    @Bean
    public JdbcMutableAclService aclService(
        SpringCacheBasedAclCache aclCache,
        BasicLookupStrategy lookupStrategy,
        DataSource dataSource) {
        final var mutableAclService = new AccessControlListService(
            dataSource,
            lookupStrategy,
            aclCache
        );
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
    public HierarchicalPermissionGrantingStrategy permissionGrantingStrategy(AuditLogger auditLogger) {
        return new HierarchicalPermissionGrantingStrategy(auditLogger);
    }

    @Bean
    public SpringCacheBasedAclCache aclCache(
        CacheManager cacheManager,
        HierarchicalPermissionGrantingStrategy permissionGrantingStrategy,
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
    public BasicLookupStrategy lookupStrategy(
        SpringCacheBasedAclCache aclCache, DataSource dataSource,
        AuditLogger auditLogger, PermissionFactory permissionFactory) {
        var basicLookupStrategy = new BasicLookupStrategy(
            dataSource,
            aclCache,
            aclAuthorizationStrategy(),
            auditLogger
        );
        basicLookupStrategy.setPermissionFactory(permissionFactory);
        basicLookupStrategy.setAclClassIdSupported(true);
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
            if (authentication == null) return sids;
            if (!SecurityUtil.isAnonymous()) {
                sids.add(SecurityUtil.getPrincipalSid());
            }

            authentication.getAuthorities()
                .forEach(authority ->
                    sids.add(new GrantedAuthoritySid(authority.getAuthority())));

            return sids;
        };
    }
}
