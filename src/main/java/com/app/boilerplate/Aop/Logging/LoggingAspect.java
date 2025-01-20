package com.app.boilerplate.Aop.Logging;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Aspect
@RequiredArgsConstructor
@Component
public class LoggingAspect {
	private final transient AsyncLogging asyncLogger;

	@Pointcut(
		"within(@org.springframework.stereotype.Repository *)" +
			" || within(@org.springframework.stereotype.Service *)" +
			" || within(@org.springframework.web.bind.annotation.RestController *)"
	)
	public void springBeanPointcut() {}

	@Pointcut("within(com.app.boilerplate.Repository..*)" + " || within(com.app.boilerplate.Service..*)" + " || " +
		"within(com.app.boilerplate.Controller..*)"
	)
	public void applicationPackagePointcut() {}

	@Around("applicationPackagePointcut() && springBeanPointcut()")
	public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
		Logger logger = LoggerFactory.getLogger(joinPoint.getSignature().getDeclaringTypeName());

		MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
		String className = methodSignature.getDeclaringType().getSimpleName();
		String methodName = methodSignature.getName();

		asyncLogger.logMethodEntry(logger, className, methodName, joinPoint.getArgs());

		final var stopWatch = new StopWatch();
		stopWatch.start();

		try {
			Object result = joinPoint.proceed();
			stopWatch.stop();

			asyncLogger.logMethodExit(logger, className, methodName, result, stopWatch.getTotalTimeMillis());

			return result;
		} catch (Throwable throwable) {
			stopWatch.stop();
			asyncLogger.logExceptionSync(logger, className, methodName, throwable, stopWatch.getTotalTimeMillis());
			throw throwable;
		}
	}

	@AfterThrowing(pointcut = "applicationPackagePointcut() && springBeanPointcut()", throwing = "throwable")
	public void logAfterThrowing(JoinPoint joinPoint, Throwable throwable) {
		Logger logger = LoggerFactory.getLogger(joinPoint.getSignature().getDeclaringTypeName());
		MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
		String className = methodSignature.getDeclaringType().getSimpleName();
		String methodName = methodSignature.getName();


		// Also log asynchronously for consistency with other logs
		asyncLogger.logExceptionSync(logger, className, methodName, throwable, -1);
	}


}
