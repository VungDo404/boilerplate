package com.app.boilerplate.Aop.AclAware;

import com.app.boilerplate.Service.Authorization.AuthorizeService;
import com.app.boilerplate.Shared.Common.Identifiable;
import com.app.boilerplate.Util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Order(20)
@RequiredArgsConstructor
@Component
public class IdentifiableHandler implements AclAwareHandler {
    private final AuthorizeService authorizeService;

    @Override
    public boolean supports(final Object result) {
        return result instanceof Identifiable<?>;
    }

    @Override
    public void handle(final Object result, final List<Integer> permissions) {
        String sid = SecurityUtil.getPrincipalSid().toString();
        authorizeService.createAcl(true, sid, true, List.of((Identifiable<?>) result), permissions);
    }
}
