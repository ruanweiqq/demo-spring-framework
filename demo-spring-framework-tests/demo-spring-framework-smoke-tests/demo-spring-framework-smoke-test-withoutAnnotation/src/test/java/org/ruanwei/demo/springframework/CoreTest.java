package org.ruanwei.demo.springframework;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.ruanwei.demo.springframework.core.aop.Good;
import org.ruanwei.demo.springframework.core.aop.Happy;
import org.ruanwei.demo.springframework.core.ioc.Family;
import org.ruanwei.demo.springframework.core.ioc.House;
import org.ruanwei.demo.springframework.core.ioc.People;
import org.ruanwei.demo.springframework.core.ioc.extension.MyFamilyFactoryBean;
import org.ruanwei.demo.util.Recorder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.AbstractRefreshableApplicationContext;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

/**
 * 
 * @SpringJUnitConfig(AppConfig.class) = @ExtendWith(SpringExtension.class) + @ContextConfiguration(classes = AppConfig.class).
 * so does @SpringJUnitWebConfig.
 * 
 * 1.避免手动初始化ApplicationContext
 * 2.避免手动获取bean实例
 * 3.避免手动数据库清理
 * 
 * @author ruanwei
 *
 */
@ActiveProfiles("development")
//@TestPropertySource("classpath:family.properties")
@TestPropertySource("classpath:propertySource.properties")
@SpringJUnitConfig(AppConfig.class)
//@SpringJUnitConfig(locations = "classpath:spring/applicationContext.xml")
public class CoreTest {
	private static Log log = LogFactory.getLog(CoreTest.class);

	@Autowired
	private static ApplicationContext context;

	@BeforeAll
	static void beforeAll() {
		log.info("beforeAll()");

		assertNotNull(context, "context should be not null++++++++++++++++++++++++++++");

		if (context == null) {
			context = new AnnotationConfigApplicationContext(AppConfig.class);
			// context = new
			// ClassPathXmlApplicationContext("classpath:spring/applicationContext.xml");
		}

		/*MockEnvironment env = new MockEnvironment();
		env.setActiveProfiles("development");
		env.setDefaultProfiles("production");
		env.setProperty("foo", "bar");
		
		MockPropertySource propertySource = new MockPropertySource();
		propertySource.setProperty("foo1", "bar1");*/
	}

	@BeforeEach
	void beforeEach() {
		log.info("beforeEach()");
	}

	@Test
	void testEnvWithProfile() {
		log.info("1======================================================================================");

		Family family = context.getBean("family", Family.class);
		log.info(family);
		family.helloWorld1();

		assertTrue("development".contentEquals(Recorder.get("activeProfile")), "active profiles should be development");
		assertTrue("production".contentEquals(Recorder.get("defaultProfile")), "default profiles should be production");

		assertTrue("development".contentEquals(Recorder.get("activeProfile2")),
				"active profiles should be development");
		assertTrue("production".contentEquals(Recorder.get("defaultProfile2")),
				"default profiles should be production");

		House house = context.getBean("house", House.class);
		log.info("house==========" + house);
		assertTrue("RuanHouse".contentEquals(Recorder.get("houseName")), "houseName should be RuanHouse as overrided");
		assertTrue("developmentHost".contentEquals(Recorder.get("hostName")),
				"hostName should be developmentHost as profile");
	}

	@Test
	void testEnvWithPropertySource() {
		log.info("2======================================================================================");

		Family family = context.getBean("family", Family.class);
		log.info(family);
		family.helloWorld1();

		assertTrue("1".contentEquals(Recorder.get("a")), "a should be 1");
		assertTrue("2".contentEquals(Recorder.get("b")), "b should be 2 ");
		assertTrue("3".contentEquals(Recorder.get("c")), "c should be 3");
		assertTrue("4".contentEquals(Recorder.get("d")), "d should be 4");
	}

