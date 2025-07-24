package com.app.boilerplate.Aop.AclAware;

import com.app.boilerplate.Service.Authorization.AuthorizeService;
import com.app.boilerplate.Shared.Common.Identifiable;
import com.app.boilerplate.Shared.Common.IdentifiableUserResource;
import com.app.boilerplate.Util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Component
public class IterableHandler implements AclAwareHandler{
    private final AuthorizeService authorizeService;

    @Override
    public boolean supports(final Object result) {
        return result instanceof Iterable<?>;
    }

    @Override
    public void handle(final Object result, final List<Integer> permissions) throws SQLException {
        Iterable<?> iterable = (Iterable<?>) result;

        List<IdentifiableUserResource<?>> iurList = new ArrayList<>();
        List<Identifiable<?>> iList = new ArrayList<>();

        for (Object item : iterable) {
            if (item instanceof IdentifiableUserResource<?> iur) {
                iurList.add(iur);
            } else if (item instanceof Identifiable<?> i) {
                iList.add(i);
            }
        }

        if (!iurList.isEmpty()) {
            authorizeService.createAcl(true, iurList, permissions);
        } else if (!iList.isEmpty()) {
            String sid = SecurityUtil.getPrincipalSid().toString();
            authorizeService.createAcl(true, sid, true, iList, permissions);
        }
    }
}
