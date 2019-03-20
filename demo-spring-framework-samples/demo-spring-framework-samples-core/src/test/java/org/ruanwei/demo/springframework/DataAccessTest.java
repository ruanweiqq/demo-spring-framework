package org.ruanwei.demo.springframework;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.sql.Date;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.ruanwei.demo.springframework.dataAccess.User;
import org.ruanwei.demo.springframework.dataAccess.jdbc.UserJdbcDao;
import org.ruanwei.demo.springframework.dataAccess.orm.hibernate.UserHibernateDao;
import org.ruanwei.demo.springframework.dataAccess.orm.jpa.UserJpaDao;
import org.ruanwei.demo.springframework.dataAccess.oxm.MarshallerClient;
import org.ruanwei.demo.springframework.dataAccess.oxm.Settings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

/**
 * 
 * @author ruanwei
 *
 */
// @Rollback
// @Commit
// @Transactional("txManager")
@ActiveProfiles("development")
//@SpringJUnitConfig(locations = "classpath:spring/applicationContext2.xml")
@SpringJUnitConfig(AppConfig2.class)
public class DataAccessTest {
	private static Log log = LogFactory.getLog(DataAccessTest.class);

	private static final String update_sql_21 = "update user set age = ? where name = ?";
	private static final String update_sql_22 = "update user set age = :age where name = :name";

	private static final String select_sql_11 = "select * from user where id = ?";
	private static final String select_sql_12 = "select * from user where id = :id";

	private static final String select_sql_21 = "select name, age from user where id = ?";
	private static final String select_sql_22 = "select name, age from user where id = :id";

	private static final String select_sql_31 = "select name from user where id = ?";
	private static final String select_sql_32 = "select name from user where id = :id";

	private static final String select_sql_41 = "select * from user where id > ?";
	private static final String select_sql_42 = "select * from user where id > :id";

	private static final String select_sql_51 = "select name, age from user where id > ?";
	private static final String select_sql_52 = "select name, age from user where id > :id";

	private static final String select_sql_61 = "select name from user where id > ?";
	private static final String select_sql_62 = "select name from user where id > :id";

	private static final String delete_sql_11 = "delete from user where id > ?";
	private static final String delete_sql_12 = "delete from user where id > :id";

	private static final User paramForRead = new User("ruanwei_tmp", 35, Date.valueOf("1983-07-06"));

	private static final User paramForCreate = new User("ruanwei_tmp", 35, Date.valueOf("1983-07-06"));
	private static final User[] arrayParamForBatchCreate = new User[] { paramForCreate };
	private static final List<User> listParamForCreate = Arrays.asList(arrayParamForBatchCreate);

	private static final User paramForUpdate2 = new User(2, "ruanwei_tmp", 18, Date.valueOf("1983-07-06"));
	private static final User paramForUpdate = new User("ruanwei_tmp", 18, Date.valueOf("1983-07-06"));
	private static final User[] arrayParamForbatchUpdate = new User[] { paramForUpdate };
	private static final List<User> listParamForUpdate = Arrays.asList(arrayParamForbatchUpdate);

	private static final User paramForDelete = new User("ruanwei_tmp", 18, Date.valueOf("1983-07-06"));
	private static final User[] arrayParamForBatchDelete = new User[] { paramForDelete };
	private static final List<User> listParamForDelete = Arrays.asList(arrayParamForBatchDelete);

	private static final Map<String, Object> mapParamForCreate = new HashMap<String, Object>();
	private static final Map<String, Object> mapParamForUpdate = new HashMap<String, Object>();
	private static final Map<String, Object> mapParamForQuery1 = new HashMap<String, Object>();
	private static final Map<String, Object> mapParamForQuery2 = new HashMap<String, Object>();

	private static final Object[] arrayParamForUpdate = new Object[] { 18, "ruanwei_tmp" };
	private static final Object[] arrayParamForQuery1 = new Object[] { 1 };
	private static final Object[] arrayParamForQuery2 = new Object[] { "ruanwei_tmp", 18 };

	private static final int id1 = 1;
	private static final int id3 = 3;

	static {
		mapParamForCreate.put("name", "ruanwei_tmp");
		mapParamForCreate.put("age", 35);
		mapParamForCreate.put("birthday", Date.valueOf("1983-07-06"));

		mapParamForUpdate.put("name", "ruanwei_tmp");
		mapParamForUpdate.put("age", 18);

		mapParamForQuery1.put("id", 1);

		mapParamForQuery2.put("name", "ruanwei_tmp");
		mapParamForQuery2.put("age", 18);
	}

	@Autowired
	private UserJdbcDao userJdbcDao;

	@Autowired
	private UserHibernateDao userHibernateDao;

	@Autowired
	private UserJpaDao userJpaDao;

	@Autowired
	private MarshallerClient marshallerClient;

	@BeforeAll
	static void beforeAll() {
		log.info("beforeAll()");
	}

	@BeforeEach
	void beforeEach() {
		log.info("beforeEach()");
	}

