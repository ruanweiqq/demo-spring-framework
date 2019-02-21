package org.ruanwei.demo.springframework.core.ioc.lifecycle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.SmartLifecycle;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(Ordered.LOWEST_PRECEDENCE)
@Component
public class MySmartLifecycle2 implements SmartLifecycle {
	private static Log log = LogFactory.getLog(MySmartLifecycle2.class);

	private volatile boolean running = false;

	@Override
	public boolean isAutoStartup() {
		log.info("====================isAutoStartup()");
		return false;
	}

	@Override
	public void stop(Runnable callback) {
		log.info("====================stop(Runnable callback)");
		if (this.running == true) {
			this.running = false;
		}
		callback.run();
	}

	// ==================================================
	
	@Override
	public void start() {
		log.info("====================start()");
		if (this.running == false) {
			running = true;
		}
	}

	@Override
	public void stop() {
		log.info("====================stop()");
		if (this.running == true) {
			this.running = false;
		}
	}

	@Override
	public boolean isRunning() {
		log.info("====================isRunning()");
		return this.running;
	}

	@Override
	public int getPhase() {
		log.info("getPhase()");
		return -1;
	}

}
