package org.ruanwei.demo.springframework;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.ruanwei.demo.springframework.dataAccess.jdbc.JdbcDao;
import org.ruanwei.demo.springframework.dataAccess.jdbc.JdbcExampleDao;
import org.ruanwei.demo.springframework.dataAccess.jdbc.entity.UserJdbcEntity;
import org.ruanwei.demo.springframework.dataAccess.orm.hibernate.HibernateDao;
import org.ruanwei.demo.springframework.dataAccess.orm.hibernate.entity.UserHibernateEntity;
import org.ruanwei.demo.springframework.dataAccess.orm.jpa.JpaDao;
import org.ruanwei.demo.springframework.dataAccess.orm.jpa.entity.UserJpaEntity;
import org.ruanwei.demo.springframework.dataAccess.orm.mybatis.MyBatisMapper;
import org.ruanwei.demo.springframework.dataAccess.orm.mybatis.entity.UserMyBatisEntity;
import org.ruanwei.demo.springframework.dataAccess.springdata.User;
import org.ruanwei.demo.springframework.dataAccess.springdata.jdbc.UserJdbcRepository;
import org.ruanwei.demo.springframework.dataAccess.springdata.jpa.UserJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.util.concurrent.ListenableFuture;

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
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringJUnitConfig(locations = "classpath:spring/applicationContext.xml")
//@SpringJUnitConfig(AppConfig.class)
public class DataAccessTest {
	private static Log log = LogFactory.getLog(DataAccessTest.class);

	// create
	private static final UserJdbcEntity beanForCreate;
	private static final Map<String, Object> mapForCreate;
	// batch create
	private static final UserJdbcEntity[] beanArrayForBatchCreate;
	private static final Collection<UserJdbcEntity> beanCollForBatchCreate;
	private static final Map<String, Object>[] mapArrayForBatchCreate;
	private static final List<Object[]> objArrayForBatchCreate;
	// update or delete
	private static final UserJdbcEntity beanForUpdateOrDelete;
	private static final Map<String, Object> mapForUpdateOrDelete;
	// batch update or delete
	private static final UserJdbcEntity[] beanArrayForBatchUpdateOrDelete;
	private static final Collection<UserJdbcEntity> beanCollForBatchUpdateOrDelete;
	private static final Map<String, Object>[] mapArrayForBatchUpdateOrDelete;
	private static final List<Object[]> objArrayForBatchUpdateOrDelete;
	// delete
	private static final UserJdbcEntity beanForTransactionDelete1;
	private static final UserJdbcEntity beanForTransactionDelete2;

	// Spring JPA entity
	private static final UserJpaEntity jpaEntityForCreate;
	private static final UserJpaEntity jpaEntityForUpdateOrDelete;
	private static final UserJpaEntity jpaEntityForTransactionDelete1;
	private static final UserJpaEntity jpaEntityForTransactionDelete2;

	// Spring Hibernate entity
	private static final UserHibernateEntity hibernateEntityForCreate;
	private static final UserHibernateEntity hibernateEntityForUpdateOrDelete;
	private static final UserHibernateEntity hibernateEntityForTransactionDelete1;
	private static final UserHibernateEntity hibernateEntityForTransactionDelete2;

	// Spring MyBatis entity
	private static final UserMyBatisEntity myBatisEntityForCreate;
	private static final UserMyBatisEntity myBatisEntityForUpdateOrDelete;
	private static final UserMyBatisEntity myBatisEntityForTransactionDelete1;
	private static final UserMyBatisEntity myBatisEntityForTransactionDelete2;

	// Spring Data JDBC entity
	private static final User jdbcEntityForCreate = null;
	private static final User jdbcEntityForUpdate = null;
	private static final User jdbcEntityForDelete = null;

	private static final int gt0 = 0;
	private static final int gt1 = 1;
	private static final int eq0 = 0;
	private static final int eq1 = 1;
	// TODO:这里eq0不能放在eq1前面，坑！
	private static final List<Integer> ids = Arrays.asList(eq1,eq0);

