package org.ruanwei.demo.springframework.core.ioc.lifecycle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;

public class MyInitializingBean implements InitializingBean {
	private static Log log = LogFactory.getLog(MyInitializingBean.class);

	@Override
	public void afterPropertiesSet() throws Exception {
		log.info("====================afterPropertiesSet()");
	}

}
