package org.ruanwei.demo.springframework.core.aop;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

@Component("good")
public class GoodImpl2 implements Good2 {
	private static Log log = LogFactory.getLog(GoodImpl2.class);

	@Override
	public String good(String msg) {
		log.info("good()" + msg);
		return "Good " + msg;
	}

}
