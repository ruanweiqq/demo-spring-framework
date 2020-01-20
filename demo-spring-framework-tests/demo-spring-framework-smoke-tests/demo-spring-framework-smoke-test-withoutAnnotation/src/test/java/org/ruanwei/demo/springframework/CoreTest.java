package org.ruanwei.demo.springframework;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Locale;

import javax.validation.ConstraintViolationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.ruanwei.demo.springframework.core.aop.AOP;
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
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
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
 * 注意：
 * 1.@ActiveProfiles("p")不生效，只能指定-Dspring.profiles.active="development" -Dspring.profiles.default="development" -Da=1.
 * 2.beforeAll()在ApplicationContext初始化前执行，因此无法访问容器及其信息.
 * 3.尽管被测试代码没有启用注解，但测试代码默认启用了，因此可以用@Autowired注入，或者通过ApplicationContextAware，或者自己实例化ApplicationContext.
 * 
 * @author ruanwei
 * 
 */
@ActiveProfiles("development")
@TestPropertySource("classpath:propertySource-${spring.profiles.active:development_def}.properties")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringJUnitConfig(AppConfig.class)
//@SpringJUnitConfig(locations = {"classpath:spring/applicationContext.xml"})
public class CoreTest {
	private static Log log = LogFactory.getLog(CoreTest.class);

	@Autowired
	private ApplicationContext context;

	@BeforeAll
	static void beforeAll() {
		log.info("beforeAll()");

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
		assertNotNull(context, "context should be not null");
	}

	@Order(1)
	@Test
	void testEnvWithProfile() {
		log.info("1======================================================================================");

		Environment environment = context.getEnvironment();
		String[] activeProfiles = environment.getActiveProfiles();
		String[] defaultProfiles = environment.getDefaultProfiles();

		assertTrue(activeProfiles.length == 1, "active profile should be at least 1");
		assertTrue(defaultProfiles.length == 1, "default profile should be at least 1");

		assertTrue("development".contentEquals(activeProfiles[0]), "active profile should be development");
		assertTrue("development".contentEquals(defaultProfiles[0]), "default profile should be development");

		Family family = context.getBean("family", Family.class);
		family.refreshProfile();

		environment = context.getEnvironment();
		activeProfiles = environment.getActiveProfiles();
		defaultProfiles = environment.getDefaultProfiles();

		assertTrue(activeProfiles.length == 1, "active profile should be at least 1");
		assertTrue(defaultProfiles.length == 1, "default profile should be at least 1");

		assertTrue("development".contentEquals(activeProfiles[0]), "active profile should be development");
		assertTrue("production".contentEquals(defaultProfiles[0]), "default profile should be production");

		assertTrue("development".contentEquals(activeProfiles[0]), "active profiles should be development");
		// assertTrue("development".contentEquals(defaultProfiles[0]), "default profiles
		// should be development");

		House house = context.getBean("house", House.class);
		assertTrue("RuanHouse".contentEquals(house.getHouseName()), "houseName should be RuanHouse as overrided");
		assertTrue("developmentHost".contentEquals(house.getHostName()),
				"hostName should be developmentHost as profile");
	}

	@Order(2)
	@Test
	void testEnvWithPropertySource() {
		log.info("2======================================================================================");

		Environment environment = context.getEnvironment();

		Family family = context.getBean("family", Family.class);
		family.refreshPropertySource();

		assertEquals(1, Integer.valueOf(environment.getProperty("a", "18")), "a should be 1");
		// assertEquals(2, Integer.valueOf(environment.getProperty("b", "28")), "b
		// should be 2 ");
		assertEquals(3, Integer.valueOf(environment.getProperty("c", "38")), "c should be 3");
		assertEquals(4, Integer.valueOf(environment.getProperty("d", "48")), "d should be 4");
	}

