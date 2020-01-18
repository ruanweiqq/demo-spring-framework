package org.ruanwei.demo.springframework.dataAccess.jdbc;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ruanwei.demo.springframework.dataAccess.User;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
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
public class UserJdbcDao {
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

	private static final String sql_11 = "select name from user where id = ?";
	private static final String sql_12 = "select name from user where id = :id";

	private static final String sql_21 = "select name, age from user where id = ?";
	private static final String sql_22 = "select name, age from user where id = :id";

	private static final String sql_31 = "select * from user where id = ?";
	private static final String sql_32 = "select * from user where id = :id";

	private static final String sql_41 = "select name from user where id > ?";
	private static final String sql_42 = "select name from user where id > :id";

	private static final String sql_51 = "select name, age from user where id > ?";
	private static final String sql_52 = "select name, age from user where id > :id";

	private static final String sql_61 = "select * from user where id > ?";
	private static final String sql_62 = "select * from user where id > :id";

//	private static final String sql_71 = "insert into user(name,age,birthday) values(\"ruanwei\", 35, \"1983-07-06\")";
	private static final String sql_insert = "insert into user(name,age,birthday) values(?, ?, ?)";
	private static final String sql_insert_namedParam = "insert into user(name,age,birthday) values(:name, :age, :birthday)";

	private static final String sql_update = "update user set age = ? where name = ?";
	private static final String sql_update_namedParam = "update user set age = :age where name = :name";

	private static final String sql_delete = "delete from user where name = ?";
	private static final String sql_delete_namedParam = "delete from user where name = :name";

	@Required
	public void setDataSource(@Qualifier("jdbcDataSource") DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);