	static {
		// create
		beanForCreate = new UserJdbcEntity("ruanwei_tmp", 36, Date.valueOf("1983-07-06"));
		mapForCreate = new HashMap<>();
		mapForCreate.put("name", beanForCreate.getName());
		mapForCreate.put("age", beanForCreate.getAge());
		mapForCreate.put("birthday", beanForCreate.getBirthday());
		// batch create
		beanArrayForBatchCreate = new UserJdbcEntity[] { beanForCreate, beanForCreate, beanForCreate };
		beanCollForBatchCreate = Arrays.asList(beanArrayForBatchCreate);
		mapArrayForBatchCreate = new HashMap[3];
		mapArrayForBatchCreate[0] = mapForCreate;
		mapArrayForBatchCreate[1] = mapForCreate;
		mapArrayForBatchCreate[2] = mapForCreate;
		objArrayForBatchCreate = new ArrayList<Object[]>();
		objArrayForBatchCreate
				.add(new Object[] { beanForCreate.getName(), beanForCreate.getAge(), beanForCreate.getBirthday() });
		objArrayForBatchCreate
				.add(new Object[] { beanForCreate.getName(), beanForCreate.getAge(), beanForCreate.getBirthday() });
		objArrayForBatchCreate
				.add(new Object[] { beanForCreate.getName(), beanForCreate.getAge(), beanForCreate.getBirthday() });

		// update or delete
		beanForUpdateOrDelete = new UserJdbcEntity("ruanwei_tmp", 18, Date.valueOf("1983-07-06"));
		mapForUpdateOrDelete = new HashMap<>();
		mapForUpdateOrDelete.put("name", beanForUpdateOrDelete.getName());
		mapForUpdateOrDelete.put("age", beanForUpdateOrDelete.getAge());
		mapForUpdateOrDelete.put("birthday", beanForUpdateOrDelete.getBirthday());
		// batch update or delete
		beanArrayForBatchUpdateOrDelete = new UserJdbcEntity[] { beanForUpdateOrDelete, beanForUpdateOrDelete,
				beanForUpdateOrDelete };
		beanCollForBatchUpdateOrDelete = Arrays.asList(beanArrayForBatchUpdateOrDelete);
		mapArrayForBatchUpdateOrDelete = new HashMap[3];
		mapArrayForBatchUpdateOrDelete[0] = mapForUpdateOrDelete;
		mapArrayForBatchUpdateOrDelete[1] = mapForUpdateOrDelete;
		mapArrayForBatchUpdateOrDelete[2] = mapForUpdateOrDelete;
		objArrayForBatchUpdateOrDelete = new ArrayList<Object[]>();
		objArrayForBatchUpdateOrDelete.add(new Object[] { beanForUpdateOrDelete.getName(),
				beanForUpdateOrDelete.getAge(), beanForUpdateOrDelete.getBirthday() });
		objArrayForBatchUpdateOrDelete.add(new Object[] { beanForUpdateOrDelete.getName(),
				beanForUpdateOrDelete.getAge(), beanForUpdateOrDelete.getBirthday() });
		objArrayForBatchUpdateOrDelete.add(new Object[] { beanForUpdateOrDelete.getName(),
				beanForUpdateOrDelete.getAge(), beanForUpdateOrDelete.getBirthday() });

		beanForTransactionDelete1 = new UserJdbcEntity("ruanwei_tmp", 1, Date.valueOf("1983-07-06"));
		beanForTransactionDelete2 = new UserJdbcEntity("ruanwei_tmp", 2, Date.valueOf("1983-07-06"));
	}

	static {
		jpaEntityForCreate = new UserJpaEntity("ruanwei_tmp", 36, Date.valueOf("1983-07-06"));
		jpaEntityForUpdateOrDelete = new UserJpaEntity("ruanwei_tmp", 18, Date.valueOf("1983-07-06"));
		jpaEntityForTransactionDelete1 = new UserJpaEntity("ruanwei_tmp", 1, Date.valueOf("1983-07-06"));
		jpaEntityForTransactionDelete2 = new UserJpaEntity("ruanwei_tmp", 2, Date.valueOf("1983-07-06"));

		hibernateEntityForCreate = new UserHibernateEntity("ruanwei_tmp", 36, Date.valueOf("1983-07-06"));
		hibernateEntityForUpdateOrDelete = new UserHibernateEntity("ruanwei_tmp", 18, Date.valueOf("1983-07-06"));
		hibernateEntityForTransactionDelete1 = new UserHibernateEntity("ruanwei_tmp", 1, Date.valueOf("1983-07-06"));
		hibernateEntityForTransactionDelete2 = new UserHibernateEntity("ruanwei_tmp", 2, Date.valueOf("1983-07-06"));

		myBatisEntityForCreate = new UserMyBatisEntity("ruanwei_tmp", 36, Date.valueOf("1983-07-06"));
		myBatisEntityForUpdateOrDelete = new UserMyBatisEntity("ruanwei_tmp", 18, Date.valueOf("1983-07-06"));
		myBatisEntityForTransactionDelete1 = new UserMyBatisEntity("ruanwei_tmp", 1, Date.valueOf("1983-07-06"));
		myBatisEntityForTransactionDelete2 = new UserMyBatisEntity("ruanwei_tmp", 2, Date.valueOf("1983-07-06"));
	}

