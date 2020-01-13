package org.ruanwei.demo.springframework;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Date;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.ruanwei.demo.springframework.dataAccess.User;
import org.ruanwei.demo.springframework.dataAccess.jdbc.UserJdbcDao;
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
// @DirtiesContext
// @Rollback
// @Commit
// @Transactional("txManager")
@ActiveProfiles("development")
//@SpringJUnitConfig(locations = "classpath:spring/dataAccess.xml")
@SpringJUnitConfig(DataAccessConfig.class)
public class DataAccessTest {
	private static Log log = LogFactory.getLog(DataAccessTest.class);

	private static final User beanForCreate;
	private static final Map<String, Object> mapForCreate;

	private static final User beanForUpdate;
	private static final Map<String, Object> mapForUpdate;

	private static final User beanForDelete;
	private static final Map<String, Object> mapForDelete;

	private static final User[] beanArrayForBatchUpdate;
	private static final Collection<User> beanCollForBatchUpdate;
	private static final Map<String, Object>[] mapArrayForBatchUpdate;

	private static final int gt0 = 0;
	private static final int eq1 = 1;

	static {
		// create
		beanForCreate = new User("ruanwei_tmp", 36, Date.valueOf("1983-07-06"));
		mapForCreate = new HashMap<>();
		mapForCreate.put("name", beanForCreate.getName());
		mapForCreate.put("age", beanForCreate.getAge());
		mapForCreate.put("birthday", beanForCreate.getBirthday());

		// update
		beanForUpdate = new User("ruanwei_tmp", 18, Date.valueOf("1983-07-06"));
		mapForUpdate = new HashMap<>();
		mapForUpdate.put("name", beanForUpdate.getName());
		mapForUpdate.put("age", beanForUpdate.getAge());
		mapForUpdate.put("birthday", beanForUpdate.getBirthday());

		// delete
		beanForDelete = new User("ruanwei_tmp", 18, Date.valueOf("1983-07-06"));
		mapForDelete = new HashMap<>();
		mapForDelete.put("name", beanForDelete.getName());
		mapForDelete.put("age", beanForDelete.getAge());
		mapForDelete.put("birthday", beanForDelete.getBirthday());

		// batch update
		beanArrayForBatchUpdate = new User[] { new User("ruanwei_tmp", 18, Date.valueOf("1983-07-06")),
				new User("ruanwei_tmp", 18, Date.valueOf("1983-07-06")) };

		beanCollForBatchUpdate = Arrays.asList(beanArrayForBatchUpdate);

		Map<String, Object> mapForUpdate1 = new HashMap<>();
		mapForUpdate1.put("name", "ruanwei_tmp");
		mapForUpdate1.put("age", 18);
		mapForUpdate1.put("birthday", Date.valueOf("1983-07-06"));

		Map<String, Object> mapForUpdate2 = new HashMap<>();
		mapForUpdate2.put("name", "ruanwei_tmp");
		mapForUpdate2.put("age", 18);
		mapForUpdate2.put("birthday", Date.valueOf("1983-07-06"));

		mapArrayForBatchUpdate = new HashMap[2];
		mapArrayForBatchUpdate[0] = mapForUpdate1;
		mapArrayForBatchUpdate[1] = mapForUpdate2;
	}

	@Autowired
	private UserJdbcDao userJdbcDao;
	
	@BeforeAll
	static void beforeAll() {
		log.info("beforeAll()");
	}

	@BeforeEach
	void beforeEach() {
		log.info("beforeEach()");
		assertNotNull(userJdbcDao, "userJdbcDao should not be null");
	}

	@Order(1)
	@Test
	void testSpringJdbcCRUD() {
		log.info("1======================================================================================");

		// 1.根据name删除
		userJdbcDao.delete(beanForDelete);
		userJdbcDao.delete(mapForDelete);

		List<User> users = userJdbcDao.queryForListWithBeanProperty(gt0);
		assertEquals(1, users.size(), "user size should be 1");

		// 2.创建
		userJdbcDao.create(beanForCreate);
		userJdbcDao.create(mapForCreate);
		userJdbcDao.createWithKey(beanForCreate);
		userJdbcDao.createWithKey(mapForCreate);

		userJdbcDao.createUser1(beanForCreate);
		userJdbcDao.createUser2(beanForCreate);
		userJdbcDao.createUserWithKey1(beanForCreate);

		users = userJdbcDao.queryForListWithBeanProperty(gt0);
		assertEquals(8, users.size(), "user size should be 8");

		// 3.根据name更新age
		userJdbcDao.update(beanForUpdate);
		userJdbcDao.update(mapForUpdate);

		users = userJdbcDao.queryForListWithBeanProperty(eq1);
		List<Map<String, Object>> users2 = userJdbcDao.queryForListWithColumnMap(eq1);
		List<String> names = userJdbcDao.queryForListWithSingleColumn(eq1);

		assertEquals(users.size(), users2.size(), "users size should be equal");
		assertEquals(users.size(), names.size(), "users size should be equal");

		users.forEach(u -> assertEquals(18, u.getAge(), "user age should be 18"));
		users2.forEach(m -> assertEquals(18, m.get("age"), "user age should be 18"));
		names.forEach(n -> assertTrue("ruanwei_tmp".contentEquals(n), "user name should be ruanwei_tmp"));

		// 4.根据name删除
		userJdbcDao.delete(beanForDelete);
		userJdbcDao.delete(mapForDelete);

		users = userJdbcDao.queryForListWithBeanProperty(gt0);
		assertEquals(1, users.size(), "user size should be 1");

		// 根据id查找多行
		users = userJdbcDao.queryForListWithBeanProperty(gt0);
		// 根据id查找一行
		User user = userJdbcDao.queryForSingleRowAsBeanProperty(eq1);
		Map<String, Object> map = userJdbcDao.queryForSingleRowAsColumnMap(eq1);
		String name = userJdbcDao.queryForSingleRowWithSingleColumn(eq1);

		assertEquals(1, users.size(), "user size should be 1");
		assertNotNull(user, "user1 should not be null");

		assertEquals(1, user.getId(), "user id should be 36");
		assertEquals(36, user.getAge(), "user age should be 36");
		assertTrue("ruanwei".contentEquals(user.getName()), "user name should be ruanwei");

		assertTrue(user.getName().contentEquals(name), "user name should be equal");
		assertEquals(user.getAge(), map.get("age"), "user age should be equal");
	}