		this.simpleJdbcInsert = new SimpleJdbcInsert(dataSource).withTableName("user").usingGeneratedKeyColumns("id");
		this.simpleJdbcCall = new SimpleJdbcCall(dataSource);
	}

	// ====================single row====================
	// RowMapperResultSetExtractor & ColumnMapRowMapper
	public Map<String, Object> queryForSingleRowAsColumnMap(int id) {
		log.info("queryForSingleRowAsColumnMap(int id)");

		Map<String, Object> columnMap1 = jdbcTemplate.queryForMap(sql_21, new Object[] { id });

		Map<String, Integer> paramMap = new HashMap<String, Integer>();
		paramMap.put("id", id);
		Map<String, Object> columnMap2 = namedParameterJdbcTemplate.queryForMap(sql_22, paramMap);

		columnMap1.forEach((k, v) -> log.info(k + "=" + v));
		columnMap2.forEach((k, v) -> log.info(k + "=" + v));

		return columnMap2;
	}

	// RowMapperResultSetExtractor & SingleColumnRowMapper
	public String queryForSingleRowWithSingleColumn(int id) {
		log.info("queryForSingleRowWithSingleColumn(int id)");

		String column1 = jdbcTemplate.queryForObject(sql_11, new Object[] { id }, String.class);

		Map<String, Integer> paramMap = new HashMap<String, Integer>();
		paramMap.put("id", id);
		String column2 = namedParameterJdbcTemplate.queryForObject(sql_12, paramMap, String.class);

		log.info("column1=" + column1 + ",column2=" + column2);
		return column2;
	}

	// RowMapperResultSetExtractor & BeanPropertyRowMapper
	public User queryForSingleRowAsBeanProperty(int id) {
		log.info("queryForSingleRowAsBeanProperty(int id)");

		User user1 = jdbcTemplate.queryForObject(sql_31, new Object[] { id },
				new BeanPropertyRowMapper<User>(User.class));

		Map<String, Integer> paramMap = new HashMap<String, Integer>();
		paramMap.put("id", id);
		User user2 = namedParameterJdbcTemplate.queryForObject(sql_32, paramMap,
				new BeanPropertyRowMapper<User>(User.class));

		log.info("user1=" + user1 + ",user2=" + user2);
		return user2;
	}

	// ====================multiple row====================
	public List<Map<String, Object>> queryForListWithColumnMap(int largerThanId) {
		log.info("queryForListWithColumnMap(int largerThanId)");

		List<Map<String, Object>> columnMapList1 = jdbcTemplate.queryForList(sql_51, new Object[] { largerThanId });

		Map<String, Integer> paramMap = new HashMap<String, Integer>();
		paramMap.put("id", largerThanId);
		List<Map<String, Object>> columnMapList2 = namedParameterJdbcTemplate.queryForList(sql_52, paramMap);

		columnMapList1.forEach(columbMap -> columbMap.forEach((k, v) -> log.info(k + "=" + v)));
		columnMapList2.forEach(columbMap -> columbMap.forEach((k, v) -> log.info(k + "=" + v)));
		return columnMapList2;
	}

	public List<String> queryForListWithSingleColumn(int largerThanId) {
		log.info("queryForListWithSingleColumn(int largerThanId)");

		List<String> columnList1 = jdbcTemplate.queryForList(sql_41, new Object[] { largerThanId }, String.class);

		Map<String, Integer> paramMap = new HashMap<String, Integer>();
		paramMap.put("id", largerThanId);
		List<String> columnList2 = namedParameterJdbcTemplate.queryForList(sql_42, paramMap, String.class);

		columnList1.forEach(column -> log.info("column=" + column));
		columnList2.forEach(column -> log.info("column=" + column));
		return columnList2;
	}

	public List<User> queryForListWithBeanProperty(int largerThanId) {
		log.info("queryForListWithBeanProperty(int largerThanId)");

		List<User> objList1 = jdbcTemplate.query(sql_61, new Object[] { largerThanId },
				new BeanPropertyRowMapper<User>(User.class));

		PreparedStatementSetter pss0 = ps -> ps.setLong(1, 0L);
		List<User> objList2 = jdbcTemplate.query(sql_61, pss0, new BeanPropertyRowMapper<User>(User.class));

		Map<String, Integer> paramMap = new HashMap<String, Integer>();
		paramMap.put("id", largerThanId);
		List<User> objList3 = namedParameterJdbcTemplate.query(sql_62, paramMap,
				new BeanPropertyRowMapper<User>(User.class));

		objList1.forEach(obj -> log.info("obj=" + obj));
		objList2.forEach(obj -> log.info("obj=" + obj));
		objList3.forEach(obj -> log.info("obj=" + obj));
		return objList3;
	}

	// ===================Part 1=============================
	// ====================update====================
	private int _update(String sql, Object candidate, KeyHolder keyHolder) {
		log.info("_update(String sql, Object candidate, KeyHolder keyHolder)");

		int ret;
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

		int ret;
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

	// Create
	public int create(User user) {
		log.info("create(User user)");
		return _update(sql_insert_namedParam, user, null);
	}

	public int create(Map<String, ?> user) {
		log.info("create(Map<String, ?> user)");
		return _update(sql_insert_namedParam, user, null);
	}

	// Create with key
	public int createWithKey(User user) {
		log.info("createWithKey(User user)");

		KeyHolder keyHolder = new GeneratedKeyHolder();
		int key = _update(sql_insert_namedParam, user, keyHolder);
		log.info("generatedKey=" + key);
		return key;
	}

	public int createWithKey(Map<String, ?> user) {
		log.info("createWithKey(Map<String, ?> user)");

		KeyHolder keyHolder = new GeneratedKeyHolder();
		int key = _update(sql_insert_namedParam, user, keyHolder);
		log.info("generatedKey=" + key);
		return key;
	}

	// Update
	public int update(User user) {
		log.info("update(User user)");
		return _update(sql_update_namedParam, user, null);
	}

	public int update(Map<String, ?> user) {
		log.info("update(Map<String, ?> user)");
		return _update(sql_update_namedParam, user, null);
	}

	// Delete
	public int delete(User user) {
		log.info("delete(User user)");
		return _update(sql_delete_namedParam, user, null);
	}

	public int delete(Map<String, ?> user) {
		log.info("delete(Map<String, ?> user)");
		return _update(sql_delete_namedParam, user, null);
	}

	// Batch Create
	public int[] batchCreate(User... users) {
		log.info("batchCreate(User... users)");
		return _batchUpdate(sql_insert_namedParam, users);
	}

	// Batch Update
	public int[] batchUpdate(Map<String, Object>[] users) {
		log.info("batchUpdate(Map<String, User>[] users)");
		return _batchUpdate(sql_update_namedParam, users);
	}

	// Batch Delete
	public int[] batchDelete(Collection<User> users) {
		log.info("batchDelete(Collection<User> users)");
		return _batchUpdate(sql_delete_namedParam, users);
	}

	// ===================Part 2=============================
	// Create/Update/Delete
	public int createUser1(User user) {
		log.info("createUser1(User user)" + user);
		return jdbcTemplate.update(sql_insert, user.getName(), user.getAge(), user.getBirthday());
	}

	public int createUser2(User user) {
		log.info("createUser2(User user)");

		PreparedStatementSetter pss = ps -> {
			ps.setString(1, user.getName());
			ps.setInt(2, user.getAge());
			ps.setDate(3, user.getBirthday());
		};
		return jdbcTemplate.update(sql_insert, pss);
	}

	public int createUserWithKey1(User user) {
		log.info("createUserWithKey1(User user)");

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

	// Batch Create/Update/Delete
	public int[] batchCreateUser1(List<User> users) {
		log.info("batchCreateUser1(List<User> users)");

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

	public int[][] batchCreateUser2(Collection<User> users) {
		log.info("batchCreateUser2(Collection<User> users)");

		ParameterizedPreparedStatementSetter<User> uppss = (ps, user) -> {
			ps.setString(1, user.getName());
			ps.setInt(2, user.getAge());
			ps.setDate(3, user.getBirthday());
		};

		int[][] updateCounts = jdbcTemplate.batchUpdate(sql_insert, users, 2, uppss);
		return updateCounts;
	}

	public int[] batchCreateUser3(List<User> users) {
		log.info("batchCreateUser3(List<User> users)");
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
		createUser1(user);

		// REQUIRES_NEW，理论上这个方法不回滚
		transactionalMethod2(new User("ruanwei_tmp", 2, Date.valueOf("1983-07-06")));

		int i = 1 / 0;
	}

	// 不能在事务方法中进行try-catch
	public void transactionalMethod2(User user) {
		createUser1(user);
	}

	// ====================private====================

}
