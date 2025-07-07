package com.app.boilerplate.Shared.Authorization.Event;

import com.app.boilerplate.Shared.Common.Identifiable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.acls.domain.PrincipalSid;

@Getter
@AllArgsConstructor
public class AclEvent<T extends Identifiable<?>> {
    private T entity;
    private PrincipalSid principalSid;
}
