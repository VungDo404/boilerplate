package com.app.boilerplate.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

public class RateLimitExceededException extends ErrorResponseException {
    public RateLimitExceededException() {
        super(
            HttpStatus.TOO_MANY_REQUESTS,
            ProblemDetail.forStatusAndDetail(HttpStatus.TOO_MANY_REQUESTS, ""),
            null,
            "error.rate-limit.exceed",
            null
             );

    }
}