	@Order(2)
	@Test
	void testSpringJdbcBatchCRUD() {
		log.info("2======================================================================================");

		// 1.根据name批量删除
		userJdbcDao.batchDelete(beanCollForBatchUpdate);
		List<User> users = userJdbcDao.queryForListWithBeanProperty(gt0);
		assertEquals(1, users.size(), "user size should be 1");

		// 2.批量创建
		userJdbcDao.batchCreate(beanArrayForBatchUpdate);
		userJdbcDao.batchCreateUser1(Arrays.asList(beanCollForBatchUpdate.toArray(new User[0])));
		userJdbcDao.batchCreateUser2(beanCollForBatchUpdate);

		users = userJdbcDao.queryForListWithBeanProperty(gt0);
		assertEquals(7, users.size(), "user size should be 7");

		// 3.根据name批量更新age
		userJdbcDao.batchUpdate(mapArrayForBatchUpdate);

		users = userJdbcDao.queryForListWithBeanProperty(eq1);
		List<Map<String, Object>> users2 = userJdbcDao.queryForListWithColumnMap(eq1);
		List<String> names = userJdbcDao.queryForListWithSingleColumn(eq1);

		assertEquals(users.size(), users2.size(), "users size should be equal");
		assertEquals(users.size(), names.size(), "users size should be equal");

		users.forEach(u -> assertEquals(18, u.getAge(), "user age should be 18"));
		users2.forEach(m -> assertEquals(18, m.get("age"), "user age should be 18"));
		names.forEach(n -> assertTrue("ruanwei_tmp".contentEquals(n), "user name should be ruanwei_tmp"));

		// 4.根据name批量删除
		userJdbcDao.batchDelete(beanCollForBatchUpdate);

		users = userJdbcDao.queryForListWithBeanProperty(gt0);
		assertEquals(1, users.size(), "user size should be 1");

		// 根据id查找多行
		users = userJdbcDao.queryForListWithBeanProperty(gt0);
		// 根据id查找一行
		User user = userJdbcDao.queryForSingleRowAsBeanProperty(eq1);
		Map<String, Object> map = userJdbcDao.queryForSingleRowAsColumnMap(eq1);
		String name = userJdbcDao.queryForSingleRowWithSingleColumn(eq1);

		assertEquals(1, users.size(), "user size should be 1");
		assertNotNull(user, "user1 should not be null");

		assertEquals(1, user.getId(), "user id should be 36");
		assertEquals(36, user.getAge(), "user age should be 36");
		assertTrue("ruanwei".contentEquals(user.getName()), "user name should be ruanwei");

		assertTrue(user.getName().contentEquals(name), "user name should be equal");
		assertEquals(user.getAge(), map.get("age"), "user age should be equal");
	}

	@Order(3)
	@Test
	void testSpringJdbcWithTransaction() {
		log.info("3======================================================================================");
		try {
			User paramForCreate1 = new User("ruanwei_tmp", 1, Date.valueOf("1983-07-06"));
			User paramForCreate2 = new User("ruanwei_tmp", 2, Date.valueOf("1983-07-06"));

			userJdbcDao.m1(paramForCreate1, paramForCreate2);
		} catch (Exception e) {
			log.error("transaction rolled back", e);
		} finally {
			List<User> users = userJdbcDao.queryForListWithBeanProperty(gt0);
			assertEquals(2, users.size(), "user size should be 2");
		}
	}

	private void testQueryForSingleRow() {
		userJdbcDao.queryForSingleRowWithSingleColumn(eq1);
		userJdbcDao.queryForSingleRowAsColumnMap(eq1);
		userJdbcDao.queryForSingleRowAsBeanProperty(eq1);
	}

	private void testQueryForList() {
		userJdbcDao.queryForListWithSingleColumn(gt0);
		userJdbcDao.queryForListWithColumnMap(gt0);
		userJdbcDao.queryForListWithBeanProperty(gt0);
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