	@Autowired
	private JdbcDao<UserJdbcEntity, Integer> userJdbcDao;

	@Autowired
	private JdbcExampleDao<UserJdbcEntity, Integer> userJdbcExampleDao;

	@Autowired
	private JpaDao<UserJpaEntity, Integer> userJpaDao;

	@Autowired
	private HibernateDao<UserHibernateEntity, Integer> userHibernateDao;

	@Autowired
	private MyBatisMapper<UserMyBatisEntity, Integer> userMyBatisMapper;

	// @Autowired
	private UserJdbcRepository userJdbcRepository;

	// @Autowired
	private UserJpaRepository userJpaRepository;

	@BeforeAll
	static void beforeAll() {
		log.info("beforeAll()");
	}

	@BeforeEach
	void beforeEach() {
		log.info("beforeEach()==============================");
		assertNotNull(userJdbcDao, "userJdbcDao should not be null");
		assertNotNull(userJdbcExampleDao, "userJdbcDao2 should not be null");
		assertNotNull(userJpaDao, "userJpaDao should not be null");
		assertNotNull(userHibernateDao, "userHibernateDao should not be null");
		assertNotNull(userMyBatisMapper, "userMyBatisMapper should not be null");
		// assertNotNull(userJdbcRepository, "userJdbcRepository should not be null");
		// assertNotNull(userJpaRepository, "userJpaRepository should not be null");

		userJdbcDao.deleteAllByGtId(gt1);
		long count = userJdbcDao.count();
		assertEquals(1, count, "count should be 1");

		Optional<UserJdbcEntity> userOpt = userJdbcDao.findById(eq1);
		assertTrue(userOpt.isPresent(), "user should be present");
		UserJdbcEntity user = userOpt.orElse(null);
		assertNotNull(user, "user should not be null");
		assertEquals(1, user.getId(), "user id should be 1983-07-06");
		assertEquals("ruanwei", user.getName(), "user name should be ruanwei");
		assertEquals(36, user.getAge(), "user age should be 36");

		Map<String, ?> userMap = userJdbcDao.findMapById(eq1);
		assertEquals(1, userMap.get("id"), "user id should be 1");
		assertEquals("ruanwei", userMap.get("name"), "user name should be ruanwei");
		assertEquals(36, userMap.get("age"), "user age should be 36");
	}

	// @Disabled
	@Order(1)
	@Test
	void testSpringJdbcCRUD() {
		log.info("1======================================================================================");

		// 1.创建
		userJdbcDao.save(beanForCreate);
		userJdbcDao.saveWithKey(beanForCreate);

		userJdbcDao.save(mapForCreate);
		userJdbcDao.saveWithKey(mapForCreate);

		userJdbcExampleDao.save(beanForCreate.getName(), beanForCreate.getAge(), beanForCreate.getBirthday());
		userJdbcExampleDao.saveWithKey(beanForCreate.getName(), beanForCreate.getAge(), beanForCreate.getBirthday());

		List<UserJdbcEntity> allUsers = userJdbcDao.findAll();
		List<UserJdbcEntity> users = userJdbcDao.findAllByGtId(gt1);
		List<UserJdbcEntity> users2 = userJdbcDao.findAllById(ids);

		assertTrue(allUsers.size() > 2, "size of all users should be > 2");
		assertTrue(users.size() > 1, "size of users which id >1 should be > 1");
		assertEquals(1, users2.size(), "size of users which id in 0,1 should be 1");
		users.forEach(u -> {
			assertTrue(u.getId() > 1, "user id should be > 1");
			assertEquals("ruanwei_tmp", u.getName(), "user name should be ruanwei_tmp");
			assertEquals(36, u.getAge(), "user age should be 36");
		});

		List<Map<String, Object>> allMapUsers = userJdbcDao.findAllMap();
		List<Map<String, Object>> mapUsers = userJdbcDao.findAllMapByGtId(gt1);
		List<Map<String, Object>> mapUsers2 = userJdbcDao.findAllMapById(ids);

		assertTrue(allMapUsers.size() > 2, "size of all users should be > 2");
		assertTrue(mapUsers.size() > 1, "size of users which id >1 should be > 1");
		assertEquals(1, mapUsers2.size(), "size of users which id in 0,1 should be 1");
		mapUsers.forEach(u -> {
			assertTrue((Integer) u.get("id") > 1, "user id should be > 1");
			assertEquals("ruanwei_tmp", (String) u.get("name"), "user name should be ruanwei_tmp");
			assertEquals(36, (Integer) u.get("age"), "user age should be 36");
		});

		// 2.更新age
		userJdbcDao.updateAge(beanForUpdateOrDelete);
		userJdbcDao.updateAge(mapForUpdateOrDelete);

		users = userJdbcDao.findAllByGtId(gt1);
		users.forEach(u -> {
			assertTrue(u.getId() > 1, "user id should be > 1");
			assertEquals("ruanwei_tmp", u.getName(), "user name should be ruanwei_tmp");
			assertEquals(18, u.getAge(), "user age should be 36");
		});

		// 3.删除
		userJdbcDao.delete(beanForUpdateOrDelete);
		userJdbcDao.delete(mapForUpdateOrDelete);
		userJdbcDao.delete(beanForTransactionDelete1);
		userJdbcDao.delete(beanForTransactionDelete2);

		userJdbcExampleDao.delete(beanForUpdateOrDelete.getName(), beanForUpdateOrDelete.getAge(),
				beanForUpdateOrDelete.getBirthday());
		userJdbcExampleDao.delete(beanForTransactionDelete1.getName(), beanForTransactionDelete1.getAge(),
				beanForTransactionDelete1.getBirthday());
		userJdbcExampleDao.delete(beanForTransactionDelete2.getName(), beanForTransactionDelete2.getAge(),
				beanForTransactionDelete2.getBirthday());

		boolean exist = userJdbcDao.existsById(eq1);
		assertTrue(exist, "user which id = 1 should exist");

		Optional<UserJdbcEntity> userOpt = userJdbcDao.findById(eq1);
		assertTrue(userOpt.isPresent(), "user should be present");
		UserJdbcEntity user = userOpt.orElse(null);
		assertNotNull(user, "user should not be null");
		assertEquals(1, user.getId(), "user id should be 1983-07-06");
		assertEquals("ruanwei", user.getName(), "user name should be ruanwei");
		assertEquals(36, user.getAge(), "user age should be 36");
	}

