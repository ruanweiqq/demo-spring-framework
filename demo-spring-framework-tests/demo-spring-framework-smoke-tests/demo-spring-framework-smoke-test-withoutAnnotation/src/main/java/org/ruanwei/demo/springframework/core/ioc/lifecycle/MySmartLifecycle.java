package org.ruanwei.demo.springframework.core.ioc.lifecycle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ruanwei.demo.util.Recorder;
import org.springframework.context.SmartLifecycle;

// Startup and shutdown callbacks
public class MySmartLifecycle implements SmartLifecycle {
	private static Log log = LogFactory.getLog(MySmartLifecycle.class);
	
	private volatile boolean running = false;

	@Override
	public boolean isAutoStartup() {
		log.info("====================isAutoStartup()");
		return true;
	}

	@Override
	public void stop(Runnable callback) {
		log.info("====================stop(Runnable callback)"+callback);
		if (this.running == true) {
			this.running = false;
		}
		callback.run();
		Recorder.record("stop(Runnable callback)", this.getClass());
	}

	// ==================================================
	
	@Override
	public void start() {
		log.info("====================start()");
		if (this.running == false) {
			running = true;
		}
		Recorder.record("start()", this.getClass());
	}

	@Override
	public void stop() {
		log.info("====================stop()");
		if (this.running == true) {
			this.running = false;
		}
		Recorder.record("stop()", this.getClass());
	}

	@Override
	public boolean isRunning() {
		log.info("====================isRunning()"+this.running);
		return this.running;
	}
	
	// ==================================================

	@Override
	public int getPhase() {
		log.info("getPhase()");
		return -1;
	}

}
