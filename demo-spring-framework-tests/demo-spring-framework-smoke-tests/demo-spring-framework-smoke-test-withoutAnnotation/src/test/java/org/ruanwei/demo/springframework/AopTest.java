package org.ruanwei.demo.springframework;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ruanwei.demo.springframework.core.aop.Good;
import org.ruanwei.demo.springframework.core.aop.Happy;
import org.ruanwei.demo.springframework.core.ioc.Family;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

/**
 * 
 * @SpringJUnitConfig(AppConfig.class) is composed of
 * @ExtendWith(SpringExtension.class) and ContextConfiguration(classes =
 *                                    AppConfig.class).
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
//@SpringJUnitConfig(locations = "classpath:spring/applicationContext.xml")
@SpringJUnitConfig(AppConfig.class)
public class AopTest {
	private static Log log = LogFactory.getLog(AopTest.class);

	@Autowired
	private ApplicationContext context;

	@BeforeAll
	static void beforeAll() {
		log.info("beforeAll()");
	}

	@BeforeEach
	void beforeEach() {
		log.info("beforeEach()");
	}

	// @Disabled
	@Test
	void testAop() {
		assertNotNull(context, "context is null++++++++++++++++++++++++++++");
		log.info("1======================================================================================");

		Family family = context.getBean("family", Family.class);
		family.sayHello("whatever");
	}

	@Test
	void testIntroduction() {
		assertNotNull(context, "context is null++++++++++++++++++++++++++++");
		log.info("2======================================================================================");

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