	// @Disabled
	@Order(2)
	@Test
	void testSpringJdbcBatchCRUD() {
		log.info("2======================================================================================");

		// 1.批量创建
		userJdbcDao.batchSave(beanArrayForBatchCreate);
		userJdbcDao.batchSave(beanCollForBatchCreate);
		userJdbcDao.batchSave(mapArrayForBatchCreate);
		userJdbcExampleDao.batchSave(objArrayForBatchCreate);

		List<UserJdbcEntity> allUsers = userJdbcDao.findAll();
		List<UserJdbcEntity> users = userJdbcDao.findAllByGtId(gt1);
		List<UserJdbcEntity> users2 = userJdbcDao.findAllById(ids);
		assertTrue(allUsers.size() > 2, "size of all users should be > 2");
		assertTrue(users.size() > 1, "size of users which id >1 should be > 1");
		assertEquals(1, users2.size(), "size of users which id in 0,1 should be 1");
		users.forEach(u -> {
			assertTrue(u.getId() > 1, "user id should be > 1");
			assertEquals("ruanwei_tmp", u.getName(), "user name should be ruanwei_tmp");
			assertEquals(36, u.getAge(), "user age should be 36");
		});

		List<Map<String, Object>> allMapUsers = userJdbcDao.findAllMap();
		List<Map<String, Object>> mapUsers = userJdbcDao.findAllMapByGtId(gt1);
		List<Map<String, Object>> mapUsers2 = userJdbcDao.findAllMapById(ids);
		assertTrue(allMapUsers.size() > 2, "size of all users should be > 2");
		assertTrue(mapUsers.size() > 1, "size of users which id >1 should be > 1");
		assertEquals(1, mapUsers2.size(), "size of users which id in 1,2,3 should be 1");
		mapUsers.forEach(u -> {
			assertTrue((Integer) u.get("id") > 1, "user id should be > 1");
			assertEquals("ruanwei_tmp", (String) u.get("name"), "user name should be ruanwei_tmp");
			assertEquals(36, (Integer) u.get("age"), "user age should be 36");
		});

		// 2.批量更新age
		userJdbcDao.batchUpdateAge(beanArrayForBatchUpdateOrDelete);
		userJdbcDao.batchUpdateAge(beanCollForBatchUpdateOrDelete);
		userJdbcDao.batchUpdateAge(mapArrayForBatchUpdateOrDelete);
		// TODO:这个方法调用的SQL有问题
		// userJdbcExampleDao.batchUpdateAge(objArrayForBatchUpdateOrDelete);

		users = userJdbcDao.findAllByGtId(gt1);
		users.forEach(u -> {
			assertTrue(u.getId() > 1, "user id should be > 1");
			assertEquals("ruanwei_tmp", u.getName(), "user name should be ruanwei_tmp");
			assertEquals(18, u.getAge(), "user age should be 36");
		});

		// 3.批量删除
		userJdbcDao.batchDelete(beanArrayForBatchUpdateOrDelete);
		userJdbcDao.batchDelete(beanCollForBatchUpdateOrDelete);
		userJdbcDao.batchDelete(mapArrayForBatchUpdateOrDelete);
		userJdbcExampleDao.batchDelete(objArrayForBatchUpdateOrDelete);

		boolean exist = userJdbcDao.existsById(eq1);
		assertTrue(exist, "user which id = 1 should exist");

		Optional<UserJdbcEntity> userOpt = userJdbcDao.findById(eq1);
		assertTrue(userOpt.isPresent(), "user should be present");
		UserJdbcEntity user = userOpt.orElse(null);
		assertNotNull(user, "user should not be null");
		assertEquals(1, user.getId(), "user id should be 1983-07-06");
		assertEquals("ruanwei", user.getName(), "user name should be ruanwei");
		assertEquals(36, user.getAge(), "user age should be 36");
	}