	@Test
	void testIoC() {
		log.info("3======================================================================================");

		Family family = context.getBean("family", Family.class);
		log.info(family);
		assertTrue("Ruan".contentEquals(family.getFamilyName()), "familyName should be Ruan");

		Family family1 = context.getBean("family1", Family.class);
		log.info(family1);
		assertTrue("Ruan1".contentEquals(family1.getFamilyName()), "familyName should be Ruan1");

		Family family2 = context.getBean("family2", Family.class);
		log.info(family2);
		assertTrue("Ruan2".contentEquals(family2.getFamilyName()), "familyName should be Ruan2");

		Family family3 = context.getBean("family3", Family.class);
		log.info(family3);
		assertTrue("Ruan3".contentEquals(family3.getFamilyName()), "familyName should be Ruan3");

		Family familyx = context.getBean("familyx", Family.class);
		log.info(familyx);
		assertTrue("RuanX".contentEquals(familyx.getFamilyName()), "familyName should be RuanX");

		Object familyx2 = context.getBean("&familyx");
		log.info(familyx2);
		assertTrue(familyx2 instanceof MyFamilyFactoryBean, "familyx2 should be MyFamilyFactoryBean");

		// 3.Method injection: Lookup method injection
		People bueaty = family.createBueaty();
		assertNotNull(bueaty, "bueaty should not be null");
		log.info(bueaty);
		assertTrue("Guest_ruanwei".contentEquals(bueaty.getName()), "guest name should be Guest_ruanwei");

		// 3.Method injection: Arbitrary method replacement
		int sum = family.calc(3, 5);
		log.info(sum);
		assertNotEquals(sum, 8, "sum should not be 8");
	}

	@Test
	void testMessageSource() {
		log.info("4======================================================================================");

		assertTrue(context instanceof MessageSource, "context should be MessageSource");

		Family family = context.getBean("family", Family.class);
		log.info(family);
		family.helloWorld2();

		assertTrue("This is a message for ruanwei in English".contentEquals(Recorder.get("helloWorld1")),
				"helloWorld should be English");
		assertTrue("This is a message for ruanwei in Chinese".contentEquals(Recorder.get("helloWorld2")),
				"helloWorld should be Chinese");
	}

	@Test
	void testResourceLoader() {
		log.info("5======================================================================================");

		assertTrue(context instanceof ResourceLoader, "context should be ResourceLoader");

		Family family = context.getBean("family", Family.class);
		log.info(family);
		family.helloWorld2();

		assertTrue("applicationContext.xml".contentEquals(Recorder.get("resource")),
				"resource should be applicationContext.xml");
	}

	@Test
	void testApplicationEventPublisher() {
		log.info("6======================================================================================");

		assertTrue(context instanceof ApplicationEventPublisher, "context should be ApplicationEventPublisher");

		Family family = context.getBean("family", Family.class);
		log.info(family);
		family.helloWorld2();

		assertNotEquals(Integer.valueOf(0), People.EVENT_COUNT, "EVENT_COUNT should not be 0");
	}

	@Disabled
	@Test
	void testApplicationEvent() {
		log.info("7======================================================================================");

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

	@Test
	void testAop() {
		log.info("8======================================================================================");

		Family family = context.getBean("family", Family.class);
		log.info(family);
		family.sayHello("whatever");

		assertTrue("whatever".contentEquals(Recorder.get("before")), "message should be whatever");
		assertTrue("whatever".contentEquals(Recorder.get("after")), "message should be whatever");
		assertTrue("whatever".contentEquals(Recorder.get("afterReturning")), "message should be whatever");
		assertTrue("whatever".contentEquals(Recorder.get("afterThrowing")), "message should be whatever");
		assertTrue("whatever".contentEquals(Recorder.get("around")), "message should be whatever");

		assertTrue("whatever".contentEquals(Recorder.get("ret")), "message should be whatever");
	}

	@Test
	void testIntroduction() {
		log.info("9======================================================================================");

		Good good = (Good) context.getBean("good");
		Happy mixin = (Happy) context.getBean("good");
		log.info(good.good("whatever") + mixin.happy("whatever"));
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
