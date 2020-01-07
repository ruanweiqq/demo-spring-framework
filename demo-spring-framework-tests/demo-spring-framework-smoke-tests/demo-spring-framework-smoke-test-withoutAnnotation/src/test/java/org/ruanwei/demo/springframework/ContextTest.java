package org.ruanwei.demo.springframework;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.ruanwei.demo.springframework.core.ioc.Family;
import org.ruanwei.demo.springframework.core.ioc.House;
import org.ruanwei.demo.springframework.core.ioc.event.MyApplicationEvent;
import org.ruanwei.demo.springframework.core.ioc.extension.MyFamilyFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.AbstractRefreshableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.mock.env.MockPropertySource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

/**
 * 
 * @SpringJUnitConfig(AppConfig.class) is composed of
 * @ExtendWith(SpringExtension.class) and @ContextConfiguration(classes = AppConfig.class).
 * 
 * @SpringJUnitWebConfig also.
 * 
 * 1.避免手动初始化ApplicationContext
 * 2.避免手动获取bean实例
 * 3.避免手动数据库清理
 * 
 * @author ruanwei
 *
 */
@ActiveProfiles("development")
@TestPropertySource("classpath:family.properties")
//@SpringJUnitConfig(locations = "classpath:spring/applicationContext.xml")
@SpringJUnitConfig(AppConfig.class)
public class ContextTest {
	private static Log log = LogFactory.getLog(ContextTest.class);

	@Autowired
	private ApplicationContext context;

	@BeforeAll
	static void beforeAll() {
		log.info("beforeAll()");

		MockEnvironment env = new MockEnvironment();
		env.setActiveProfiles("development");
		env.setDefaultProfiles("production");
		env.setProperty("foo", "bar");

		MockPropertySource ps = new MockPropertySource();
		ps.setProperty("foo1", "bar1");
	}

	@BeforeEach
	void beforeEach() {
		log.info("beforeEach()");
	}

	@Disabled
	@Test
	void testEnvWithProfile() {
		assertNotNull(context, "context is null++++++++++++++++++++++++++++");
		log.info("1======================================================================================");

		Environment env = context.getEnvironment();
		log.info("profiles==========" + env.getActiveProfiles() + " " + env.getDefaultProfiles());

		if (env instanceof ConfigurableEnvironment) {
			ConfigurableEnvironment configEnv = (StandardEnvironment) env;
			// -Dspring.profiles.active="production"
			// -Dspring.profiles.default="development"
			configEnv.setActiveProfiles("development");
			configEnv.setDefaultProfiles("production");

			if (context instanceof ConfigurableApplicationContext) {
				ConfigurableApplicationContext ctx = (ConfigurableApplicationContext) context;
				// ctx.refresh();
			}
		}

		House house = context.getBean("house", House.class);
		log.info("house==========" + house);
	}

	@Test
	void testEnvWithPropertySource() {
		assertNotNull(context, "context is null++++++++++++++++++++++++++++");
		log.info("2======================================================================================");

		// StandardEnvironment:MapPropertySource(systemProperties)/SystemEnvironmentPropertySource(systemEnvironment)
		Environment env = context.getEnvironment();
		String a = env.getProperty("a", "default_a"); // MapPropertySource(-Da=1)
		String b = env.getProperty("family.familyCount", "2");// ResourcePropertySource(@PeopertySource("family.properties"))
		log.info("property=========a=" + a + " b=" + b);

		if (env instanceof ConfigurableEnvironment) {
			ConfigurableEnvironment configEnv = (ConfigurableEnvironment) env;

			Map<String, Object> map = new HashMap<String, Object>();
			map.put("key", 8);
			PropertySource<Map<String, Object>> mapPropertySource = new MapPropertySource("mapPropertySource", map);

			// EnumerablePropertySource:CommandLinePropertySource/MapPropertySource/ServletConfigPropertySource/ServletContextPropertySource
			// CommandLinePropertySource:SimpleCommandLinePropertySource/JOptCommandLinePropertySource
			// MapPropertySource:PropertiesPropertySource/SystemEnvironmentPropertySource
			// PropertiesPropertySource:ResourcePropertySource("classpath:*.properties")/MockPropertySource
			MutablePropertySources propertySources = configEnv.getPropertySources();
			propertySources.addLast(mapPropertySource);
			log.info("PropertySources==========" + propertySources);
		}

	}

	@Test
	void testMessageSource() {
		assertNotNull(context, "context is null++++++++++++++++++++++++++++");
		log.info("3======================================================================================");

		MessageSource messageSource = (MessageSource) context;
		log.info("messageSource==========" + messageSource);
		String msg = messageSource.getMessage("my.messageSource", new Object[] { "ruanwei" },
				"This is my message source.", Locale.US);
		log.info("message==========" + msg);
	}

	@Test
	void testResourceLoader() {
		assertNotNull(context, "context is null++++++++++++++++++++++++++++");
		log.info("4======================================================================================");

		ResourceLoader resourceLoader = (ResourceLoader) context;
		log.info("resourceLoader==========" + resourceLoader);
		Resource resource = resourceLoader.getResource("classpath:spring/applicationContext.xml");
		log.info("resource==========" + resource);
	}

	@Test
	void testApplicationEventPublisher() {
		assertNotNull(context, "context is null++++++++++++++++++++++++++++");
		log.info("5======================================================================================");

		ApplicationEventPublisher applicationEventPublisher = (ApplicationEventPublisher) context;
		log.info("applicationEventPublisher==========" + applicationEventPublisher);
		applicationEventPublisher
				.publishEvent(new MyApplicationEvent(this, "custom ApplicationEvent from SpringApplication"));
		applicationEventPublisher.publishEvent(new String("PayloadApplicationEvent<String> from SpringApplication"));
	}

	@Test
	void testIoC() {
		assertNotNull(context, "context is null++++++++++++++++++++++++++++");
		log.info("6======================================================================================");

		Family family = context.getBean("family", Family.class);
		Family familyx = context.getBean("familyx", Family.class);
		MyFamilyFactoryBean myFamilyFactoryBean = (MyFamilyFactoryBean) context.getBean("&familyx");
		log.info(family);
		log.info(familyx);
		log.info(myFamilyFactoryBean);
	}

	@Test
	void testApplicationEvent() {
		assertNotNull(context, "context is null++++++++++++++++++++++++++++");
		log.info("7======================================================================================");

		log.info("context==========" + context);
		if (context instanceof AbstractApplicationContext) {
			AbstractApplicationContext absContext = (AbstractApplicationContext) context;
			log.info("7.1======================================================================================");
			absContext.start();

			log.info("7.2======================================================================================");
			absContext.stop();

			log.info("7.3======================================================================================");
			// 即ClassPathXmlApplicationContext和FileSystemXmlApplicationContext
			if (context instanceof AbstractRefreshableApplicationContext) {
				absContext.refresh();
			}

			log.info("7.4======================================================================================");
			// absContext.close();
		}
	}

	@AfterEach
	void afterEach() {
		log.info("afterEach()");
	}

	@AfterAll
	static void afterAll() {
		log.info("afterAll()");
	}
}
