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
import org.ruanwei.demo.springframework.dataAccess.User;
import org.ruanwei.demo.springframework.dataAccess.jdbc.CrudDao;
import org.ruanwei.demo.springframework.dataAccess.springdata.jdbc.UserJdbcEntity;
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
@SpringJUnitConfig(locations = "classpath:spring/dataAccess.xml")
//@SpringJUnitConfig(DataAccessConfig.class)
public class DataAccessTest {
	private static Log log = LogFactory.getLog(DataAccessTest.class);

	// create
	private static final User beanForCreate;
	private static final Map<String, Object> mapForCreate;
	// batch create
	private static final User[] beanArrayForBatchCreate;
	private static final Collection<User> beanCollForBatchCreate;
	private static final Map<String, Object>[] mapArrayForBatchCreate;
	private static final List<Object[]> objArrayForBatchCreate;

	// update and delete
	private static final User beanForUpdateAndDelete;
	private static final Map<String, Object> mapForUpdateAndDelete;
	// batch update and delete
	private static final User[] beanArrayForBatchUpdateAndDelete;
	private static final Collection<User> beanCollForBatchUpdateAndDelete;
	private static final Map<String, Object>[] mapArrayForBatchUpdateAndDelete;
	private static final List<Object[]> objArrayForBatchUpdateAndDelete;

	// delete for transaction
	private static final User beanForDelete1;
	private static final User beanForDelete2;

	// JPA entity
	private static final UserJdbcEntity entityForCreate;
	private static final UserJdbcEntity entityForUpdate;
	private static final UserJdbcEntity entityForDelete;

	private static final int gt0 = 0;
	private static final int gt1 = 1;
	private static final int eq1 = 1;

	static {
		// create
		beanForCreate = new User("ruanwei_tmp", 36, Date.valueOf("1983-07-06"));
		mapForCreate = new HashMap<>();
		mapForCreate.put("name", beanForCreate.getName());
		mapForCreate.put("age", beanForCreate.getAge());
		mapForCreate.put("birthday", beanForCreate.getBirthday());
		// batch create
		beanArrayForBatchCreate = new User[] { beanForCreate, beanForCreate, beanForCreate };
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

		// update
		beanForUpdateAndDelete = new User("ruanwei_tmp", 18, Date.valueOf("1983-07-06"));
		mapForUpdateAndDelete = new HashMap<>();
		mapForUpdateAndDelete.put("name", beanForUpdateAndDelete.getName());
		mapForUpdateAndDelete.put("age", beanForUpdateAndDelete.getAge());
		mapForUpdateAndDelete.put("birthday", beanForUpdateAndDelete.getBirthday());
		// batch update
		beanArrayForBatchUpdateAndDelete = new User[] { beanForUpdateAndDelete, beanForUpdateAndDelete,
				beanForUpdateAndDelete };
		beanCollForBatchUpdateAndDelete = Arrays.asList(beanArrayForBatchUpdateAndDelete);
		mapArrayForBatchUpdateAndDelete = new HashMap[3];
		mapArrayForBatchUpdateAndDelete[0] = mapForUpdateAndDelete;
		mapArrayForBatchUpdateAndDelete[1] = mapForUpdateAndDelete;
		mapArrayForBatchUpdateAndDelete[2] = mapForUpdateAndDelete;
		objArrayForBatchUpdateAndDelete = new ArrayList<Object[]>();
		objArrayForBatchUpdateAndDelete.add(new Object[] { beanForUpdateAndDelete.getName(),
				beanForUpdateAndDelete.getAge(), beanForUpdateAndDelete.getBirthday() });
		objArrayForBatchUpdateAndDelete.add(new Object[] { beanForUpdateAndDelete.getName(),
				beanForUpdateAndDelete.getAge(), beanForUpdateAndDelete.getBirthday() });
		objArrayForBatchUpdateAndDelete.add(new Object[] { beanForUpdateAndDelete.getName(),
				beanForUpdateAndDelete.getAge(), beanForUpdateAndDelete.getBirthday() });

		// delete for transaction
		beanForDelete1 = new User("ruanwei_tmp", 1, Date.valueOf("1983-07-06"));
		beanForDelete2 = new User("ruanwei_tmp", 2, Date.valueOf("1983-07-06"));
	}

