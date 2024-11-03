package com.app.boilerplate.Aop.Logging;

import org.slf4j.Logger;

public interface AsyncLogging {
	void logMethodEntry(Logger logger, String className, String methodName, Object[] args);
	void logMethodExit(Logger logger, String className, String methodName, Object result, long executionTime);
	void logExceptionSync(Logger logger, String className, String methodName, Throwable throwable, long executionTime);
}
