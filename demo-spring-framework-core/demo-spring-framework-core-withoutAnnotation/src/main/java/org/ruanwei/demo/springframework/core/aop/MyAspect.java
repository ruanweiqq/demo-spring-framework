package org.ruanwei.demo.springframework.core.aop;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StopWatch;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;

public class MyAspect {
	private static Log log = LogFactory.getLog(MyAspect.class);

	public void before(Object t, String message) {
		log.info("before() message=" + message + " this=" + t);
	}

	public void after(Object t, String message) {
		log.info("after() message=" + message + " this=" + t);
	}

	public void afterReturning(Object t, String message, Object ret) {
		log.info("afterReturning() message=" + message + " this=" + t + " ret=" + ret);
	}

	public void afterThrowing(JoinPoint jp, Object t, String message, Throwable e) {
		log.info("afterThrowing() message=" + message + " JoinPoint=" + jp + " this=" + t + " e=" + e);
		log.error(e);
	}

	public Object around(ProceedingJoinPoint call, String message) throws Throwable {
		log.info("around() message=" + message + " call=" + call);

		StopWatch clock = new StopWatch("message=" + message);
		try {
			clock.start(call.toShortString());
			log.info(clock.prettyPrint());
			Object[] args = call.getArgs();
			for (Object arg : args) {
				log.info("around() arg=" + arg);
			}
			Object ret = call.proceed();
			log.info("around() ret=" + ret);
			return ret;
		} finally {
			clock.stop();
			log.info(clock.prettyPrint());
		}
	}

}