	static {
		entityForCreate = new UserJdbcEntity("ruanwei_tmp", 36, Date.valueOf("1983-07-06"));
		entityForUpdate = new UserJdbcEntity("ruanwei_tmp", 18, Date.valueOf("1983-07-06"));
		entityForDelete = new UserJdbcEntity("ruanwei_tmp", 18, Date.valueOf("1983-07-06"));
	}

	@Autowired
	private CrudDao<User, Integer> userJdbcDao;

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
		// assertNotNull(userJdbcRepository, "userJdbcRepository should not be null");
		// assertNotNull(userJpaRepository, "userJpaRepository should not be null");

		userJdbcDao.delete(beanForUpdateAndDelete);
		userJdbcDao.delete(mapForUpdateAndDelete);
		userJdbcDao.delete(beanForUpdateAndDelete.getName(), beanForUpdateAndDelete.getAge(),
				beanForUpdateAndDelete.getBirthday());

		userJdbcDao.delete(beanForDelete1);
		userJdbcDao.delete(beanForDelete2);

		userJdbcDao.batchDelete(beanArrayForBatchUpdateAndDelete);
		userJdbcDao.batchDelete(beanCollForBatchUpdateAndDelete);
		userJdbcDao.batchDelete(mapArrayForBatchUpdateAndDelete);
		userJdbcDao.batchDelete(objArrayForBatchUpdateAndDelete);

		List<User> allUsers = userJdbcDao.findAll();
		assertEquals(1, allUsers.size(), "size of all users should be 1");

		List<User> users = userJdbcDao.findAllById(gt1);
		assertEquals(0, users.size(), "size of users which id > 1 should be 0");

		User user = userJdbcDao.findById(eq1);
		assertNotNull(user, "user which id = 1 should not be null");
		assertEquals(1, user.getId(), "user id should be 1");
		assertEquals(36, user.getAge(), "user age should be 36");
		assertEquals("ruanwei", user.getName(), "user name should be ruanwei");
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

		userJdbcDao.save(beanForCreate.getName(), beanForCreate.getAge(), beanForCreate.getBirthday());
		userJdbcDao.saveWithKey(beanForCreate.getName(), beanForCreate.getAge(), beanForCreate.getBirthday());

		User user = userJdbcDao.findById(eq1);
		assertNotNull(user, "user should not be null");
		assertEquals(1, user.getId(), "user id should be 1983-07-06");
		assertEquals("ruanwei", user.getName(), "user name should be ruanwei");
		assertEquals(36, user.getAge(), "user age should be 36");

		Map<String, ?> userMap = userJdbcDao.findMapById(eq1);
		assertEquals(1, userMap.get("id"), "user id should be 1983-07-06");
		assertEquals("ruanwei", userMap.get("name"), "user name should be ruanwei");
		assertEquals(36, userMap.get("age"), "user age should be 36");

		List<User> users = userJdbcDao.findAllById(gt1);
		List<Map<String, Object>> userMaps = userJdbcDao.findAllMapById(gt1);
		List<User> allUsers = userJdbcDao.findAll();
		assertEquals(0, users.size() - userMaps.size(), "user list size should be equal");
		assertEquals(1, allUsers.size() - users.size(), "user list size diff should be 1");
		users.forEach(u -> assertTrue(u.getId() > 1, "user id should be gt 1"));
		users.forEach(u -> assertEquals("ruanwei_tmp", u.getName(), "user name should be ruanwei_tmp"));
		users.forEach(u -> assertEquals(36, u.getAge(), "user age should be 36"));

		// 2.更新age
		userJdbcDao.updateAge(beanForUpdateAndDelete);
		userJdbcDao.updateAge(mapForUpdateAndDelete);

		user = userJdbcDao.findById(eq1);
		assertNotNull(user, "user should not be null");
		assertEquals(1, user.getId(), "user id should be 1983-07-06");
		assertEquals("ruanwei", user.getName(), "user name should be ruanwei");
		assertEquals(36, user.getAge(), "user age should be 36");

