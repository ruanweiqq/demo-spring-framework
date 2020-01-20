package org.ruanwei.demo.springframework.dataAccess.jdbc;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ruanwei.demo.springframework.dataAccess.User;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ParameterizedPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.EmptySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.object.MappingSqlQuery;
import org.springframework.jdbc.object.SqlQuery;
import org.springframework.jdbc.object.SqlUpdate;
import org.springframework.jdbc.object.StoredProcedure;
import org.springframework.jdbc.object.UpdatableSqlQuery;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

/**
 * JdbcDaoSupport提供了setDataSource支持 NamedParameterJdbcTemplate支持IN表达式
 * 
 * @author ruanwei
 *
 */
public class UserJdbcDao /*implements CrudDao<User, Integer>*/ {
	private static Log log = LogFactory.getLog(UserJdbcDao.class);

	// 1.core JdbcTemplate & NamedParameterJdbcTemplate thread-safe
	private JdbcTemplate jdbcTemplate;
	// named parameters instead of the traditional JDBC "?" placeholders.
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	// 2.core SimpleJdbc classes
	private SimpleJdbcInsert simpleJdbcInsert;
	private SimpleJdbcCall simpleJdbcCall;// 执行存储过程或者函数

	// 3.RdbmsOperation objects.
	private SqlQuery<User> sqlQuery;
	private MappingSqlQuery<User> mappingSqlQuery;
	private UpdatableSqlQuery<User> updatableSqlQuery;
	private SqlUpdate sqlUpdate;
	private StoredProcedure storedProcedure;

	private static final String sql_insert = "insert into user(name,age,birthday) values(?, ?, ?)";
	private static final String sql_insert_namedParam = "insert into user(name,age,birthday) values(:name, :age, :birthday)";

	private static final String sql_update_age_by_name = "update user set age = ? where name = ?";
	private static final String sql_update_age_by_name_namedParam = "update user set age = :age where name = :name";

	private static final String sql_delete_by_name = "delete from user where name = ?";
	private static final String sql_delete_by_name_namedParam = "delete from user where name = :name";

	@Required
	public void setDataSource(@Qualifier("jdbcDataSource") DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);

		this.simpleJdbcInsert = new SimpleJdbcInsert(dataSource).withTableName("user").usingGeneratedKeyColumns("id");
		this.simpleJdbcCall = new SimpleJdbcCall(dataSource);
	}

	// =====Create=====
//	@Override
	public int save(User user) {
		log.info("save(User user)");
		return _update(sql_insert_namedParam, user, null);
	}

//	@Override
	public int saveMap(Map<String, ?> userMap) {
		log.info("saveMap(Map<String, ?> userMap)");
		return _update(sql_insert_namedParam, userMap, null);
	}

//	@Override
	public Integer saveWithKey(User user) {
		log.info("saveWithKey(User user)");

		KeyHolder keyHolder = new GeneratedKeyHolder();
		int key = _update(sql_insert_namedParam, user, keyHolder);

		log.info("generatedKey=" + key);
		return key;
	}

//	@Override
	public Integer saveMapWithKey(Map<String, ?> userMap) {
		log.info("saveMapWithKey(Map<String, ?> userMap)");

		KeyHolder keyHolder = new GeneratedKeyHolder();
		int key = _update(sql_insert_namedParam, userMap, keyHolder);
		log.info("generatedKey=" + key);
		return key;
	}