	@Disabled
	@Test
	void testJdbcDao() {
		assertNotNull(userJdbcDao, "userJdbcDao is null++++++++++++++++++++++++++++");

		testJdbcCRUD1();
		testJdbcCRUD2();

		testJdbcDaoWithTransaction();
	}

	// @Disabled
	@Test
	void testHibernateDao() {
		assertNotNull(userHibernateDao, "userHibernateDao is null++++++++++++++++++++++++++++");

		testHibernateCRUD1();
		testHibernateCRUD2();
	}

	// @Disabled
	@Test
	void testJpaDao() {
		assertNotNull(userJpaDao, "userJpaDao is null++++++++++++++++++++++++++++");

		testJpaCRUD1();
		testJpaCRUD2();
	}

	@Disabled
	@Test
	void testMarshaller() {
		assertNotNull(marshallerClient, "marshallerClient is null++++++++++++++++++++++++++++");
		Settings settings = new Settings();
		marshallerClient.saveSettings(settings);
		settings = marshallerClient.loadSettings();
	}

	@AfterEach
	void afterEach() {
		log.info("afterEach()");
	}

	@AfterAll
	static void afterAll() {
		log.info("afterAll()");
	}

	private void testJdbcCRUD1() {
		// 1. Create
		userJdbcDao.save(paramForCreate);
		userJdbcDao.saveAll(listParamForCreate);

		// 2. Update
		userJdbcDao.update(paramForUpdate2);
		userJdbcDao.updateByExample(update_sql_21, arrayParamForUpdate);
		// userJdbcDao.batchUpdateByExample(update_sql_21, batchArgs);

		// 3.1. Read single row
		userJdbcDao.findById(id1);
		userJdbcDao.existsById(id1);
		userJdbcDao.exists(paramForRead);
		userJdbcDao.findByExample(select_sql_11, arrayParamForQuery1);
		userJdbcDao.findColumnMapByExample(select_sql_21, arrayParamForQuery1);
		userJdbcDao.findSingleColumnByExample(select_sql_31, String.class, arrayParamForQuery1);

		// 3.2. Read multiple row
		userJdbcDao.findAll();
		userJdbcDao.count();
		// userJdbcDao.findAllById(ids);
		userJdbcDao.findAllByExample(select_sql_41, arrayParamForQuery2);
		userJdbcDao.findAllColumnMapByExample(select_sql_51, arrayParamForQuery2);
		userJdbcDao.findAllSingleColumnByExample(select_sql_61, String.class, arrayParamForQuery2);

		// 4. Delete
		userJdbcDao.deleteById(id3);
		userJdbcDao.delete(paramForDelete);
		userJdbcDao.deleteAll(listParamForDelete);
		// userJdbcDao.deleteAll();
		userJdbcDao.updateByExample(delete_sql_11, arrayParamForQuery2);
	}

	private void testJdbcCRUD2() {
		// 1.Create
		userJdbcDao.save2(paramForCreate);
		userJdbcDao.saveAll2(listParamForCreate);

		// 2.Update
		userJdbcDao.update2(paramForUpdate2);
		userJdbcDao.updateByExample(update_sql_22, mapParamForUpdate);
		// userJdbcDao.batchUpdateByExample(update_sql_22, batchArgs);

		// 3.1.Read single row
		userJdbcDao.findById2(id1);
		userJdbcDao.existsById2(id1);
		userJdbcDao.exists2(paramForRead);
		userJdbcDao.findByExample(select_sql_12, mapParamForQuery1);
		userJdbcDao.findColumnMapByExample(select_sql_22, mapParamForQuery1);
		userJdbcDao.findSingleColumnByExample(select_sql_32, String.class, mapParamForQuery1);

		// 3.2.Read multiple row
		userJdbcDao.findAll();
		userJdbcDao.count();
		// userJdbcDao.findAllById(ids);
		userJdbcDao.findAllByExample(select_sql_42, mapParamForQuery2);
		userJdbcDao.findAllColumnMapByExample(select_sql_52, mapParamForQuery2);
		userJdbcDao.findAllSingleColumnByExample(select_sql_62, String.class, mapParamForQuery2);

		// 4.Delete
		userJdbcDao.deleteById2(id3);
		userJdbcDao.delete2(paramForDelete);
		userJdbcDao.deleteAll2(listParamForDelete);
		// userJdbcDao.deleteAll();
		userJdbcDao.updateByExample(delete_sql_12, mapParamForQuery2);
	}

	void testJdbcDaoWithTransaction() {
		assertNotNull(userJdbcDao, "userJdbcDao is null++++++++++++++++++++++++++++");
		try {
			userJdbcDao.transactionalMethod(arrayParamForBatchCreate);
		} catch (Exception e) {
			log.error("transaction rolled back", e);
		}
	}

	private void testHibernateCRUD1() {
		userHibernateDao.findAll();
	}

	private void testHibernateCRUD2() {
		userHibernateDao.findAll();
	}

	private void testJpaCRUD1() {
		userJpaDao.findAll();
	}

	private void testJpaCRUD2() {
		userJpaDao.findAll();
	}
}