		users = userJdbcDao.findAllById(gt1);
		users.forEach(u -> assertTrue(u.getId() > 1, "user id should be gt 1"));
		users.forEach(u -> assertEquals("ruanwei_tmp", u.getName(), "user name should be ruanwei_tmp"));
		users.forEach(u -> assertEquals(18, u.getAge(), "user age should be 18"));
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
		userJdbcDao.batchSave(objArrayForBatchCreate);

		User user = userJdbcDao.findById(eq1);
		assertNotNull(user, "user should not be null");
		assertEquals(1, user.getId(), "user id should be 1983-07-06");
		assertEquals("ruanwei", user.getName(), "user name should be ruanwei");
		assertEquals(36, user.getAge(), "user age should be 36");

		Map<String, ?> userMap = userJdbcDao.findMapById(eq1);
		assertEquals(1, userMap.get("id"), "user id should be 1983-07-06");
		assertEquals("ruanwei", userMap.get("name"), "user name should be ruanwei");
		assertEquals(36, userMap.get("age"), "user age should be 36");

		List<User> users = userJdbcDao.findAllById(gt1);
		List<Map<String, Object>> userMaps = userJdbcDao.findAllMapById(gt1);
		List<User> allUsers = userJdbcDao.findAll();
		assertEquals(0, users.size() - userMaps.size(), "user list size should be equal");
		assertEquals(1, allUsers.size() - users.size(), "user list size diff should be 1");
		users.forEach(u -> assertTrue(u.getId() > 1, "user id should be gt 1"));
		users.forEach(u -> assertEquals("ruanwei_tmp", u.getName(), "user name should be ruanwei_tmp"));
		users.forEach(u -> assertEquals(36, u.getAge(), "user age should be 36"));

		// 2.批量更新age
		userJdbcDao.batchUpdateAge(beanArrayForBatchUpdateAndDelete);
		userJdbcDao.batchUpdateAge(beanCollForBatchUpdateAndDelete);
		userJdbcDao.batchUpdateAge(mapArrayForBatchUpdateAndDelete);
		// TODO:这个方法调用的SQL有问题
		// userJdbcDao.batchUpdateAge(objArrayForBatchUpdateAndDelete);

		user = userJdbcDao.findById(eq1);
		assertNotNull(user, "user should not be null");
		assertEquals(1, user.getId(), "user id should be 1983-07-06");
		assertEquals("ruanwei", user.getName(), "user name should be ruanwei");
		assertEquals(36, user.getAge(), "user age should be 36");