	// @Disabled
	@Order(3)
	@Test
	void testSpringJdbcWithTransaction() {
		log.info("3======================================================================================");
		try {
			userJdbcDao.transactionalMethod1(new UserJdbcEntity("ruanwei_tmp", 1, Date.valueOf("1983-07-06")),
					new UserJdbcEntity("ruanwei_tmp", 2, Date.valueOf("1983-07-06")));
		} catch (ArithmeticException e) {
			log.error("transaction rolled back for ArithmeticException", e);
		} catch (Exception e) {
			log.error("transaction rolled back for Exception", e);
		} finally {
			List<UserJdbcEntity> allUsers = userJdbcDao.findAll();
			assertEquals(2, allUsers.size(), "size of all users should be 2");

			List<UserJdbcEntity> users = userJdbcDao.findAllByGtId(gt1);
			users.forEach(u -> {
				assertEquals(2, u.getAge(), "user age should be 2.");
				userJdbcDao.delete(u);
			});
		}
	}

	// @Disabled
	@Order(4)
	@Test
	void testSpringJpaCRUD() {
		log.info("4======================================================================================");

		// 1.创建
		userJpaDao.save(jpaEntityForCreate);

		List<UserJpaEntity> allUsers = userJpaDao.findAll();
		List<UserJpaEntity> users = userJpaDao.findAllByGtId(gt1);
		List<UserJpaEntity> users2 = userJpaDao.findAllById(ids);

		assertEquals(2, allUsers.size(), "size of all users should be 2");
		assertEquals(1, users.size(), "size of users which id >1 should be 1");
		assertEquals(1, users2.size(), "size of users which id in 0,1 should be 1");
		users.forEach(u -> {
			assertTrue(u.getId() > 1, "user id should be > 1");
			assertEquals("ruanwei_tmp", u.getName(), "user name should be ruanwei_tmp");
			assertEquals(36, u.getAge(), "user age should be 36");
		});

		// 2.更新age
		userJpaDao.updateAge(jpaEntityForUpdateOrDelete);

		users = userJpaDao.findAllByGtId(gt1);
		users.forEach(u -> {
			assertTrue(u.getId() > 1, "user id should be > 1");
			assertEquals("ruanwei_tmp", u.getName(), "user name should be ruanwei_tmp");
			assertEquals(18, u.getAge(), "user age should be 18");
			// 3.删除：不能直接删除transient entity
			userJpaDao.delete(u);
		});

		boolean exist = userJpaDao.existsById(eq1);
		assertTrue(exist, "user which id = 1 should exist");

		Optional<UserJpaEntity> userOpt = userJpaDao.findById(eq1);
		assertTrue(userOpt.isPresent(), "user should be present");
		UserJpaEntity user = userOpt.orElse(null);
		assertNotNull(user, "user should not be null");
		assertEquals(1, user.getId(), "user id should be 1983-07-06");
		assertEquals("ruanwei", user.getName(), "user name should be ruanwei");
		assertEquals(36, user.getAge(), "user age should be 36");
	}

