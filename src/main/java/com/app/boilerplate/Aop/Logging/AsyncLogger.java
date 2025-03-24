package com.app.boilerplate.Aop.Logging;

import org.slf4j.Logger;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class AsyncLogger implements AsyncLogging {
	@Async
	public void logMethodEntry(Logger logger, String className, String methodName, Object[] args) {
		logger.info("→ {}.{}() with argument[s]: {}",
			className,
			methodName,
			Arrays.toString(args)
		);
	}

	@Async
	public void logMethodExit(Logger logger, String className, String methodName, Object result, long executionTime) {
		logger.info("← {}.{}() returned: {} (execution time: {} ms)",
			className,
			methodName,
			result,
			executionTime
		);
	}

	public void logExceptionSync(Logger logger, String className, String methodName, Throwable throwable,
								 long executionTime) {
		String executionTimeStr = executionTime >= 0 ? String.format(" (execution time: %d ms)", executionTime) : "";
		logger.error("× Exception in {}.{}(). {}: with cause: {}{}",
			className,
			methodName,
			throwable.getClass().getSimpleName(),
			throwable.getCause() != null ? throwable.getCause().toString() : "NULL",
			executionTimeStr
		);

	}
}
