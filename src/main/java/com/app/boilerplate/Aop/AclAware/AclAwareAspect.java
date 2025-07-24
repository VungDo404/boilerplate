package com.app.boilerplate.Aop.AclAware;

import com.app.boilerplate.Decorator.AclAware.AclAware;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Aspect
@RequiredArgsConstructor
@Component
public class AclAwareAspect {
    private final List<AclAwareHandler> aclHandlers;

    @AfterReturning(pointcut = "@annotation(aclAware)", returning = "result")
    public void aclAware(JoinPoint joinPoint, AclAware aclAware, Object result) throws Throwable {
        List<Integer> permissions = Arrays.stream(aclAware.permissions())
            .boxed()
            .collect(Collectors.toList());

        if (result instanceof CompletableFuture<?> future) {
            future.thenAccept(resolved -> handleAcl(resolved, permissions));
        } else {
            handleAcl(result, permissions);
        }
    }

    private void handleAcl(Object result, List<Integer> permissions) {
        aclHandlers.stream()
            .filter(handler -> handler.supports(result))
            .findFirst()
            .ifPresent(handler -> {
                try {
                    handler.handle(result, permissions);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
    }

}