	// @Disabled
	@Order(5)
	@Test
	void testSpringJpaWithTransaction() {
		log.info("5======================================================================================");
		try {
			userJpaDao.transactionalMethod1(new UserJpaEntity("ruanwei_tmp", 1, Date.valueOf("1983-07-06")),
					new UserJpaEntity("ruanwei_tmp", 2, Date.valueOf("1983-07-06")));
		} catch (ArithmeticException e) {
			log.error("transaction rolled back for ArithmeticException", e);
		} catch (Exception e) {
			log.error("transaction rolled back for Exception", e);
		} finally {
			List<UserJpaEntity> allUsers = userJpaDao.findAll();
			assertEquals(2, allUsers.size(), "size of all users should be 2");

			List<UserJpaEntity> users = userJpaDao.findAllByGtId(gt1);
			users.forEach(u -> {
				assertEquals(2, u.getAge(), "user age should be 2.");
				userJpaDao.delete(u);
			});
		}
	}

	// @Disabled
	@Order(6)
	@Test
	void testSpringHibernateCRUD() {
		log.info("6======================================================================================");

		// 1.创建
		userHibernateDao.save(hibernateEntityForCreate);

		List<UserHibernateEntity> allUsers = userHibernateDao.findAll();
		List<UserHibernateEntity> users = userHibernateDao.findAllByGtId(gt1);
		List<UserHibernateEntity> users2 = userHibernateDao.findAllById(ids);

		assertEquals(2, allUsers.size(), "size of all users should be 2");
		assertEquals(1, users.size(), "size of users which id >1 should be 1");
		assertEquals(1, users2.size(), "size of users which id in 0,1 should be 1");
		users.forEach(u -> {
			assertTrue(u.getId() > 1, "user id should be > 1");
			assertEquals("ruanwei_tmp", u.getName(), "user name should be ruanwei_tmp");
			assertEquals(36, u.getAge(), "user age should be 36");
		});

		// 2.更新age
		userHibernateDao.updateAge(hibernateEntityForUpdateOrDelete);

		users = userHibernateDao.findAllByGtId(gt1);
		users.forEach(u -> {
			assertTrue(u.getId() > 1, "user id should be > 1");
			assertEquals("ruanwei_tmp", u.getName(), "user name should be ruanwei_tmp");
			assertEquals(18, u.getAge(), "user age should be 18");
			// 3.删除：不能直接删除transient entity
			userHibernateDao.delete(u);
		});

		boolean exist = userHibernateDao.existsById(eq1);
		assertTrue(exist, "user which id = 1 should exist");

		Optional<UserHibernateEntity> userOpt = userHibernateDao.findById(eq1);
		assertTrue(userOpt.isPresent(), "user should be present");
		UserHibernateEntity user = userOpt.orElse(null);
		assertNotNull(user, "user should not be null");
		assertEquals(1, user.getId(), "user id should be 1983-07-06");
		assertEquals("ruanwei", user.getName(), "user name should be ruanwei");
		assertEquals(36, user.getAge(), "user age should be 36");
	}

	// @Disabled
	@Order(7)
	@Test
	void testSpringHibernateWithTransaction() {
		log.info("7======================================================================================");
		try {
			userHibernateDao.transactionalMethod1(new UserHibernateEntity("ruanwei_tmp", 1, Date.valueOf("1983-07-06")),
					new UserHibernateEntity("ruanwei_tmp", 2, Date.valueOf("1983-07-06")));
		} catch (ArithmeticException e) {
			log.error("transaction rolled back for ArithmeticException", e);
		} catch (Exception e) {
			log.error("transaction rolled back for Exception", e);
		} finally {
			List<UserHibernateEntity> allUsers = userHibernateDao.findAllByGtId(gt0);
			assertEquals(2, allUsers.size(), "size of all users should be 2");

			List<UserHibernateEntity> users = userHibernateDao.findAllByGtId(gt1);
			users.forEach(u -> {
				assertEquals(2, u.getAge(), "user age should be 2.");
				userHibernateDao.delete(u);
			});
		}
	}