//	@Override
	public int saveAll(Iterable<User> users) {
		log.info("saveAll(Iterable<User> users)");

		int rows = 0;
		for (User user : users) {
			int row = save(user);
			rows += row;
		}
		return rows;
	}

	public int save2(User user) {
		log.info("save2(User user)" + user);
		return jdbcTemplate.update(sql_insert, user.getName(), user.getAge(), user.getBirthday());
	}

	public int save3(User user) {
		log.info("save3(User user)");

		PreparedStatementSetter pss = ps -> {
			ps.setString(1, user.getName());
			ps.setInt(2, user.getAge());
			ps.setDate(3, user.getBirthday());
		};
		return jdbcTemplate.update(sql_insert, pss);
	}

	public int saveWithKey2(User user) {
		log.info("saveWithKey2(User user)");

		PreparedStatementCreator psc = conn -> {
			PreparedStatement ps = conn.prepareStatement(sql_insert, new String[] { "id" });
			ps.setString(1, user.getName());
			ps.setInt(2, user.getAge());
			ps.setDate(3, user.getBirthday());
			return ps;
		};
		KeyHolder keyHolder = new GeneratedKeyHolder();

		int count = jdbcTemplate.update(psc, keyHolder);
		int key = keyHolder.getKey().intValue();
		log.info("generatedKey=" + key + ", count=" + count);
		return key;
	}

	// =====Read=====
//	@Override
	public User findById(Integer id) {
		log.info("findById(Integer id)");

		String sql = "select * from user where id = :id";
		Map<String, Integer> paramMap = new HashMap<String, Integer>();
		paramMap.put("id", id);

		User user = namedParameterJdbcTemplate.queryForObject(sql, paramMap,
				new BeanPropertyRowMapper<User>(User.class));

		log.info("user=" + user);
		return user;
	}

//	@Override
	public Map<String, ?> findMapById(Integer id) {
		log.info("findMapById(Integer id)");

		String sql = "select id, name, age, birthday from user where id = :id";
		Map<String, Integer> paramMap = new HashMap<String, Integer>();
		paramMap.put("id", id);
		Map<String, ?> columnMap = namedParameterJdbcTemplate.queryForMap(sql, paramMap);

		columnMap.forEach((k, v) -> log.info(k + "=" + v));
		return columnMap;
	}

//	@Override
	public boolean existsById(Integer id) {
		log.info("existsById(Integer id)");
		return findById(id) == null;
	}

//	@Override
	public List<User> findAll() {
		log.info("findAll()");

		String sql = "select * from user";
		List<User> userList = namedParameterJdbcTemplate.query(sql, new BeanPropertyRowMapper<User>(User.class));

		userList.forEach(user -> log.info("user=" + user));
		return userList;
	}

//	@Override
	public List<User> findAllById(Integer id) {
		log.info("findAllById(Iterable<Integer> ids)");

		String sql = "select * from user where id > :id";
		Map<String, Integer> paramMap = new HashMap<String, Integer>();
		paramMap.put("id", id);
		List<User> userList = namedParameterJdbcTemplate.query(sql, paramMap,
				new BeanPropertyRowMapper<User>(User.class));

		userList.forEach(user -> log.info("user=" + user));
		return userList;
	}

//	@Override
	public List<Map<String, Object>> findAllMapById(Integer id) {
		log.info("findAllMapById(Integer id)");

		String sql = "select name, age, birthday from user where id > :id";
		Map<String, Integer> paramMap = new HashMap<String, Integer>();
		paramMap.put("id", id);
		List<Map<String, Object>> columnMapList2 = namedParameterJdbcTemplate.queryForList(sql, paramMap);

		columnMapList2.forEach(columbMap -> columbMap.forEach((k, v) -> log.info(k + "=" + v)));
		return columnMapList2;
	}