	@Order(3)
	@Test
	void testIoC() {
		log.info("3======================================================================================");

		Family family = context.getBean("family", Family.class);
		assertTrue("Ruan".contentEquals(family.getFamilyName()), "familyName should be Ruan");

		Family family1 = context.getBean("family1", Family.class);
		assertTrue("Ruan1".contentEquals(family1.getFamilyName()), "familyName should be Ruan1");

		Family family2 = context.getBean("family2", Family.class);
		assertTrue("Ruan2".contentEquals(family2.getFamilyName()), "familyName should be Ruan2");

		Family family3 = context.getBean("family3", Family.class);
		assertTrue("Ruan3".contentEquals(family3.getFamilyName()), "familyName should be Ruan3");

		Family familyx = context.getBean("familyx", Family.class);
		assertTrue("RuanX".contentEquals(familyx.getFamilyName()), "familyName should be RuanX");

		Object familyx2 = context.getBean("&familyx");
		assertTrue(familyx2 instanceof MyFamilyFactoryBean, "familyx2 should be MyFamilyFactoryBean");

		// 3.Method injection: Lookup method injection
		if (context instanceof ClassPathXmlApplicationContext) {
			People bueaty = family.createBueaty();
			assertNotNull(bueaty, "bueaty should not be null");
			assertTrue("ruan_guest".contentEquals(bueaty.getName()), "guest name should be Guest_ruanwei");

			// 3.Method injection: Arbitrary method replacement
			int sum = family.calc(3, 5);
			assertEquals(18, sum, "sum should be 18");
		}
	}

	@Order(4)
	@Test
	void testMessageSource() {
		log.info("4======================================================================================");

		assertTrue(context instanceof MessageSource, "context should be MessageSource");

		String helloWorld1 = context.getMessage("messageSource.helloWorld", new Object[] { "ruanwei" },
				"This is a message.", Locale.US);
		String helloWorld2 = context.getMessage("messageSource.helloWorld", new Object[] { "ruanwei" },
				"This is a message.", Locale.CHINA);

		assertTrue(helloWorld1.contains("English"), "helloWorld should be English");
		assertTrue(helloWorld2.contains("Chinese"), "helloWorld should be Chinese");
	}

	@Order(5)
	@Test
	void testResourceLoader() {
		log.info("5======================================================================================");

		assertTrue(context instanceof ResourceLoader, "context should be ResourceLoader");

		Resource resource = context.getResource("classpath:spring/applicationContext.xml");
		assertTrue("applicationContext.xml".contentEquals(resource.getFilename()),
				"resource filename should be applicationContext.xml");
	}

	@Order(6)
	@Test
	void testApplicationEvent() {
		log.info("6======================================================================================");

		assertTrue(context instanceof ApplicationEventPublisher, "context should be ApplicationEventPublisher");

		Family family = context.getBean("family", Family.class);
		family.helloWorld();

		assertTrue(People.EVENT_COUNT > 0, "EVENT_COUNT should be greater than 0");
		assertTrue(People.PAYLOAD_EVENT_COUNT > 0, "PAYLOAD_EVENT_COUNT should be greater than 0");
		assertTrue(People.CONTEXT_EVENT_COUNT > 0, "CONTEXT_EVENT_COUNT should be greater than 0");
	}

	@Order(7)
	@Test
	void testValidation() {
		log.info("7======================================================================================");

		Family family = context.getBean("family", Family.class);

		assertDoesNotThrow(() -> {
			family.sayHello("12345");
		}, "family.sayHello should not throw exception");

		String result = assertDoesNotThrow(() -> {
			return family.sayHello("123");
		});
		assertTrue("Hello,123".contentEquals(result), "result message should be Hello,123");

		assertThrows(ConstraintViolationException.class, () -> {
			family.sayHello("1");
		}, "family.sayHello should throw ConstraintViolationException");
	}

	@Order(8)
	@Test
	void testAop() {
		log.info("8======================================================================================");

		AOP aop = (AOP) context.getBean("aop");
		aop.sayHello("whatever");

		assertTrue("whatever".contentEquals(Recorder.get("before_message")), "message should be whatever");
		assertTrue("whatever".contentEquals(Recorder.get("after_message")), "message should be whatever");
		assertTrue("whatever".contentEquals(Recorder.get("afterReturning_message")), "message should be whatever");
		// assertTrue("whatever".contentEquals(Recorder.get("afterThrowing_message")),
		// "message should be whatever");
		assertTrue("whatever".contentEquals(Recorder.get("around_message")), "message should be whatever");
		assertTrue("Hello,whatever".contentEquals(Recorder.get("ret_message")), "message should be whatever");
	}

	@Order(9)
	@Test
	void testIntroduction() {
		log.info("9======================================================================================");

		Good good = (Good) context.getBean("good");
		Happy happy = (Happy) context.getBean("good");

		assertTrue(good instanceof Good, "good should be Good");
		assertTrue(good instanceof Happy, "good should be Happy");
		assertTrue(happy instanceof Good, "happy should be Good");
		assertTrue(happy instanceof Happy, "happy should be Happy");
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
