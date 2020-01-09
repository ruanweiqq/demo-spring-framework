package org.ruanwei.demo.springframework.core.ioc.lifecycle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component
public class MyInitializingBean2 implements InitializingBean {
	private static Log log = LogFactory.getLog(MyInitializingBean2.class);

	@Override
	public void afterPropertiesSet() throws Exception {
		log.info("====================afterPropertiesSet()");
	}

}
