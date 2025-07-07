package com.app.boilerplate.Util;

import com.app.boilerplate.Security.AuthenticationOAuth2User;
import com.app.boilerplate.Shared.Authentication.AccessJwt;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class SecurityUtil {
    public static AccessJwt getAccessJwt() {
        final var context = SecurityContextHolder.getContext();
        final var authentication = (Authentication) context.getAuthentication();
        final var principal = authentication.getPrincipal();
        return (AccessJwt) principal;
    }

    public static Object getPrincipal() {
        final var context = SecurityContextHolder.getContext();
        final var authentication = context.getAuthentication();

        if (authentication == null) {
            throw new IllegalStateException("No authentication found in security context");
        }

        return authentication.getPrincipal();
    }

    public static Set<GrantedAuthority> getAuthorities() {
        final var context = SecurityContextHolder.getContext();
        final var authentication = (Authentication) context.getAuthentication();
        return new HashSet<>(authentication.getAuthorities());
    }

    public static PrincipalSid getPrincipalSid() {
        final var principal = getPrincipal();

        if (isAnonymous()) {
            return new PrincipalSid((String) principal);
        } else if (principal instanceof AccessJwt jwt) {
            final var sidValue = String.format("%s:%s",
                jwt.getProvider()
                    .name(),
                jwt.getUsername());
            return new PrincipalSid(sidValue);
        } else {
            throw new IllegalStateException("Unknown principal type: " + principal.getClass()
                .getName());
        }
    }

    public static boolean isAnonymous() {
        final var principal = getPrincipal();
        return (principal == null) || (principal instanceof String strPrincipal && strPrincipal.equals(AppConsts.ANONYMOUS_USER)) || (principal instanceof AuthenticationOAuth2User);
    }

    public static UUID getUserId() {
        final var principal = getPrincipal();

        if (isAnonymous()) {
            return AppConsts.SYSTEM_USER_ID;
        } else if (principal instanceof AccessJwt jwt) {
            return jwt.getSub();
        } else {
            throw new IllegalStateException("Unknown principal type: " + principal.getClass()
                .getName());
        }
    }
}
