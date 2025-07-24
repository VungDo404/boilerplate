package com.app.boilerplate.Aop.AclAware;

import java.sql.SQLException;
import java.util.List;

public interface AclAwareHandler {
    boolean supports(Object result);
    void handle(Object result, List<Integer> permissions) throws SQLException;
}
