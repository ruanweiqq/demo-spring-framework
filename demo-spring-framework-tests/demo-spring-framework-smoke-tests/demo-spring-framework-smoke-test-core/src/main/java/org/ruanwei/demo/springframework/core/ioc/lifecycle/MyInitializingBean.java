package org.ruanwei.demo.springframework.core.ioc.lifecycle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ruanwei.demo.util.Recorder;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component
public class MyInitializingBean implements InitializingBean {
	private static Log log = LogFactory.getLog(MyInitializingBean.class);

	@Override
	public void afterPropertiesSet() throws Exception {
		log.info("====================afterPropertiesSet()");
		Recorder.record("afterPropertiesSet()", this.getClass());
	}

}
