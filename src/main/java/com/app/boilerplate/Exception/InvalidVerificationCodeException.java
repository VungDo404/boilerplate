package com.app.boilerplate.Exception;

import org.springframework.security.core.AuthenticationException;

public class InvalidVerificationCodeException extends AuthenticationException {
    public InvalidVerificationCodeException(String msg) {
        super(msg);
    }

    public InvalidVerificationCodeException(String msg, Throwable t) {
        super(msg, t);
    }
}