	// @Disabled
	@Order(8)
	@Test
	void testSpringMyBatisCRUD() {
		log.info("8======================================================================================");

		// 1.创建
		userMyBatisMapper.save(myBatisEntityForCreate);

		List<UserMyBatisEntity> allUsers = userMyBatisMapper.findAll();
		List<UserMyBatisEntity> users1 = userMyBatisMapper.findAllById(ids);
		List<UserMyBatisEntity> users2 = userMyBatisMapper.findAllByGtId(gt1);
		assertEquals(2, allUsers.size(), "size of all users should be 2");
		assertEquals(1, users1.size(), "size of users which id in 1,2,3 should be 1");
		assertEquals(1, users2.size(), "size of users which id > 1 should be 1");
		users2.forEach(u -> assertTrue(u.getId() > 1, "user id should be > 1"));
		users2.forEach(u -> assertEquals("ruanwei_tmp", u.getName(), "user name should be ruanwei_tmp"));
		users2.forEach(u -> assertEquals(36, u.getAge(), "user age should be 36"));

		// 2.更新age
		userMyBatisMapper.updateAge(myBatisEntityForUpdateOrDelete);

		Optional<UserMyBatisEntity> userOpt = userMyBatisMapper.findById(eq1);
		assertTrue(userOpt.isPresent(), "user should be present");
		UserMyBatisEntity user = userOpt.orElse(null);
		assertNotNull(user, "user should not be null");
		assertEquals(1, user.getId(), "user id should be 1983-07-06");
		assertEquals("ruanwei", user.getName(), "user name should be ruanwei");
		assertEquals(36, user.getAge(), "user age should be 36");

		users2 = userMyBatisMapper.findAllByGtId(gt1);
		users2.forEach(u -> assertTrue(u.getId() > 1, "user id should be > 1"));
		users2.forEach(u -> assertEquals("ruanwei_tmp", u.getName(), "user name should be ruanwei_tmp"));
		users2.forEach(u -> assertEquals(18, u.getAge(), "user age should be 18"));

		// 3.删除
		userMyBatisMapper.delete(myBatisEntityForUpdateOrDelete);
		userMyBatisMapper.delete(myBatisEntityForTransactionDelete1);
		userMyBatisMapper.delete(myBatisEntityForTransactionDelete2);
	}

	// 由于接口无法注入依赖，以及无法增加异常逻辑，所以这个用例跑不通
	@Disabled
	@Order(9)
	@Test
	void testSpringMyBatisWithTransaction() {
		log.info("9======================================================================================");
		try {
			userMyBatisMapper.transactionalMethod1(new UserMyBatisEntity("ruanwei_tmp", 1, Date.valueOf("1983-07-06")),
					new UserMyBatisEntity("ruanwei_tmp", 2, Date.valueOf("1983-07-06")));
		} catch (ArithmeticException e) {
			log.error("transaction rolled back for ArithmeticException", e);
		} catch (Exception e) {
			log.error("transaction rolled back for Exception", e);
		} finally {
			List<UserMyBatisEntity> allUsers = userMyBatisMapper.findAll();
			assertEquals(2, allUsers.size(), "size of all users should be 2");

			List<UserMyBatisEntity> users = userMyBatisMapper.findAllByGtId(gt1);
			users.forEach(u -> {
				assertEquals(2, u.getAge(), "user age should be 2.");
				userMyBatisMapper.delete(u);
			});
		}
	}

	@Disabled
	@Order(10)
	@Test
	void testSpringDataJdbcCRUD() {
		log.info("10======================================================================================");
		// 1.创建
		userJdbcRepository.save(jdbcEntityForUpdate);

		User user = userJdbcRepository.findUserById(eq1);
		assertNotNull(user, "user should not be null");
		assertEquals(1, user.getId(), "user id should be 1983-07-06");
		assertEquals("ruanwei", user.getName(), "user name should be ruanwei");
		assertEquals(36, user.getAge(), "user age should be 36");

		Iterable<User> allUsers = userJdbcRepository.findAll();
		allUsers.forEach(u -> assertTrue(u.getId() > 1, "user id should be gt 1"));
		allUsers.forEach(u -> assertEquals("ruanwei_tmp", u.getName(), "user name should be ruanwei_tmp"));
		allUsers.forEach(u -> assertEquals(36, u.getAge(), "user age should be 36"));

		// 2.根据name更新age
		testCreate();
		testUpdate();
		testQueryForSingleRow();
		testQueryForList();
		testQueryAsync();
		// testQueryForPagingAndSorting();
		testDelete();
	}

	@Disabled
	@Order(11)
	@Test
	void testSpringDataJdbcWithTransaction() {
		log.info("11======================================================================================");

		assertNotNull(userJdbcRepository, "userJdbcRepository should npt be null");
		try {
			userJdbcRepository.transactionalMethod1(new User("ruanwei_tmp", 1, Date.valueOf("1983-07-06")));
		} catch (Exception e) {
			log.error("transaction rolled back", e);
		}
	}

	@AfterEach
	void afterEach() {
		log.info("afterEach()");

		Optional<UserJdbcEntity> userOpt = userJdbcDao.findById(eq1);
		assertTrue(userOpt.isPresent(), "user should be present");
		UserJdbcEntity user = userOpt.orElse(null);
		assertNotNull(user, "user should not be null");
		assertEquals(1, user.getId(), "user id should be 1983-07-06");
		assertEquals("ruanwei", user.getName(), "user name should be ruanwei");
		assertEquals(36, user.getAge(), "user age should be 36");

		Map<String, ?> userMap = userJdbcDao.findMapById(eq1);
		assertEquals(1, userMap.get("id"), "user id should be 1");
		assertEquals("ruanwei", userMap.get("name"), "user name should be ruanwei");
		assertEquals(36, userMap.get("age"), "user age should be 36");
	}

