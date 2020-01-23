package org.ruanwei.demo.springframework.dataAccess.jdbc;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ruanwei.demo.springframework.dataAccess.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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

	private static final String sql_select_by_id1 = "select * from user where id = ?";
	private static final String sql_select_by_id_namedParam1 = "select * from user where id = :id";

	private static final String sql_select_by_id2 = "select id, name, age, birthday from user where id = ?";
	private static final String sql_select_by_id_namedParam2 = "select id, name, age, birthday from user where id = :id";

	private static final String sql_select_by_gt_id1 = "select id, name, age, birthday from user where id > ?";
	private static final String sql_select_by_gt_id_namedParam1 = "select id, name, age, birthday from user where id > :id";

	private static final String sql_select_by_gt_id2 = "select * from user where id > ?";
	private static final String sql_select_by_gt_id_namedParam2 = "select * from user where id > :id";

	private static final String sql_select_all1 = "select * from user";
	private static final String sql_select_all2 = "select id, name, age, birthday from user";

	private static final String sql_select_count = "select count(*) from user";

	private static final String sql_insert = "insert into user(name,age,birthday) values(?, ?, ?)";
	private static final String sql_insert_namedParam = "insert into user(name,age,birthday) values(:name, :age, :birthday)";

	private static final String sql_update_age = "update user set age = ? where name = ? and birthday = ?";
	private static final String sql_update_age_namedParam = "update user set age = :age where name = :name and birthday = :birthday";

	private static final String sql_delete = "delete from user where name = :name and age = :age and birthday = :birthday";
	private static final String sql_delete_namedParam = "delete from user where name = :name and age = :age and birthday = :birthday";

	private static final String sql_delete_all = "delete from user";

	@Autowired
	public void setDataSource(@Qualifier("jdbcDataSource") DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);

		this.simpleJdbcInsert = new SimpleJdbcInsert(dataSource).withTableName("user").usingGeneratedKeyColumns("id");
		this.simpleJdbcCall = new SimpleJdbcCall(dataSource);
	}

	// =====Create=====
	// @Override
	public int save(User user) {
		log.info("save(User user)");
		return _update(sql_insert_namedParam, user, null);
	}

	// @Override
	public Integer saveWithKey(User user) {
		log.info("saveWithKey(User user)");

		KeyHolder keyHolder = new GeneratedKeyHolder();
		int key = _update(sql_insert_namedParam, user, keyHolder);

		log.info("generatedKey=" + key);
		return key;
	}

	// @Override
	public int save(Map<String, ?> paramMap) {
		log.info("save(Map<String, ?> userMap)");
		return _update(sql_insert_namedParam, paramMap, null);
	}

	// @Override
	public Integer saveWithKey(Map<String, ?> userMap) {
		log.info("saveWithKey(Map<String, ?> userMap)");

		int key = _update(sql_insert_namedParam, userMap, new GeneratedKeyHolder());
		log.info("generatedKey=" + key);
		return key;
	}

	// @Override
	public int save(String name, int age, Date birthday) {
		log.info("save(String name, int age, Date birthday)");

		int rows = _update(sql_insert, name, age, birthday, null);
		return rows;
	}

	// @Override
	public Integer saveWithKey(String name, int age, Date birthday) {
		log.info("saveWithKey(String name, int age, Date birthday)");

		int key = _update(sql_insert, name, age, birthday, new GeneratedKeyHolder());
		log.info("generatedKey=" + key);
		return key;
	}

	// @Override
	public int saveAll(Iterable<User> users) {
		log.info("saveAll(Iterable<User> users)");

		int rows = 0;
		for (User user : users) {
			int row = save(user);
			rows += row;
		}
		return rows;
	}

	// =====Read 1=====
	// @Override
	public User findById(Integer id) {
		log.info("findById(Integer id)");

		Map<String, Integer> paramMap = new HashMap<String, Integer>();
		paramMap.put("id", id);

		User user = namedParameterJdbcTemplate.queryForObject(sql_select_by_id_namedParam1, paramMap, User.class);

		log.info("user=" + user);
		return user;
	}

	// @Override
	public Map<String, ?> findMapById(Integer id) {
		log.info("findMapById(Integer id)");

		Map<String, Integer> paramMap = new HashMap<String, Integer>();
		paramMap.put("id", id);
		Map<String, ?> columnMap = namedParameterJdbcTemplate.queryForMap(sql_select_by_id_namedParam2, paramMap);

		columnMap.forEach((k, v) -> log.info(k + "=" + v));
		return columnMap;
	}

	// @Override
	public boolean existsById(Integer id) {
		log.info("existsById(Integer id)");
		return findById(id) == null;
	}

	// @Override
	public List<User> findAll() {
		log.info("findAll()");

		List<User> userList = namedParameterJdbcTemplate.queryForList(sql_select_all1, EmptySqlParameterSource.INSTANCE,
				User.class);

		userList.forEach(user -> log.info("user=" + user));
		return userList;
	}

	// @Override
	public List<Map<String, Object>> findAllMap() {
		log.info("findAllMap()");

		List<Map<String, Object>> columnMapList = namedParameterJdbcTemplate.queryForList(sql_select_all2,
				EmptySqlParameterSource.INSTANCE);

		columnMapList.forEach(columbMap -> columbMap.forEach((k, v) -> log.info(k + "=" + v)));
		return columnMapList;
	}

	// @Override
	public List<User> findAllById(Integer id) {
		log.info("findAllById(Iterable<Integer> ids)");

		Map<String, Integer> paramMap = new HashMap<String, Integer>();
		paramMap.put("id", id);
		List<User> userList = namedParameterJdbcTemplate.queryForList(sql_select_by_gt_id_namedParam2, paramMap,
				User.class);

		userList.forEach(user -> log.info("user=" + user));
		return userList;
	}

	// @Override
	public List<Map<String, Object>> findAllMapById(Integer id) {
		log.info("findAllMapById(Integer id)");

		Map<String, Integer> paramMap = new HashMap<String, Integer>();
		paramMap.put("id", id);
		List<Map<String, Object>> columnMapList = namedParameterJdbcTemplate
				.queryForList(sql_select_by_gt_id_namedParam1, paramMap);

		columnMapList.forEach(columbMap -> columbMap.forEach((k, v) -> log.info(k + "=" + v)));
		return columnMapList;
	}

	// RowMapperResultSetExtractor & SingleColumnRowMapper
	// @Override
	public long count() {
		log.info("count()");

		Integer count = namedParameterJdbcTemplate.queryForObject(sql_select_count, EmptySqlParameterSource.INSTANCE,
				Integer.class);

		log.info("count=" + count);
		return count;
	}

	// =====Read 2=====
	// RowMapperResultSetExtractor & BeanPropertyRowMapper
	// @Override
	public User findById2(Integer id) {
		log.info("findById2(Integer id)");

		User user = jdbcTemplate.queryForObject(sql_select_by_id1, new Object[] { id }, User.class);

		log.info("user=" + user);
		return user;
	}

	// RowMapperResultSetExtractor & ColumnMapRowMapper
	// @Override
	public Map<String, ?> findMapById2(Integer id) {
		log.info("findMapById2(Integer id)");

		Map<String, Object> columnMap = jdbcTemplate.queryForMap(sql_select_by_id2, new Object[] { id });

		columnMap.forEach((k, v) -> log.info(k + "=" + v));
		return columnMap;
	}

	// @Override
	public boolean existsById2(Integer id) {
		log.info("existsById2(Integer id)");
		return findById2(id) == null;
	}

	// @Override
	public List<User> findAll2() {
		log.info("findAll2()");

		List<User> userList = jdbcTemplate.queryForList(sql_select_all1, User.class);

		userList.forEach(user -> log.info("user=" + user));
		return userList;
	}

	// @Override
	public List<Map<String, Object>> findAllMap2() {
		log.info("findAllMap2()");

		List<Map<String, Object>> columnMapList = jdbcTemplate.queryForList(sql_select_all2,
				EmptySqlParameterSource.INSTANCE);

		columnMapList.forEach(columbMap -> columbMap.forEach((k, v) -> log.info(k + "=" + v)));
		return columnMapList;
	}

	// @Override
	public List<User> findAllById2(Integer id) {
		log.info("findAllById2(Integer id)");

		List<User> userList = jdbcTemplate.queryForList(sql_select_by_gt_id2, new Object[] { id }, User.class);
		userList.forEach(user -> log.info("user1=" + user));

		PreparedStatementSetter pss0 = ps -> ps.setInt(1, id);
		userList = jdbcTemplate.query(sql_select_by_gt_id2, pss0, new BeanPropertyRowMapper<User>(User.class));
		userList.forEach(user -> log.info("user2" + user));

		return userList;
	}

	// @Override
	public List<Map<String, Object>> findAllMapById2(Integer id) {
		log.info("findAllMapById2(Integer id)");

		List<Map<String, Object>> columnMapList = jdbcTemplate.queryForList(sql_select_by_gt_id1, new Object[] { id });
		columnMapList.forEach(columbMap -> columbMap.forEach((k, v) -> log.info(k + "=" + v)));

		PreparedStatementSetter pss0 = ps -> ps.setInt(1, id);
		columnMapList = jdbcTemplate.queryForList(sql_select_by_gt_id1, pss0);
		columnMapList.forEach(columbMap -> log.info("user2" + columbMap));

		return columnMapList;
	}

	// RowMapperResultSetExtractor & SingleColumnRowMapper
	// @Override
	public long count2() {
		log.info("count2()");

		Integer count = jdbcTemplate.queryForObject(sql_select_count, Integer.class);

		log.info("count=" + count);
		return count;
	}

	// =====Update=====
	// @Override
	public int updateAge(User user) {
		log.info("updateAge(User user)");
		return _update(sql_update_age_namedParam, user, null);
	}

	// @Override
	public int updateAge(Map<String, ?> userMap) {
		log.info("updateAge(Map<String, ?> userMap)");
		return _update(sql_update_age_namedParam, userMap, null);
	}

	// @Override
	public int updateAge(String name, int age, Date birthday) {
		log.info("updateAge(String name, int age, Date birthday)");
		return _update(sql_update_age, name, age, birthday, null);
	}

	// =====Delete=====
	// @Override
	public int deleteById(Integer id) {
		log.info("deleteById(Integer id)");

		String sql = "delete from user where id = :id";
		Map<String, Integer> paramMap = new HashMap<String, Integer>();
		paramMap.put("id", id);

		return _update(sql, paramMap, null);
	}

	// @Override
	public int delete(User user) {
		log.info("delete(User user)");
		return _update(sql_delete_namedParam, user, null);
	}

	// @Override
	public int delete(Map<String, ?> mapUser) {
		log.info("delete(Map<String, ?> mapUser)");
		return _update(sql_delete_namedParam, mapUser, null);
	}

	// @Override
	public int delete(String name, int age, Date birthday) {
		log.info("delete(String name, int age, Date birthday)");
		return _update(sql_delete, name, age, birthday, null);
	}

	// @Override
	public int deleteAll(Iterable<? extends User> users) {
		log.info("deleteAll(Iterable<? extends User> users");

		int rows = 0;
		for (User user : users) {
			int row = delete(user);
			rows += row;
		}
		return rows;
	}

	// @Override
	public int deleteAll() {
		log.info("deleteAll()");
		return _update(sql_delete_all, Collections.emptyMap(), null);
	}

	// =====Batch Create=====
	// @Override
	public int[] batchSave(User[] users) {
		log.info("batchSave(User[] users)");
		return _batchUpdate(sql_insert_namedParam, users);
	}

	// @Override
	public int[] batchSave(Map<String, Object>[] users) {
		log.info("batchSave(Map<String, Object>[] users)");
		return _batchUpdate(sql_insert_namedParam, users);
	}

	// @Override
	public int[] batchSave(Collection<User> users) {
		log.info("batchSave(Collection<User> users)");
		return _batchUpdate(sql_insert_namedParam, users);
	}

	// @Override
	public int[] batchSave(List<Object[]> batchArgs) {
		log.info("batchSave(List<Object[]> batchArgs");
		return _batchUpdate(sql_insert, batchArgs);
	}

	// B=====atch Update=====
	// @Override
	public int[] batchUpdateAge(User[] users) {
		log.info("batchUpdateAge(User[] users)");
		return _batchUpdate(sql_update_age_namedParam, users);
	}

	// @Override
	public int[] batchUpdateAge(Collection<User> users) {
		log.info("batchUpdateAge(Collection<User> users)");
		return _batchUpdate(sql_update_age_namedParam, users);
	}

	// @Override
	public int[] batchUpdateAge(Map<String, Object>[] users) {
		log.info("batchUpdateAge(Map<String, User>[] users)");
		return _batchUpdate(sql_update_age_namedParam, users);
	}

	// @Override
	public int[] batchUpdateAge(List<Object[]> batchArgs) {
		log.info("batchUpdateAge(List<Object[]> batchArgs)");
		return _batchUpdate(sql_update_age, batchArgs);
	}

	// ======Batch Delete=====
	// @Override
	public int[] batchDelete(User[] users) {
		log.info("batchDelete(User[] users)");
		return _batchUpdate(sql_delete_namedParam, users);
	}

	// @Override
	public int[] batchDelete(Collection<User> users) {
		log.info("batchDelete(Collection<User> users)");
		return _batchUpdate(sql_delete_namedParam, users);
	}

	// @Override
	public int[] batchDelete(Map<String, Object>[] users) {
		log.info("batchDelete(Map<String, Object>[] users)");
		return _batchUpdate(sql_delete_namedParam, users);
	}

	// @Override
	public int[] batchDelete(List<Object[]> batchArgs) {
		log.info("batchDelete(List<Object[]> batchArgs)");
		return _batchUpdate(sql_delete, batchArgs);
	}

	// ====================private====================
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

		log.info("generatedKey=" + ret + ", count=" + ret);
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

		log.info("generatedKey=" + ret + ", count=" + ret);
		return ret;
	}

	private int _update(String sql, String name, int age, Date birthday, KeyHolder keyHolder) {
		log.info("_update(String sql, String name, int age, Date birthday, KeyHolder keyHolder)");

		PreparedStatementSetter pss = ps -> {
			ps.setString(1, name);
			ps.setInt(2, age);
			ps.setDate(3, birthday);
		};
		int updateCounts = jdbcTemplate.update(sql, pss);

		PreparedStatementCreator psc = conn -> {
			PreparedStatement ps = conn.prepareStatement(sql, new String[] { "id" });
			ps.setString(1, name);
			ps.setInt(2, age);
			ps.setDate(3, birthday);
			return ps;
		};
		if (keyHolder == null) {
			updateCounts = jdbcTemplate.update(psc);
		} else {
			jdbcTemplate.update(psc, keyHolder);
			updateCounts = keyHolder.getKey().intValue();
		}

		updateCounts = jdbcTemplate.update(sql, name, age, birthday);

		return updateCounts;
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

	private int[] _batchUpdate(List<Object[]> batchArgs) {
		log.info("_batchUpdate(List<Object[]> batchArgs");

		int[] updateCounts = jdbcTemplate.batchUpdate(sql_insert, batchArgs);

		BatchPreparedStatementSetter bpss = new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ps.setString(1, (String) batchArgs.get(i)[0]);
				ps.setInt(2, (Integer) batchArgs.get(i)[1]);
				ps.setDate(3, (Date) batchArgs.get(i)[2]);
			}

			@Override
			public int getBatchSize() {
				return batchArgs.size();
			}
		};
		updateCounts = jdbcTemplate.batchUpdate(sql_insert, bpss);

		ParameterizedPreparedStatementSetter<Object[]> uppss = (ps, args) -> {
			ps.setString(1, (String) args[0]);
			ps.setInt(2, (Integer) args[1]);
			ps.setDate(3, (Date) args[2]);
		};
		int[][] updateCounts2 = jdbcTemplate.batchUpdate(sql_insert, batchArgs, 2, uppss);

		return updateCounts;
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
		save(user);

		// REQUIRES_NEW，理论上这个方法不回滚
		transactionalMethod2(new User("ruanwei_tmp", 2, Date.valueOf("1983-07-06")));

		int i = 1 / 0;
	}

	// 不能在事务方法中进行try-catch
	public void transactionalMethod2(User user) {
		save(user);
	}

}
