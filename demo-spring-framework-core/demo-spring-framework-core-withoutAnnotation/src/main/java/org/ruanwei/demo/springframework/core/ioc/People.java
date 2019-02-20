package org.ruanwei.demo.springframework.core.ioc;

import java.beans.ConstructorProperties;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ruanwei.demo.springframework.core.ioc.databinding.validation.FamilyName;
import org.ruanwei.demo.springframework.core.ioc.event.MyApplicationEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.PayloadApplicationEvent;
import org.springframework.context.SmartLifecycle;

public class People implements SmartLifecycle,
		ApplicationListener<ApplicationEvent> {
	private static Log log = LogFactory.getLog(People.class);

	private volatile boolean running = true;

	// JSR-303 Bean Validation
	@NotEmpty
	@Size(min = 1, max = 15)
	@FamilyName("ruan")
	private String name;

	@Min(0)
	@Max(100)
	private int age;

	// a.Bean instantiation with a constructor
	// 1.Constructor-based dependency injection(byName with javac -g)
	@ConstructorProperties({ "name", "age" })
	public People(String name, int age) {
		this.name = name;
		this.age = age;
		log.info("People(String name, int age)" + this);
	}

	// ApplicationListener callback
	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		log.info("====================onApplicationEvent(ApplicationEvent event)"
				+ event);
		log.info("Recieve " + event.getClass() + " from " + event.getSource());

		if (event instanceof PayloadApplicationEvent<?>) {
			Object payload = ((PayloadApplicationEvent<?>) event).getPayload();
			log.info(event.getTimestamp() + " payload=" + payload.toString());
		} else if (event instanceof MyApplicationEvent) {
			String message = ((MyApplicationEvent) event).getMessage();
			log.info(event.getTimestamp() + " message=" + message);
		} else {
			long timestamp = event.getTimestamp();
			log.info(event.getSource() + " timestamp=" + timestamp);
		}
	}

	// Initialization callback
	public void init() {
		log.info("====================init()");
	}

	// Destruction callback
	public void destroy() {
		log.info("====================destroy()");
	}

	// Lifecycle Startup callback
	@Override
	public void start() {
		log.info("====================start()");
		if (this.running == false) {
			running = true;
		}
	}

	// Lifecycle Shutdown callback
	@Override
	public void stop() {
		log.info("====================stop()");
		if (this.running == true) {
			this.running = false;
		}
	}

	// Lifecycle
	@Override
	public boolean isRunning() {
		log.info("====================isRunning()" + this.running);
		return this.running;
	}

	// SmartLifecycle Shutdown callback
	@Override
	public void stop(Runnable callback) {
		log.info("====================stop(Runnable callback)" + callback);
		if (this.running == true) {
			this.running = false;
		}
		callback.run();
	}

	// SmartLifecycle Shutdown callback
	@Override
	public boolean isAutoStartup() {
		return true;
	}

	@Override
	public int getPhase() {
		return -2;
	}

	public String getName() {
		return this.name;
	}

	public int getAge() {
		return this.age;
	}

	@Override
	public String toString() {
		return "People [name=" + name + ", age=" + age + "]";
	}

}
