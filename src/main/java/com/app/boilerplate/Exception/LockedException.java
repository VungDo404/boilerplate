package com.app.boilerplate.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

public class LockedException extends ErrorResponseException {
    public LockedException(Object... args) {
        super(
            HttpStatus.LOCKED,
            ProblemDetail.forStatusAndDetail(HttpStatus.LOCKED, ""),
            null,
            "error.auth.locked",
            args
             );

    }
}
