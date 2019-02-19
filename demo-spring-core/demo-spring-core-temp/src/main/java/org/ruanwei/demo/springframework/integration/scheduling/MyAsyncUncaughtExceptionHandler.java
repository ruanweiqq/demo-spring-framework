package org.ruanwei.demo.springframework.integration.scheduling;

import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

public class MyAsyncUncaughtExceptionHandler implements
		AsyncUncaughtExceptionHandler {
	private static Log logger = LogFactory
			.getLog(MyAsyncUncaughtExceptionHandler.class);

	@Override
	public void handleUncaughtException(Throwable ex, Method method,
			Object... params) {
		logger.info("handleUncaughtException(Throwable ex, Method method, Object... params)");
	}

}
