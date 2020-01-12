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
import org.ruanwei.demo.util.Recorder;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.PayloadApplicationEvent;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.event.ApplicationContextEvent;

public class People implements SmartLifecycle, ApplicationListener<ApplicationEvent>, InitializingBean, DisposableBean {
	private static Log log = LogFactory.getLog(People.class);

	private volatile boolean running = true;

	public static volatile int EVENT_COUNT;
	public static volatile int CONTEXT_EVENT_COUNT;
	public static volatile int PAYLOAD_EVENT_COUNT;

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
		log.info("====================onApplicationEvent(ApplicationEvent event)" + event);
		log.info(this + " recieved a " + event.getClass() + " from " + event.getSource());
		Recorder.record("onApplicationEvent(ApplicationEvent event)", this.getClass());

		if (event instanceof MyApplicationEvent) {
			String message = ((MyApplicationEvent) event).getMessage();
			log.info(event.getTimestamp() + " message=" + message);
			EVENT_COUNT++;
		} else if (event instanceof ApplicationContextEvent) {
			ApplicationContext context = ((ApplicationContextEvent) event).getApplicationContext();
			log.info(event.getTimestamp() + " context=" + context);
			CONTEXT_EVENT_COUNT++;
		} else if (event instanceof PayloadApplicationEvent<?>) {
			Object payload = ((PayloadApplicationEvent<?>) event).getPayload();
			log.info(event.getTimestamp() + " payload=" + payload.toString());
			PAYLOAD_EVENT_COUNT++;
		} else {
			long timestamp = event.getTimestamp();
			log.info(event.getSource() + " timestamp=" + timestamp);
		}
	}

	// Bean initialization callback
	public void init() {
		log.info("====================init()");
		Recorder.record("init()", this.getClass());
	}

	// Bean initialization callback
	@Override
	public void afterPropertiesSet() throws Exception {
		log.info("====================afterPropertiesSet()");
		Recorder.record("afterPropertiesSet()", this.getClass());
	}

	// Bean destruction callback
	public void destroy2() {
		log.info("====================destroy()");
		Recorder.record("destroy2()", this.getClass());
	}

	@Override
	// Bean destruction callback
	public void destroy() {
		log.info("====================destroy()");
		Recorder.record("destroy()", this.getClass());
	}

	// Context life cycle Startup callback
	@Override
	public void start() {
		log.info("====================start()");
		if (this.running == false) {
			running = true;
		}
		Recorder.record("start()", this.getClass());
	}

	// Context life cycle Shutdown callback
	@Override
	public void stop() {
		log.info("====================stop()");
		if (this.running == true) {
			this.running = false;
		}
		Recorder.record("stop()", this.getClass());
	}

	// Context life cycle
	@Override
	public boolean isRunning() {
		log.info("====================isRunning()" + this.running);
		return this.running;
	}

	// Context SmartLifecycle Shutdown callback
	@Override
	public void stop(Runnable callback) {
		log.info("====================stop(Runnable callback)" + callback);
		if (this.running == true) {
			this.running = false;
		}
		callback.run();
		Recorder.record("stop(Runnable callback)", this.getClass());
	}

	// Context SmartLifecycle Shutdown callback
	@Override
	public boolean isAutoStartup() {
		return true;
	}

	// Context SmartLifecycle Shutdown callback
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
