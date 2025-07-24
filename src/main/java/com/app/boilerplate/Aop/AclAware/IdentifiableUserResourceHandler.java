package com.app.boilerplate.Aop.AclAware;

import com.app.boilerplate.Service.Authorization.AuthorizeService;
import com.app.boilerplate.Shared.Common.IdentifiableUserResource;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.List;

@Order(10)
@RequiredArgsConstructor
@Component
public class IdentifiableUserResourceHandler implements AclAwareHandler {
    private final AuthorizeService authorizeService;
    @Override
    public boolean supports(final Object result) {
        return result instanceof IdentifiableUserResource<?>;
    }

    @Override
    public void handle(final Object result, final List<Integer> permissions) throws SQLException {
        authorizeService.createAcl(true, List.of((IdentifiableUserResource<?>) result), permissions);
    }
}
