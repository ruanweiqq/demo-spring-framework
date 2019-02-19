package org.ruanwei.demo.springframework.core.ioc.lifecycle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.Lifecycle;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(Ordered.LOWEST_PRECEDENCE)
@Component
public class MyLifecycle2 implements Lifecycle {
	private static Log log = LogFactory.getLog(MyLifecycle2.class);

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
