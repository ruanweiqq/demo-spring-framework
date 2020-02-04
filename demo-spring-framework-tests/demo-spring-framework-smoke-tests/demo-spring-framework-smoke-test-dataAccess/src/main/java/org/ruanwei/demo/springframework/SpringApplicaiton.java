package org.ruanwei.demo.springframework;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

@Deprecated
public class SpringApplicaiton {
	private static Log log = LogFactory.getLog(SpringApplicaiton.class);

	private static AbstractApplicationContext context;
	static {
		log.info("0======================================================================================");
		// initApplicationContext(ApplicationContextType.ANNOTATION_CONFIG);
		log.info("0======================================================================================");
	}

	public static void main(String[] args) {
		log.info("Hello, World!" + context);
		Integer[] arr = { 1, 2, 3 };
		List<Integer> list = Arrays.asList(arr);
		log.info("0======================================================================================"
				+ toString1(arr));
		log.info("0======================================================================================"
				+ toString2(list));
	}

	public static String toString2(Iterable<Integer> ids) {
		if (ids == null)
			return "null";

		StringBuilder b = new StringBuilder();

		Iterator<Integer> iterator = ids.iterator();
		if (!iterator.hasNext()) {
			return "";
		}
		b.append(iterator.next());

		while (iterator.hasNext()) {
			b.append(", ");
			b.append(iterator.next());
		}
		return b.toString();
	}

	public static String toString1(Integer[] ids) {
		if (ids == null)
			return "null";
		int iMax = ids.length - 1;
		if (iMax == -1)
			return "";

		StringBuilder b = new StringBuilder();
		for (int i = 0;; i++) {
			b.append(ids[i]);
			if (i == iMax)
				return b.toString();
			b.append(", ");
		}
	}

	private static void initApplicationContext(ApplicationContextType type) {
		switch (type) {
		case ANNOTATION_CONFIG: {// GenericApplicationContext
			context = new AnnotationConfigApplicationContext(AppConfig.class);
			// 要在getBean之前进行设置并刷新
			// context.register(AppConfig.class);
			// context.getEnvironment().setActiveProfiles("development");
			// context.refresh();
			break;
		}
		case CLASSPATH_XML: {// AbstractRefreshableApplicationContext
			context = new ClassPathXmlApplicationContext(new String[] { "classpath:spring/applicationContext.xml" });
			// 要在getBean之前进行设置并刷新
			// context.setConfigLocation("spring/applicationContext.xml");
			// context.getEnvironment().setActiveProfiles("development");
			// context.refresh();
			break;
		}
		case FILESYSTEM_XML: {// AbstractRefreshableApplicationContext
			context = new FileSystemXmlApplicationContext(new String[] { "file:spring/applicationContext.xml" });
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
