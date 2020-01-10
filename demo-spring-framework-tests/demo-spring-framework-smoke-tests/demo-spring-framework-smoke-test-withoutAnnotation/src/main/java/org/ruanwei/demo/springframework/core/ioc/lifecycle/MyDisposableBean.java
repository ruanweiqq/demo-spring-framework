package org.ruanwei.demo.springframework.core.ioc.lifecycle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ruanwei.demo.util.Recorder;
import org.springframework.beans.factory.DisposableBean;

public class MyDisposableBean implements DisposableBean {
	private static Log log = LogFactory.getLog(MyDisposableBean.class);

	@Override
	public void destroy() throws Exception {
		log.info("====================destroy()");
		Recorder.record("destroy()", this.getClass());
	}

}
