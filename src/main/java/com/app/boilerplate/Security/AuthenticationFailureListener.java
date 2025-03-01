package com.app.boilerplate.Security;

import com.app.boilerplate.Service.Account.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AuthenticationFailureListener implements
    ApplicationListener<AuthenticationFailureBadCredentialsEvent> {
    private final AccountService accountService;
    @Override
    public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent event) {
        final var principal = (String) event.getAuthentication().getPrincipal();
        if (event.getException() instanceof BadCredentialsException){
            accountService.processLockoutFailed(principal);
        }
    }
}
