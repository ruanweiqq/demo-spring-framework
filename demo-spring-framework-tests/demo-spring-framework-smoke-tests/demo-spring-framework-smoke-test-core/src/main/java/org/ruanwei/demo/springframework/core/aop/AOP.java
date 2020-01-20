package org.ruanwei.demo.springframework.core.aop;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

@Component("aop")
public class AOP {
	private static Log log = LogFactory.getLog(AOP.class);

	public String sayHello(String message) {
		log.info("sayHello(String message)" + message);

		return "Hello," + message;
	}

}
