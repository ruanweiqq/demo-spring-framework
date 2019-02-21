package org.ruanwei.demo.springframework.core.ioc.lifecycle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.Lifecycle;

// Startup and shutdown callbacks
public class MyLifecycle implements Lifecycle {
	private static Log log = LogFactory.getLog(MyLifecycle.class);

	private volatile boolean running = false;

	@Override
	public void start() {
		log.info("====================start()");
		if (running == false) {
			running = true;
		}
	}

	@Override
	public void stop() {
		log.info("====================stop()");
		if (running == true) {
			running = false;
		}
	}

	@Override
	public boolean isRunning() {
		log.info("====================isRunning()");
		return running;
	}

}
