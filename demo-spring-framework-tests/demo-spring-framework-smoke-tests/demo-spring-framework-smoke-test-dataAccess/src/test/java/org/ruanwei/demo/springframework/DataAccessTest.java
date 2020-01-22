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
import org.ruanwei.demo.springframework.dataAccess.jdbc.UserJdbcDao;
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
// @DirtiesContext
// @Rollback
// @Commit
// @Transactional("txManager")
@ActiveProfiles("development")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//@SpringJUnitConfig(locations = "classpath:spring/applicationContext.xml")
@SpringJUnitConfig(AppConfig.class)
public class DataAccessTest {
	private static Log log = LogFactory.getLog(DataAccessTest.class);

	private static final User beanForCreate;
	private static final UserJdbcEntity entityForCreate;
	private static final Map<String, Object> mapForCreate;

	private static final User beanForUpdate;
	private static final Map<String, Object> mapForUpdate;

	private static final User beanForDelete;
	private static final UserJdbcEntity entityForDelete;
	private static final Map<String, Object> mapForDelete;

	private static final User[] beanArrayForBatchCreate;
	private static final User[] beanArrayForBatchUpdate;
	private static final Collection<User> beanCollForBatchCreate;
	private static final Collection<User> beanCollForBatchUpdate;
	private static final Map<String, Object>[] mapArrayForBatchUpdate;

	private static final int gt0 = 0;
	private static final int gt1 = 1;
	private static final int eq1 = 1;

