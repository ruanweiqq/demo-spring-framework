package org.ruanwei.demo.springframework.core.ioc.lifecycle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.LifecycleProcessor;

/**
 * 默认是被ApplicationContext主动调用
 * 默认实现是DefaultLifecycleProcessor
 * 所以这里是起了一个普通的Lifecycle作用
 * @author ruanwei
 *
 */
public class MyLifecycleProcessor implements LifecycleProcessor {
	private static Log log = LogFactory.getLog(MyLifecycleProcessor.class);
	
	private volatile boolean running = false;

	@Override
	public void onRefresh() {
		log.info("====================onRefresh()");
		this.running = true;
	}

	@Override
	public void onClose() {
		log.info("====================onClose()");
		this.running = false;
	}
	
	// ==================================================

	@Override
	public void start() {
		log.info("====================start()");
		if (this.running == false) {
			this.running = true;
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

}
