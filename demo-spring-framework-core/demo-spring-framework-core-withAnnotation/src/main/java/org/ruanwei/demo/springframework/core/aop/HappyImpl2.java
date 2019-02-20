package org.ruanwei.demo.springframework.core.aop;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class HappyImpl2 implements Happy2 {
	private static Log log = LogFactory.getLog(HappyImpl2.class);

	@Override
	public String happy(String msg) {
		log.info("happy()" + msg);
		return "Happy " + msg;
	}

}
