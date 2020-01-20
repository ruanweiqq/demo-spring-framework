package org.ruanwei.demo.springframework.core.ioc;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ruanwei.demo.springframework.core.ioc.databinding.validation.FamilyName;
import org.ruanwei.demo.springframework.core.ioc.event.MyApplicationEvent;
import org.ruanwei.demo.util.Recorder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.PayloadApplicationEvent;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.ApplicationContextEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

//import org.springframework.transaction.event.TransactionalEventListener;

@Lazy
@Component("father")
public class People implements SmartLifecycle {
	private static Log log = LogFactory.getLog(People.class);

	private volatile boolean running = false;
	
	public static volatile int EVENT_COUNT;
	public static volatile int CONTEXT_EVENT_COUNT;
	public static volatile int PAYLOAD_EVENT_COUNT;

	// JSR-303 Bean Validation
	@NotEmpty
	@Size(min = 1, max = 12)
	@FamilyName("ruan")
	private String name;

	@Min(0)
	@Max(100)
	private int age;

	// a.Bean instantiation with a constructor
	// 1.Constructor-based dependency injection
	public People(@Value("${father.name:ruanwei_def}") String name,
			@Value("${father.age:35}") int age) {
		this.name = name;
		this.age = age;
		log.info("People(String name,int age)" + this);
	}

	// ApplicationListener callback
	@Order(1)
	// @Async
	@EventListener({ ApplicationEvent.class })
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

	// Initialization callback
	@PostConstruct
	public void init() {
		log.info("====================init()");
		Recorder.record("init()", this.getClass());
	}

	// Destruction callback
	@PreDestroy
	public void destroy() {
		log.info("====================destroy()");
		Recorder.record("destroy()", this.getClass());
	}

	// Lifecycle Startup callback
	@Override
	public void start() {
		log.info("====================start()");
		if (this.running == false) {
			running = true;
		}
		Recorder.record("start()", this.getClass());
	}

	// Lifecycle Shutdown callback
	@Override
	public void stop() {
		log.info("====================stop()");
		if (this.running == true) {
			this.running = false;
		}
		Recorder.record("stop()", this.getClass());
	}

	// Lifecycle
	@Override
	public boolean isRunning() {
		log.info("====================isRunning()");
		return this.running;
	}

	// SmartLifecycle Shutdown callback
	@Override
	public void stop(Runnable callback) {
		log.info("====================stop(Runnable callback)");
		if (this.running == true) {
			this.running = false;
		}
		callback.run();
		Recorder.record("stop(Runnable callback)", this.getClass());
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

	public int getAge() {
		return this.age;
	}

	public String getName() {
		return this.name;
	}

	@Override
	public String toString() {
		return "People [name=" + name + ", age=" + age + "]";
	}

}