	static {
		// create
		beanForCreate = new User("ruanwei_tmp", 36, Date.valueOf("1983-07-06"));
		entityForCreate = new UserJdbcEntity("ruanwei_tmp", 36, Date.valueOf("1983-07-06"));
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
		entityForDelete = new UserJdbcEntity("ruanwei_tmp", 18, Date.valueOf("1983-07-06"));
		mapForDelete = new HashMap<>();
		mapForDelete.put("name", beanForDelete.getName());
		mapForDelete.put("age", beanForDelete.getAge());
		mapForDelete.put("birthday", beanForDelete.getBirthday());

		// batch update
		beanArrayForBatchCreate = new User[] { new User("ruanwei_tmp", 36, Date.valueOf("1983-07-06")),
				new User("ruanwei_tmp", 36, Date.valueOf("1983-07-06")) };
		// batch update
		beanArrayForBatchUpdate = new User[] { new User("ruanwei_tmp", 18, Date.valueOf("1983-07-06")),
				new User("ruanwei_tmp", 18, Date.valueOf("1983-07-06")) };

		beanCollForBatchCreate = Arrays.asList(beanArrayForBatchCreate);
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

	// 若实现接口则被代理，注入失败
	@Autowired
	private UserJdbcDao userJdbcDao;

	@Autowired
	private UserJdbcRepository userJdbcRepository;

	// @Autowired
	private UserJpaRepository userJpaRepository;

	@BeforeAll
	static void beforeAll() {
		log.info("beforeAll()");
	}

	@BeforeEach
	void beforeEach() {
		log.info("beforeEach()");
		assertNotNull(userJdbcDao, "userJdbcDao should not be null");
		// assertNotNull(userJdbcRepository, "userJdbcRepository should not be null");
		// assertNotNull(userJpaRepository, "userJpaRepository should not be null");

		userJdbcDao.deleteByName(beanForDelete);
		userJdbcDao.deleteByName(mapForDelete);
		userJdbcDao.batchDeleteByName(beanCollForBatchUpdate);

		List<User> users = userJdbcDao.findAllById(gt0);
		assertEquals(1, users.size(), "user size should be 1");
	}

	@Order(1)
	@Test
	void testSpringJdbcCRUD() {
		log.info("1======================================================================================");

		// 1.创建
		userJdbcDao.save(beanForCreate);
		userJdbcDao.saveWithKey(beanForCreate);

		userJdbcDao.saveMap(mapForCreate);
		userJdbcDao.saveMapWithKey(mapForCreate);

		userJdbcDao.save2(beanForCreate);
		userJdbcDao.save3(beanForCreate);
		userJdbcDao.saveWithKey2(beanForCreate);

		User user = userJdbcDao.findById(eq1);
		Map<String, ?> userMap = userJdbcDao.findMapById(eq1);
		assertNotNull(user, "user should not be null");
		assertEquals(1, user.getId(), "user id should be 1983-07-06");
		assertEquals("ruanwei", user.getName(), "user name should be ruanwei");
		assertEquals(36, user.getAge(), "user age should be 36");
		assertEquals(1, userMap.get("id"), "user id should be 1983-07-06");
		assertEquals("ruanwei", userMap.get("name"), "user name should be ruanwei");
		assertEquals(36, userMap.get("age"), "user age should be 36");

		List<User> users = userJdbcDao.findAllById(gt1);
		List<Map<String, Object>> userMaps = userJdbcDao.findAllMapById(gt1);
		List<String> names = userJdbcDao.findAllNamesById(gt1);
		List<User> allUsers = userJdbcDao.findAll();
		assertEquals(0, users.size() - userMaps.size(), "user list size should be equal");
		assertEquals(0, users.size() - names.size(), "user list size should be equal");
		assertEquals(1, allUsers.size() - users.size(), "user list size diff should be 1");
		users.forEach(u -> assertTrue(u.getId() > 1, "user id should be gt 1"));
		users.forEach(u -> assertEquals("ruanwei_tmp", u.getName(), "user name should be ruanwei_tmp"));
		users.forEach(u -> assertEquals(36, u.getAge(), "user age should be 36"));

		// 2.根据name更新age
		userJdbcDao.updateAgeByName(beanForUpdate);
		userJdbcDao.updateAgeByName(mapForUpdate);

		user = userJdbcDao.findById(eq1);
		users = userJdbcDao.findAllById(gt1);
		assertEquals(1, user.getId(), "user id should be 1983-07-06");
		assertEquals("ruanwei", user.getName(), "user name should be ruanwei");
		assertEquals(36, user.getAge(), "user age should be 36");
		users.forEach(u -> assertTrue(u.getId() > 1, "user id should be gt 1"));
		users.forEach(u -> assertEquals("ruanwei_tmp", u.getName(), "user name should be ruanwei_tmp"));
		users.forEach(u -> assertEquals(18, u.getAge(), "user age should be 18"));

		// 3.根据name删除
		userJdbcDao.deleteByName(beanForDelete);
		userJdbcDao.deleteByName(mapForDelete);

		user = userJdbcDao.findById(eq1);
		assertNotNull(user, "user should not be null");
		assertEquals(1, user.getId(), "user id should be 1");
		assertEquals(36, user.getAge(), "user age should be 36");
		assertEquals("ruanwei", user.getName(), "user name should be ruanwei");

		users = userJdbcDao.findAllById(gt1);
		allUsers = userJdbcDao.findAll();
		assertEquals(0, users.size(), "user size should be 0");
		assertEquals(1, allUsers.size(), "all user size should be 1");
	}

	@Order(2)
	@Test
	void testSpringJdbcBatchCRUD() {
		log.info("2======================================================================================");

		// 1.批量创建
		userJdbcDao.batchSave(beanArrayForBatchCreate);
		userJdbcDao.batchSave2(Arrays.asList(beanCollForBatchCreate.toArray(new User[0])));
		userJdbcDao.batchSave3(beanCollForBatchCreate);

		User user = userJdbcDao.findById(eq1);
		List<User> users = userJdbcDao.findAllById(gt1);
		Map<String, ?> userMap = userJdbcDao.findMapById(eq1);
		assertNotNull(user, "user should not be null");
		assertEquals(1, user.getId(), "user id should be 1983-07-06");
		assertEquals("ruanwei", user.getName(), "user name should be ruanwei");
		assertEquals(36, user.getAge(), "user age should be 36");
		assertEquals(1, userMap.get("id"), "user id should be 1983-07-06");
		assertEquals("ruanwei", userMap.get("name"), "user name should be ruanwei");
		assertEquals(36, userMap.get("age"), "user age should be 36");

		users = userJdbcDao.findAllById(gt1);
		List<Map<String, Object>> userMaps = userJdbcDao.findAllMapById(gt1);
		List<String> names = userJdbcDao.findAllNamesById(gt1);
		List<User> allUsers = userJdbcDao.findAll();
		assertEquals(0, users.size() - userMaps.size(), "user list size should be equal");
		assertEquals(0, users.size() - names.size(), "user list size should be equal");
		assertEquals(1, allUsers.size() - users.size(), "user list size diff should be 1");
		users.forEach(u -> assertTrue(u.getId() > 1, "user id should be gt 1"));
		users.forEach(u -> assertEquals("ruanwei_tmp", u.getName(), "user name should be ruanwei_tmp"));
		users.forEach(u -> assertEquals(36, u.getAge(), "user age should be 36"));

		// 2.根据name批量更新age
		userJdbcDao.batchUpdateAgeByName(mapArrayForBatchUpdate);

		user = userJdbcDao.findById(eq1);
		users = userJdbcDao.findAllById(gt1);
		assertEquals(1, user.getId(), "user id should be 1983-07-06");
		assertEquals("ruanwei", user.getName(), "user name should be ruanwei");
		assertEquals(36, user.getAge(), "user age should be 36");
		users.forEach(u -> assertTrue(u.getId() > 1, "user id should be gt 1"));
		users.forEach(u -> assertEquals("ruanwei_tmp", u.getName(), "user name should be ruanwei_tmp"));
		users.forEach(u -> assertEquals(18, u.getAge(), "user age should be 18"));

		// 3.根据name批量删除
		userJdbcDao.batchDeleteByName(beanCollForBatchUpdate);

		user = userJdbcDao.findById(eq1);
		assertNotNull(user, "user should not be null");
		assertEquals(1, user.getId(), "user id should be 1");
		assertEquals(36, user.getAge(), "user age should be 36");
		assertEquals("ruanwei", user.getName(), "user name should be ruanwei");

		users = userJdbcDao.findAllById(gt1);
		allUsers = userJdbcDao.findAll();
		assertEquals(0, users.size(), "user size should be 0");
		assertEquals(1, allUsers.size(), "all user size should be 1");
	}

	@Order(3)
	@Test
	void testSpringJdbcWithTransaction() {
		log.info("3======================================================================================");
		try {
			userJdbcDao.transactionalMethod1(new User("ruanwei_tmp", 1, Date.valueOf("1983-07-06")));
		} catch (ArithmeticException e) {
			log.error("transaction rolled back as ArithmeticException", e);
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
		UserJdbcEntity user = userJdbcRepository.save(entityForCreate);
		log.info("jdbcCrudRepository.save========" + user);

		//Iterable<User> userList = userJdbcRepository.saveAll(listParamForCreate);
		//userList.forEach(e -> log.info("jdbcCrudRepository.saveAll========" + e));
		
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

		// Iterable<User> userList = userJdbcRepository.saveAll(listParamForCreate);
		// userList.forEach(e -> log.info("jdbcCrudRepository.saveAll========" + e));
	}

	private void testUpdate() {
		int count = userJdbcRepository.updateUser(beanForUpdate.getName(), beanForUpdate.getAge());
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
