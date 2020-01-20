package org.ruanwei.demo.springframework.core.ioc.lifecycle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ruanwei.demo.util.Recorder;
import org.springframework.context.Lifecycle;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

//Startup and shutdown callbacks
@Order(Ordered.LOWEST_PRECEDENCE)
@Component
public class MyLifecycle implements Lifecycle {
	private static Log log = LogFactory.getLog(MyLifecycle.class);

	private volatile boolean running = false;

	@Override
	public void start() {
		log.info("====================start()");
		if (running == false) {
			running = true;
		}
		Recorder.record("start()", this.getClass());
	}

	@Override
	public void stop() {
		log.info("====================stop()");
		if (running == true) {
			running = false;
		}
		Recorder.record("stop()", this.getClass());
	}

	@Override
	public boolean isRunning() {
		log.info("====================isRunning()");
		return running;
	}

}
