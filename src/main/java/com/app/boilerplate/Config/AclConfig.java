package com.app.boilerplate.Config;

import com.app.boilerplate.Security.HierarchicalPermission;
import com.app.boilerplate.Security.HierarchicalPermissionGrantingStrategy;
import com.app.boilerplate.Service.Authorization.AccessControlListService;
import com.app.boilerplate.Util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.acls.AclPermissionEvaluator;
import org.springframework.security.acls.domain.*;
import org.springframework.security.acls.jdbc.BasicLookupStrategy;
import org.springframework.security.acls.model.Sid;
import org.springframework.security.acls.model.SidRetrievalStrategy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Configuration
public class AclConfig {
    private final DataSource dataSource;
    private final CacheManager cacheManager;

    @Bean
    public AclAuthorizationStrategy aclAuthorizationStrategy() {
        return new AclAuthorizationStrategyImpl(new SimpleGrantedAuthority("ROLE_AUTHENTICATION_ANONYMOUS"));
    }

    @Bean
    public HierarchicalPermissionGrantingStrategy permissionGrantingStrategy() {
        return new HierarchicalPermissionGrantingStrategy(auditLogger());
    }

    @Bean
    public SpringCacheBasedAclCache aclCache() {
        Cache redisCache = cacheManager.getCache("acl_cache");
        if (redisCache == null) {
            throw new IllegalStateException("Cache named 'acl_cache' must be configured in RedisCacheManager.");
        }
        return new SpringCacheBasedAclCache(redisCache, permissionGrantingStrategy(), aclAuthorizationStrategy());
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
    public BasicLookupStrategy lookupStrategy() {
        var basicLookupStrategy = new BasicLookupStrategy(
            dataSource,
            aclCache(),
            aclAuthorizationStrategy(),
            permissionGrantingStrategy()
        );
        basicLookupStrategy.setPermissionFactory(aclPermissionFactory());
        basicLookupStrategy.setAclClassIdSupported(true);

        return basicLookupStrategy;
    }

    @Bean
    public MethodSecurityExpressionHandler defaultMethodSecurityExpressionHandler(AccessControlListService aclService) {
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