		users = userJdbcDao.findAllById(gt1);
		users.forEach(u -> assertTrue(u.getId() > 1, "user id should be gt 1"));
		users.forEach(u -> assertEquals("ruanwei_tmp", u.getName(), "user name should be ruanwei_tmp"));
		users.forEach(u -> assertEquals(18, u.getAge(), "user age should be 18"));
	}

	@Order(3)
	@Test
	void testSpringJdbcWithTransaction() {
		log.info("3======================================================================================");
		try {
			userJdbcDao.transactionalMethod1(new User("ruanwei_tmp", 1, Date.valueOf("1983-07-06")));
		} catch (ArithmeticException e) {
			log.error("transaction rolled back for ArithmeticException", e);
		} finally {
			List<User> users = userJdbcDao.findAllById(gt0);
			assertEquals(2, users.size(), "user size should be 2");
		}
	}

	@Disabled
	@Order(4)
	@Test
	void testSpringDataJdbcCRUD() {
		log.info("4======================================================================================");
		// 1.创建
		userJdbcRepository.save(entityForCreate);

		UserJdbcEntity user = userJdbcRepository.findUserById(eq1);
		assertNotNull(user, "user should not be null");
		assertEquals(1, user.getId(), "user id should be 1983-07-06");
		assertEquals("ruanwei", user.getName(), "user name should be ruanwei");
		assertEquals(36, user.getAge(), "user age should be 36");

		Iterable<UserJdbcEntity> allUsers = userJdbcRepository.findAll();
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
	@Order(5)
	@Test
	void testSpringDataJdbcWithTransaction() {
		log.info("5======================================================================================");

		assertNotNull(userJdbcRepository, "userJdbcRepository should npt be null");
		try {
			userJdbcRepository.transactionalMethod1(new UserJdbcEntity("ruanwei_tmp", 1, Date.valueOf("1983-07-06")));
		} catch (Exception e) {
			log.error("transaction rolled back", e);
		}
	}

	private void testCreate() {
		int count = userJdbcRepository.createUser(beanForCreate.getName(), beanForCreate.getAge(),
				beanForCreate.getBirthday());
		log.info("jdbcRepository.createUser========" + count);

		UserJdbcEntity user = userJdbcRepository.save(entityForCreate);
		log.info("jdbcCrudRepository.save========" + user);
	}

	private void testUpdate() {
		int count = userJdbcRepository.updateUser(beanForUpdateAndDelete.getName(), beanForUpdateAndDelete.getAge());
		log.info("jdbcRepository.updateUser========" + count);
	}

	private void testQueryForSingleRow() {
		String name = userJdbcRepository.findNameById(eq1);
		log.info("jdbcRepository.findNameById========" + name);

		Map<String, Object> columnMap = userJdbcRepository.findNameAndAgeById(eq1);
		columnMap.forEach((k, v) -> log.info("jdbcRepository.findNameAndAgeById====" + k + "=" + v));

		UserJdbcEntity user = userJdbcRepository.findUserById(eq1);
		log.info("jdbcRepository.findUserById========" + user);

		Optional<UserJdbcEntity> user2 = userJdbcRepository.findById(eq1);
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

		List<UserJdbcEntity> userList = userJdbcRepository.findUserListById(gt0);
		userList.forEach(e -> log.info("jdbcRepository.findUserListById========" + e));

		// Iterable<User> userList2 = userJdbcRepository.findAllById(listParamForQuery);
		// userList2.forEach(e -> log.info("jdbcCrudRepository.findAllById========" +
		// e));

		Iterable<UserJdbcEntity> userList3 = userJdbcRepository.findAll();
		userList3.forEach(e -> log.info("jdbcCrudRepository.findAll()========" + e));
	}

	private void testQueryAsync() {
		Future<List<UserJdbcEntity>> userList = userJdbcRepository.findAllUser1();
		CompletableFuture<List<UserJdbcEntity>> userList2 = userJdbcRepository.findAllUser2();
		ListenableFuture<List<UserJdbcEntity>> userList3 = userJdbcRepository.findAllUser3();
	}

	private void testQueryForPagingAndSorting() {
		Sort sort = Sort.by(Direction.ASC, "age");
		Pageable pageable = PageRequest.of(0, 5);
		Pageable pageableWithSort = PageRequest.of(0, 5, sort);

		Iterable<UserJdbcEntity> userList = userJdbcRepository.findAll(sort);
		userList.forEach(e -> log.info("jdbcPagingAndSortingRepository.findAll========" + e));

		// see also java.util.Stream in java 8.
		Page<UserJdbcEntity> userPage = userJdbcRepository.findAll(pageable);
		userPage.forEach(e -> log.info("jdbcPagingAndSortingRepository.findAll========" + e));

		Slice<UserJdbcEntity> userSlice = userJdbcRepository.findAll(pageable);
		userSlice.forEach(e -> log.info("jdbcPagingAndSortingRepository.findAll========" + e));

		Page<UserJdbcEntity> userPage2 = userJdbcRepository.findAll(pageableWithSort);
		userPage2.forEach(e -> log.info("jdbcPagingAndSortingRepository.findAll========" + e));
	}

	private void testDelete() {
		int count = userJdbcRepository.deleteUser(2);
		log.info("jdbcRepository.deleteUser========" + count);

		userJdbcRepository.deleteById(3);
		userJdbcRepository.delete(entityForDelete);
		// userJdbcRepository.deleteAll(listParamForDelete);
		// userJdbcRepository.deleteAll();
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
