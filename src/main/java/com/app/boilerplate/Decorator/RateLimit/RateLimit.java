package com.app.boilerplate.Decorator.RateLimit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.temporal.ChronoUnit;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {
    long capacity() default 10;
    long tokens() default 1;
    long duration() default 1;
    ChronoUnit timeUnit() default ChronoUnit.MINUTES;
    String key() default "'default'";
}
