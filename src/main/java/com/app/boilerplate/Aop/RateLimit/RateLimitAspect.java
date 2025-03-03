package com.app.boilerplate.Aop.RateLimit;

import com.app.boilerplate.Decorator.RateLimit.RateLimit;
import com.app.boilerplate.Exception.RateLimitExceededException;
import com.app.boilerplate.Service.RateLimiter.RateLimiterService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

@Aspect
@RequiredArgsConstructor
@Component
public class RateLimitAspect {
    private final RateLimiterService rateLimiterService;
    private final ExpressionParser expressionParser = new SpelExpressionParser();
    private final HttpServletResponse response;
    private final HttpServletRequest request;

    @Around("@annotation(com.app.boilerplate.Decorator.RateLimit.RateLimit) || @within(com.app.boilerplate.Decorator" +
        ".RateLimit.RateLimit)")
    public Object rateLimit(ProceedingJoinPoint joinPoint) throws Throwable {
        final var signature = (MethodSignature) joinPoint.getSignature();
        final var method = signature.getMethod();

        final var rateLimitAnnotation = Optional.ofNullable(method.getAnnotation(RateLimit.class))
            .orElseGet(() -> method.getDeclaringClass()
                .getAnnotation(RateLimit.class));

        final var keyExpression = rateLimitAnnotation.key();
        final var capacity = rateLimitAnnotation.capacity();
        final var duration = Duration.of(rateLimitAnnotation.duration(), rateLimitAnnotation.timeUnit());
        final var tokens = rateLimitAnnotation.tokens();

        final var key = evaluateKey(keyExpression, joinPoint);
        final var probe = rateLimiterService.tryConsume(key, capacity, tokens, duration);
        if (!probe.isConsumed()) {
            final var retryInSeconds = probe.getNanosToWaitForRefill() / 1_000_000_000;
            response.setHeader("X-Rate-Limit-Retry-After-Seconds", String.valueOf(retryInSeconds));
            throw new RateLimitExceededException();
        }
        response.setHeader("X-Rate-Limit-Remaining", String.valueOf(probe.getRemainingTokens()));
        return joinPoint.proceed();
    }

    private String evaluateKey(String keyExpression, ProceedingJoinPoint joinPoint) {
        final var context = new StandardEvaluationContext();

        final var methodSignature = (MethodSignature) joinPoint.getSignature();
        final var paramNames = methodSignature.getParameterNames();
        final var args = joinPoint.getArgs();

        if (paramNames != null) {
            for (int i = 0; i < paramNames.length; i++) {
                context.setVariable(paramNames[i], args[i]);
            }
        }

        context.setVariable("request", request);
        context.setVariable("ip", request.getRemoteAddr());

        final var expression = expressionParser.parseExpression(keyExpression);
        try {
            return expression.getValue(context, String.class);
        } catch (EvaluationException e) {
            return keyExpression;
        }
    }
}
