package org.ruanwei.demo.springframework;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class SpringApplicaiton2 {
	private static Log log = LogFactory.getLog(SpringApplicaiton2.class);

	private static AbstractApplicationContext context;
	static {
		log.info("0======================================================================================");
		initApplicationContext(ApplicationContextType.ANNOTATION_CONFIG);
		log.info("0======================================================================================");
	}

	public static void main(String[] args) {
		log.info("Hello, World!" + context);
	}

	private static void initApplicationContext(ApplicationContextType type) {
		switch (type) {
		case ANNOTATION_CONFIG: {// GenericApplicationContext
			context = new AnnotationConfigApplicationContext(AppConfig2.class);
			// 要在getBean之前进行设置并刷新
			// context.register(AppConfig.class);
			// context.getEnvironment().setActiveProfiles("development");
			// context.refresh();
			break;
		}
		case CLASSPATH_XML: {// AbstractRefreshableApplicationContext
			context = new ClassPathXmlApplicationContext(
					new String[] { "classpath:spring/applicationContext2.xml" });
			// 要在getBean之前进行设置并刷新
			// context.setConfigLocation("spring/applicationContext.xml");
			// context.getEnvironment().setActiveProfiles("development");
			// context.refresh();
			break;
		}
		case FILESYSTEM_XML: {// AbstractRefreshableApplicationContext
			context = new FileSystemXmlApplicationContext(
					new String[] { "file:spring/applicationContext.xml" });
			break;
		}
		default:
		}

		context.registerShutdownHook();
	}

	public enum ApplicationContextType {
		CLASSPATH_XML, FILESYSTEM_XML, ANNOTATION_CONFIG
	}
}
