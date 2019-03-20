package org.ruanwei.demo.springframework.core.ioc.lifecycle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

@Component
public class MyDisposableBean2 implements DisposableBean {
	private static Log log = LogFactory.getLog(MyDisposableBean2.class);

	@Override
	public void destroy() throws Exception {
		log.info("====================destroy()");
	}

}