	@AfterAll
	static void afterAll() {
		log.info("afterAll()");
	}

	private void testCreate() {
		int count = userJdbcRepository.createUser(beanForCreate.getName(), beanForCreate.getAge(),
				beanForCreate.getBirthday());
		log.info("jdbcRepository.createUser========" + count);

		User user = userJdbcRepository.save(jdbcEntityForCreate);
		log.info("jdbcCrudRepository.save========" + user);
	}

	private void testUpdate() {
		int count = userJdbcRepository.updateUser(beanForUpdateOrDelete.getName(), beanForUpdateOrDelete.getAge());
		log.info("jdbcRepository.updateUser========" + count);
	}

	private void testQueryForSingleRow() {
		String name = userJdbcRepository.findNameById(eq1);
		log.info("jdbcRepository.findNameById========" + name);

		Map<String, Object> columnMap = userJdbcRepository.findNameAndAgeById(eq1);
		columnMap.forEach((k, v) -> log.info("jdbcRepository.findNameAndAgeById====" + k + "=" + v));

		User user = userJdbcRepository.findUserById(eq1);
		log.info("jdbcRepository.findUserById========" + user);

		Optional<User> user2 = userJdbcRepository.findById(eq1);
		log.info("jdbcCrudRepository.findById========" + user2.get());

		long count = userJdbcRepository.count();
		log.info("jdbcCrudRepository.count()========" + count);

		boolean exist = userJdbcRepository.existsById(eq1);
		log.info("jdbcCrudRepository.existsById========" + exist);
	}

	private void testQueryForList() {
		List<String> nameList = userJdbcRepository.findNameListById(gt0);
		nameList.forEach(e -> log.info("jdbcRepository.findNameListById========" + e));

		List<Map<String, Object>> columnMapList = userJdbcRepository.findNameAndAgeListById(gt0);
		columnMapList.forEach(columbMap -> columbMap
				.forEach((k, v) -> log.info("jdbcRepository.findNameAndAgeListById====" + k + "=" + v)));

		List<User> userList = userJdbcRepository.findUserListById(gt0);
		userList.forEach(e -> log.info("jdbcRepository.findUserListById========" + e));

		// Iterable<User> userList2 = userJdbcRepository.findAllById(listParamForQuery);
		// userList2.forEach(e -> log.info("jdbcCrudRepository.findAllById========" +
		// e));

		Iterable<User> userList3 = userJdbcRepository.findAll();
		userList3.forEach(e -> log.info("jdbcCrudRepository.findAll()========" + e));
	}

	private void testQueryAsync() {
		Future<List<User>> userList = userJdbcRepository.findAllUser1();
		CompletableFuture<List<User>> userList2 = userJdbcRepository.findAllUser2();
		ListenableFuture<List<User>> userList3 = userJdbcRepository.findAllUser3();
	}

	private void testQueryForPagingAndSorting() {
		Sort sort = Sort.by(Direction.ASC, "age");
		Pageable pageable = PageRequest.of(0, 5);
		Pageable pageableWithSort = PageRequest.of(0, 5, sort);

		Iterable<User> userList = userJdbcRepository.findAll(sort);
		userList.forEach(e -> log.info("jdbcPagingAndSortingRepository.findAll========" + e));

		// see also java.util.Stream in java 8.
		Page<User> userPage = userJdbcRepository.findAll(pageable);
		userPage.forEach(e -> log.info("jdbcPagingAndSortingRepository.findAll========" + e));

		Slice<User> userSlice = userJdbcRepository.findAll(pageable);
		userSlice.forEach(e -> log.info("jdbcPagingAndSortingRepository.findAll========" + e));

		Page<User> userPage2 = userJdbcRepository.findAll(pageableWithSort);
		userPage2.forEach(e -> log.info("jdbcPagingAndSortingRepository.findAll========" + e));
	}

	private void testDelete() {
		int count = userJdbcRepository.deleteUser(2);
		log.info("jdbcRepository.deleteUser========" + count);

		userJdbcRepository.deleteById(3);
		userJdbcRepository.delete(jdbcEntityForDelete);
		// userJdbcRepository.deleteAll(listParamForDelete);
		// userJdbcRepository.deleteAll();
	}

}
