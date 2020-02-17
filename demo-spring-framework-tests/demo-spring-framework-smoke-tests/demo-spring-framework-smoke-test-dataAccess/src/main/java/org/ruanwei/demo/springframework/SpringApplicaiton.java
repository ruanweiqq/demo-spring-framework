package org.ruanwei.demo.springframework;

import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ruanwei.demo.springframework.dataAccess.jdbc.JdbcDao;
import org.ruanwei.demo.springframework.dataAccess.jdbc.UserJdbcDao;
import org.ruanwei.demo.springframework.dataAccess.jdbc.entity.UserJdbcEntity;
import org.ruanwei.demo.springframework.dataAccess.orm.mybatis.MyBatisMapper;
import org.ruanwei.demo.springframework.dataAccess.orm.mybatis.entity.UserMyBatisEntity;
import org.ruanwei.demo.springframework.dataAccess.springdata.jdbc.UserJdbcRepository;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class SpringApplicaiton {
	private static Log log = LogFactory.getLog(SpringApplicaiton.class);

	private static AbstractApplicationContext context;
	static {
		log.info("0======================================================================================");
		initApplicationContext(ApplicationContextType.ANNOTATION_CONFIG);
		log.info("0======================================================================================");
	}

	public static void main(String[] args) {
		log.info("Hello, World!" + context);
		UserJdbcDao userJdbcDao = context.getBean(UserJdbcDao.class);
		log.info("userJdbcDao====" + userJdbcDao);
		MyBatisMapper<UserMyBatisEntity, Integer> userMyBatisMapper = context.getBean(MyBatisMapper.class);
		log.info("userMyBatisMapper====" + userMyBatisMapper);
		UserJdbcRepository userJdbcRepository = context.getBean(UserJdbcRepository.class);
		log.info("userJdbcRepository====" + userJdbcRepository);
	}

	private static String toString2(Iterable<Integer> ids) {
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

	private static String toString1(Integer[] ids) {
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