//	@Override
	public long count() {
		log.info("count()");

		String sql = "select count(*) from user";
		Integer count = namedParameterJdbcTemplate.queryForObject(sql, EmptySqlParameterSource.INSTANCE, Integer.class);

		log.info("count=" + count);
		return count;
	}

	public String findNameById(Integer id) {
		log.info("findNameById(Integer id)");

		String sql = "select name from user where id = :id";
		Map<String, Integer> paramMap = new HashMap<String, Integer>();
		paramMap.put("id", id);
		String column = namedParameterJdbcTemplate.queryForObject(sql, paramMap, String.class);

		log.info("column=" + column);
		return column;
	}

	public List<String> findAllNamesById(Integer largerThanId) {
		log.info("findAllNamesById(Integer largerThanId)");

		String sql = "select name from user where id > :id";
		Map<String, Integer> paramMap = new HashMap<String, Integer>();
		paramMap.put("id", largerThanId);
		List<String> columnList = namedParameterJdbcTemplate.queryForList(sql, paramMap, String.class);

		columnList.forEach(column -> log.info("column=" + column));
		return columnList;
	}

	// RowMapperResultSetExtractor & BeanPropertyRowMapper
	public User findById2(Integer id) {
		log.info("findById2(int id)");

		String sql = "select * from user where id = ?";
		User user = jdbcTemplate.queryForObject(sql, new Object[] { id }, new BeanPropertyRowMapper<User>(User.class));

		log.info("user=" + user);
		return user;
	}

	// RowMapperResultSetExtractor & ColumnMapRowMapper
	public Map<String, ?> findMapById2(Integer id) {
		log.info("findMapById2(Integer id)");

		String sql = "select name, age from user where id = ?";
		Map<String, Object> columnMap = jdbcTemplate.queryForMap(sql, new Object[] { id });

		columnMap.forEach((k, v) -> log.info(k + "=" + v));
		return columnMap;
	}

	public boolean existsById2(Integer id) {
		log.info("existsById2(Integer id)");
		return findById2(id) == null;
	}

	public List<User> findAll2() {
		log.info("findAll2()");

		String sql = "select * from user";
		List<User> userList = jdbcTemplate.queryForList(sql, User.class);

		userList.forEach(user -> log.info("user=" + user));
		return userList;
	}

	public List<User> findAllById2(Integer id) {
		log.info("findAllById2(Integer id)");

		String sql = "select * from user where id > ?";
		List<User> userList1 = jdbcTemplate.queryForList(sql, new Object[] { id }, User.class);

		PreparedStatementSetter pss0 = ps -> ps.setInt(1, id);
		List<User> userList2 = jdbcTemplate.query(sql, pss0, new BeanPropertyRowMapper<User>(User.class));

		userList1.forEach(user -> log.info("user1=" + user));
		userList2.forEach(user -> log.info("user2" + user));
		return userList2;
	}

	public List<Map<String, Object>> findAllMapById2(Integer id) {
		log.info("findAllMapById2(Integer id)");

		String sql = "select name, age from user where id > ?";
		List<Map<String, Object>> columnMapList = jdbcTemplate.queryForList(sql, new Object[] { id });

		columnMapList.forEach(columbMap -> columbMap.forEach((k, v) -> log.info(k + "=" + v)));
		return columnMapList;
	}

	public long count2() {
		log.info("count2()");

		String sql = "select count(*) from user";
		Integer count = jdbcTemplate.queryForObject(sql, Integer.class);

		log.info("count=" + count);
		return count;
	}

	// RowMapperResultSetExtractor & SingleColumnRowMapper
	public String findNameById2(Integer id) {
		log.info("findNameById2(Integer id)");

		String sql = "select name from user where id = ?";
		String column = jdbcTemplate.queryForObject(sql, new Object[] { id }, String.class);

		log.info("column=" + column);
		return column;
	}

	public List<String> findAllNamesById2(Integer largerThanId) {
		log.info("findAllNamesById2(Integer largerThanId)");

		String sql = "select name from user where id > ?";
		List<String> columnList = jdbcTemplate.queryForList(sql, new Object[] { largerThanId }, String.class);

		columnList.forEach(column -> log.info("column=" + column));
		return columnList;
	}

	// =====Update=====
	public int updateAgeByName(User user) {
		log.info("updateAgeByName(User user)");
		return _update(sql_update_age_by_name_namedParam, user, null);
	}

	public int updateAgeByName(Map<String, ?> userMap) {
		log.info("updateAgeByName(Map<String, ?> userMap)");
		return _update(sql_update_age_by_name_namedParam, userMap, null);
	}

	public int updateAgeByName2(User user) {
		log.info("updateAgeByName2(User user)" + user);
		return jdbcTemplate.update(sql_update_age_by_name, user.getName(), user.getAge());
	}

	public int updateAgeByName3(User user) {
		log.info("updateAgeByName3(User user)");

		PreparedStatementSetter pss = ps -> {
			ps.setString(1, user.getName());
			ps.setInt(2, user.getAge());
		};
		return jdbcTemplate.update(sql_update_age_by_name, pss);
	}

	// =====Delete=====
