package org.ruanwei.demo.springframework.core.ioc;

import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.support.MethodReplacer;

public class MyMethodReplacer implements MethodReplacer {
	private static Log log = LogFactory.getLog(MyMethodReplacer.class);

	@Override
	public Object reimplement(Object obj, Method method, Object[] args) throws Throwable {
		log.info("reimplement(Object obj, Method method, Object[] args)" + method);

		int a = (int) args[0];
		int b = (int) args[1];
		return a + b + 10;

		// return method.invoke(obj, args);
	}
}
