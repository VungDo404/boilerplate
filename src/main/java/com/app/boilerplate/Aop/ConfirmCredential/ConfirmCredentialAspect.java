package com.app.boilerplate.Aop.ConfirmCredential;

import com.app.boilerplate.Exception.CredentialsNotConfirmedException;
import com.app.boilerplate.Service.Authentication.AuthService;
import com.app.boilerplate.Util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@RequiredArgsConstructor
@Component
public class ConfirmCredentialAspect {
    private final AuthService authService;

    @Before("@annotation(com.app.boilerplate.Decorator.Confirmcredential.ConfirmCredential)")
    public void checkSecurityAction(JoinPoint joinPoint) throws CredentialsNotConfirmedException {
        if(!authService.isConfirmCredentials(SecurityUtil.getUserId()))
            throw new CredentialsNotConfirmedException("error.auth.re-confirm");
    }
}