//	@Override
	public int deleteById(Integer id) {
		log.info("deleteById(Integer id)");

		String sql = "delete from user where id = :id";
		Map<String, Integer> paramMap = new HashMap<String, Integer>();
		paramMap.put("id", id);

		return _update(sql, paramMap, null);
	}

//	@Override
	public int delete(User user) {
		log.info("delete(User user)");

		String sql = "delete from user where name = :name and age = :age and birthday = :birthday";
		return _update(sql, user, null);
	}

//	@Override
	public int deleteMap(Map<String, ?> mapUser) {
		log.info("deleteMap(Map<String, ?> mapUser)");

		String sql = "delete from user";
		return _update(sql, mapUser, null);
	}

//	@Override
	public int deleteAll(Iterable<? extends User> users) {
		log.info("deleteAll(Iterable<? extends User> users");

		int rows = 0;
		for (User user : users) {
			int row = delete(user);
			rows += row;
		}
		return rows;
	}

//	@Override
	public int deleteAll() {
		log.info("deleteAll()");

		String sql = "delete from user";
		return _update(sql, Collections.emptyMap(), null);
	}

	public int deleteByName(User user) {
		log.info("deleteByName(User user)");
		return _update(sql_delete_by_name_namedParam, user, null);
	}

	public int deleteByName(Map<String, ?> userMap) {
		log.info("deleteByName(Map<String, ?> userMap)");
		return _update(sql_delete_by_name_namedParam, userMap, null);
	}

	///////////////////////////////////////////////////////

	// ===================Part 2=============================
	// Batch Create
	public int[] batchSave(User... users) {
		log.info("batchSave(User... users)");
		return _batchUpdate(sql_insert_namedParam, users);
	}

	// Batch Update
	public int[] batchUpdateAgeByName(Map<String, Object>[] users) {
		log.info("batchUpdate(Map<String, User>[] users)");
		return _batchUpdate(sql_update_age_by_name_namedParam, users);
	}

	// Batch Delete
	public int[] batchDeleteByName(Collection<User> users) {
		log.info("batchDelete(Collection<User> users)");
		return _batchUpdate(sql_delete_by_name_namedParam, users);
	}

	// Batch Create/Update/Delete
	public int[] batchSave2(List<User> users) {
		log.info("batchSave2(List<User> users)");

		BatchPreparedStatementSetter bpss = new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ps.setString(1, users.get(i).getName());
				ps.setInt(2, users.get(i).getAge());
				ps.setDate(3, users.get(i).getBirthday());
			}

			@Override
			public int getBatchSize() {
				return users.size();
			}
		};

		int[] updateCounts = jdbcTemplate.batchUpdate(sql_insert, bpss);
		return updateCounts;
	}

	public int[][] batchSave3(Collection<User> users) {
		log.info("batchSave3(Collection<User> users)");

		ParameterizedPreparedStatementSetter<User> uppss = (ps, user) -> {
			ps.setString(1, user.getName());
			ps.setInt(2, user.getAge());
			ps.setDate(3, user.getBirthday());
		};

		int[][] updateCounts = jdbcTemplate.batchUpdate(sql_insert, users, 2, uppss);
		return updateCounts;
	}

	public int[] batchSave4(List<User> users) {
		log.info("batchSave4(List<User> users)");
		if (jdbcTemplate != null) {
			String sql = "update user set age=:age where id!=:id";
			List<Object[]> batch = new ArrayList<Object[]>();
			for (User user : users) {
				Object[] values = new Object[] { user.getAge(), 1 };
				batch.add(values);
			}
			int[] updateCounts = jdbcTemplate.batchUpdate(sql, batch);
			return updateCounts;
		}
		throw new UnsupportedOperationException();
	}

	// ====================private====================
	// ====================update====================
	private int _update(String sql, Object candidate, KeyHolder keyHolder) {
		log.info("_update(String sql, Object candidate, KeyHolder keyHolder)");

		int ret = 0;
		// MapSqlParameterSource or BeanPropertySqlParameterSource
		SqlParameterSource sqlParamSource = SqlParameterSourceUtils.createBatch(candidate)[0];
		if (keyHolder == null) {
			ret = namedParameterJdbcTemplate.update(sql, sqlParamSource);
		} else {
			namedParameterJdbcTemplate.update(sql, sqlParamSource, keyHolder);
			ret = keyHolder.getKey().intValue();
		}
		return ret;
	}

	private int _update(String sql, Map<String, ?> valueMap, KeyHolder keyHolder) {
		log.info("_update(String sql, Map<String, ?> valueMap, KeyHolder keyHolder)");

		int ret = 0;
		// MapSqlParameterSource or BeanPropertySqlParameterSource
		SqlParameterSource sqlParamSource = SqlParameterSourceUtils.createBatch(valueMap)[0];
		if (keyHolder == null) {
			ret = namedParameterJdbcTemplate.update(sql, sqlParamSource);
		} else {
			namedParameterJdbcTemplate.update(sql, sqlParamSource, keyHolder);
			ret = keyHolder.getKey().intValue();
		}
		return ret;
	}

	// ====================batch update====================
	private int[] _batchUpdate(String sql, Object... candidates) {
		log.info("_batchUpdate(Object... candidates)");

		SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(candidates);
		return namedParameterJdbcTemplate.batchUpdate(sql, batch);
	}

	private int[] _batchUpdate(String sql, Map<String, ?>[] valueMaps) {
		log.info("_batchUpdate(Map<String, ?>[] valueMaps)");

		SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(valueMaps);
		return namedParameterJdbcTemplate.batchUpdate(sql, batch);
	}

	private int[] _batchUpdate(String sql, Collection<?> candidates) {
		log.info("_batchUpdate(Collection<?> candidates)");

		SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(candidates);
		return namedParameterJdbcTemplate.batchUpdate(sql, batch);
	}

	// ====================SimpleJdbc====================
	public void insertUser6(User user) {
		log.info("insertUser6(User user)");
		Map<String, Object> parameters = new HashMap<String, Object>(3);
		parameters.put("name", user.getName());
		parameters.put("age", user.getAge());
		parameters.put("birthday", user.getBirthday());

		simpleJdbcInsert.execute(parameters);
		Number newId = simpleJdbcInsert.executeAndReturnKey(parameters);
		log.info("generatedKey=" + newId.longValue());
	}

	public void callProcedure() {
		log.info("callProcedure()");
		SqlParameterSource in = new MapSqlParameterSource().addValue("in_id", 1);
		simpleJdbcCall.withProcedureName("user").withoutProcedureColumnMetaDataAccess().useInParameterNames("in_id")
				.declareParameters(new SqlParameter("in_id", Types.NUMERIC),
						new SqlOutParameter("out_name", Types.VARCHAR), new SqlOutParameter("out_age", Types.VARCHAR),
						new SqlOutParameter("out_birthday", Types.DATE));
		Map<String, Object> out = simpleJdbcCall.execute(in);
		User user = new User();
		user.setName((String) out.get("out_name"));
		user.setAge((int) out.get("out_age"));
		user.setBirthday((Date) out.get("out_birthday"));
	}

	public void createTable() {
		log.info("createTable()");
		if (jdbcTemplate != null) {
			String sql = "create table user(id int(11) not null AUTO_INCREMENT,name varchar(128) not null default '',gender int(11) not null default 0,age int(4) not null default 0";
			jdbcTemplate.execute(sql);
		}
		throw new UnsupportedOperationException();
	}

	// ====================transaction====================
	// 不能在事务方法中进行try-catch
	// REQUIRED
	public void transactionalMethod1(User user) {
		save2(user);

		// REQUIRES_NEW，理论上这个方法不回滚
		transactionalMethod2(new User("ruanwei_tmp", 2, Date.valueOf("1983-07-06")));

		int i = 1 / 0;
	}

	// 不能在事务方法中进行try-catch
	public void transactionalMethod2(User user) {
		save2(user);
	}

}
