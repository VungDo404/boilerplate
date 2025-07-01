package com.app.boilerplate.Shared.Authorization.Event;

import com.app.boilerplate.Shared.Common.Identifiable;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ObjectIdentityEvent<T extends Identifiable<?>> {
    private T entity;
}
