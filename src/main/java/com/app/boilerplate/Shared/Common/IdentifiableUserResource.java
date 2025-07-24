package com.app.boilerplate.Shared.Common;

import com.app.boilerplate.Domain.User.User;

public interface IdentifiableUserResource<T> extends Identifiable<T> {
    User getUser();
}
