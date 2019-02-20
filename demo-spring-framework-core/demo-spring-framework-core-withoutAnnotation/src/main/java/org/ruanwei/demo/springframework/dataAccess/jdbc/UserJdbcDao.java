package org.ruanwei.demo.springframework.dataAccess.jdbc;

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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ParameterizedPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
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
	private AdvancedJdbcTemplate advancedJdbcTemplate;

	// 2.core SimpleJdbc classes
	private SimpleJdbcInsert simpleJdbcInsert;
	private SimpleJdbcCall simpleJdbcCall;// 执行存储过程或者函数

	// 3.RdbmsOperation objects.
	private SqlQuery<User> sqlQuery;
	private MappingSqlQuery<User> mappingSqlQuery;
	private UpdatableSqlQuery<User> updatableSqlQuery;
	private SqlUpdate sqlUpdate;
	private StoredProcedure storedProcedure;

	private static final String sql_11 = "select name from user where id = 1";
	private static final String sql_12 = "select name from user where id = ?";
	private static final String sql_13 = "select name from user where id = :id";

	private static final String sql_21 = "select name, age from user where id = 1";
	private static final String sql_22 = "select name, age from user where id = ?";
	private static final String sql_23 = "select name, age from user where id = :id";

	private static final String sql_31 = "select * from user where id = 1";
	private static final String sql_32 = "select * from user where id = ?";
	private static final String sql_33 = "select * from user where id = :id";

	private static final String sql_41 = "select name from user where id > 0";
	private static final String sql_42 = "select name from user where id > ?";
	private static final String sql_43 = "select name from user where id > :id";

	private static final String sql_51 = "select name, age from user where id > 0";
	private static final String sql_52 = "select name, age from user where id > ?";
	private static final String sql_53 = "select name, age from user where id > :id";

	private static final String sql_61 = "select * from user where id > 0";
	private static final String sql_62 = "select * from user where id > ?";
	private static final String sql_63 = "select * from user where id > :id";

	private static final String sql_71 = "insert into user(name,age,birthday) values(\"ruanwei\", 35, \"1983-07-06\")";
	private static final String sql_72 = "insert into user(name,age,birthday) values(?, ?, ?)";
	private static final String sql_73 = "insert into user(name,age,birthday) values(:name, :age, :birthday)";

	private static final String sql_81 = "update user set age = 18 where name = 'ruanwei'";
	private static final String sql_82 = "update user set age = ? where name = ?";
	private static final String sql_83 = "update user set age = :age where name = :name";

	private static final String sql_9 = "delete from user where id > ?";

	private static final PreparedStatementSetter pss0 = ps -> ps.setLong(1, 0L);

	@Required
	@Autowired
	public void setDataSource(@Qualifier("jdbcDataSource") DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		this.advancedJdbcTemplate = new AdvancedJdbcTemplate(dataSource);

		this.simpleJdbcInsert = new SimpleJdbcInsert(dataSource).withTableName("user").usingGeneratedKeyColumns("id");
		this.simpleJdbcCall = new SimpleJdbcCall(dataSource);
	}

	// ====================single row====================
	// RowMapperResultSetExtractor & SingleColumnRowMapper
	public void queryForSingleRowWithSingleColumn(int id) {
		String column1 = jdbcTemplate.queryForObject(sql_11, String.class);
		String column2 = jdbcTemplate.queryForObject(sql_12, new Object[] { id }, String.class);

		Map<String, Integer> namedParam = new HashMap<String, Integer>();
		namedParam.put("id", id);
		String column3 = namedParameterJdbcTemplate.queryForObject(sql_13, namedParam, String.class);
		log.info("column1=" + column1 + ",column2=" + column2 + ",column3=" + column3);
	}

	// RowMapperResultSetExtractor & ColumnMapRowMapper
	public void queryForSingleRowAsColumnMap(int id) {
		Map<String, Object> columnMap1 = jdbcTemplate.queryForMap(sql_21);
		Map<String, Object> columnMap2 = jdbcTemplate.queryForMap(sql_22, new Object[] { id });

		Map<String, Integer> namedParam = new HashMap<String, Integer>();
		namedParam.put("id", id);
		Map<String, Object> columnMap3 = namedParameterJdbcTemplate.queryForMap(sql_23, namedParam);
		columnMap1.forEach((k, v) -> log.info(k + "=" + v));
		columnMap2.forEach((k, v) -> log.info(k + "=" + v));
		columnMap3.forEach((k, v) -> log.info(k + "=" + v));
	}

	// RowMapperResultSetExtractor & BeanPropertyRowMapper
	public void queryForSingleRowAsBeanProperty(int id) {
		User obj1 = advancedJdbcTemplate.queryForObject(sql_31, User.class);
		User obj2 = advancedJdbcTemplate.queryForObject(sql_32, new Object[] { id }, User.class);

		Map<String, Integer> namedParam = new HashMap<String, Integer>();
		namedParam.put("id", id);
		User obj3 = advancedJdbcTemplate.queryForObject(sql_33, namedParam, User.class);
		log.info("obj1=" + obj1 + ",obj2=" + obj2 + ",obj3=" + obj3);
	}

	// ====================multiple row====================
	public void queryForListWithSingleColumn(int largerThanId) {
		List<String> columnList1 = jdbcTemplate.queryForList(sql_41, String.class);
		List<String> columnList2 = jdbcTemplate.queryForList(sql_42, new Object[] { largerThanId }, String.class);

		Map<String, Integer> namedParam = new HashMap<String, Integer>();
		namedParam.put("id", largerThanId);
		List<String> columnList3 = namedParameterJdbcTemplate.queryForList(sql_43, namedParam, String.class);
		columnList1.forEach(column -> log.info("column=" + column));
		columnList2.forEach(column -> log.info("column=" + column));
		columnList3.forEach(column -> log.info("column=" + column));
	}

	public void queryForListWithColumnMap(int largerThanId) {
		List<Map<String, Object>> columnMapList1 = jdbcTemplate.queryForList(sql_51);
		List<Map<String, Object>> columnMapList2 = jdbcTemplate.queryForList(sql_52, new Object[] { largerThanId });

		Map<String, Integer> namedParam = new HashMap<String, Integer>();
		namedParam.put("id", largerThanId);
		List<Map<String, Object>> columnMapList3 = namedParameterJdbcTemplate.queryForList(sql_53, namedParam);

		columnMapList1.forEach(columbMap -> columbMap.forEach((k, v) -> log.info(k + "=" + v)));
		columnMapList2.forEach(columbMap -> columbMap.forEach((k, v) -> log.info(k + "=" + v)));
		columnMapList3.forEach(columbMap -> columbMap.forEach((k, v) -> log.info(k + "=" + v)));
	}

	public void queryForListWithBeanProperty(int largerThanId) {
		List<User> objList1 = advancedJdbcTemplate.queryForObjectList(sql_61, User.class);
		List<User> objList2 = advancedJdbcTemplate.queryForObjectList(sql_62, new Object[] { largerThanId },
				User.class);
		List<User> objList3 = advancedJdbcTemplate.queryForObjectList(sql_62, pss0, User.class);

		Map<String, Integer> namedParam = new HashMap<String, Integer>();
		namedParam.put("id", largerThanId);
		List<User> objList4 = advancedJdbcTemplate.queryForObjectList(sql_63, namedParam, User.class);
		objList1.forEach(obj -> log.info("obj=" + obj));
		objList2.forEach(obj -> log.info("obj=" + obj));
		objList3.forEach(obj -> log.info("obj=" + obj));
		objList4.forEach(obj -> log.info("obj=" + obj));
	}

	// ====================update====================
	// create/update/delete都是调用update方法
	public int createUser1(User user) {
		log.info("createUser1(User user)" + user);

		int count = jdbcTemplate.update(sql_72, user.getName(), user.getAge(), user.getBirthday());
		return count;
	}

	public int createUser2(User user) {
		log.info("createUser2(User user)");

		PreparedStatementSetter upss = buildUserPSS(user);
		int count = jdbcTemplate.update(sql_72, upss);
		return count;
	}

	public int createUser3(User user) {
		log.info("createUser3(User user)");

		KeyHolder keyHolder = new GeneratedKeyHolder();
		PreparedStatementCreator upsc = buildUserPSC(sql_72, user);
		int count = jdbcTemplate.update(upsc, keyHolder);
		log.info("generatedKey=" + keyHolder.getKey().longValue());
		return count;
	}

	public <T> int createUser4(T t) {
		log.info("createUser4(T t)");

		SqlParameterSource sqlParamSource = SqlParameterSourceUtils.createBatch(t)[0];
		int count = namedParameterJdbcTemplate.update(sql_73, sqlParamSource);
		return count;
	}

	public <T> int createUser5(T t) {
		log.info("createUser5(T t)");

		KeyHolder keyHolder = new GeneratedKeyHolder();
		SqlParameterSource sqlParamSource = SqlParameterSourceUtils.createBatch(t)[0];

		int count = namedParameterJdbcTemplate.update(sql_73, sqlParamSource, keyHolder);
		log.info("generatedKey=" + keyHolder.getKey().longValue());
		return count;
	}

	// ====================batch update====================
	public int[] batchUpdateUser1(final List<User> users) {
		log.info("batchUpdateUser1(final List<User> users)");

		BatchPreparedStatementSetter ubpss = buildUserBPSS(users);
		int[] updateCounts = jdbcTemplate.batchUpdate(sql_82, ubpss);
		return updateCounts;
	}

	public int[][] batchUpdateUser2(final Collection<User> users) {
		log.info("batchUpdateUser2(final List<User> users)");

		ParameterizedPreparedStatementSetter<User> uppss = buildUserPPSS();
		int[][] updateCounts = jdbcTemplate.batchUpdate(sql_82, users, 2, uppss);
		return updateCounts;
	}

	public <T> int[] batchUpdateUser3(final Collection<T> list) {
		log.info("batchUpdateUser3(final List<User> users)");

		SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(list);
		int[] updateCounts = namedParameterJdbcTemplate.batchUpdate(sql_83, batch);
		return updateCounts;
	}

	public <T> int[] batchUpdateUser4(final Map<String, ?>... batchValues) {
		log.info("batchUpdateUser4(final Map<String, ?>... batchValues)");

		SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(batchValues);
		int[] updateCounts = namedParameterJdbcTemplate.batchUpdate(sql_83, batch);
		return updateCounts;
	}

	public int[] batchUpdateUser5(final List<User> users) {
		log.info("batchUpdateUser5(final List<User> users)");
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

	public int deleteUser(int largerThanId) {
		log.info("deleteUser(int largerThanId)");

		int count = jdbcTemplate.update(sql_9, largerThanId);
		return count;
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
						new SqlOutParameter("out_first_name", Types.VARCHAR),
						new SqlOutParameter("out_last_name", Types.VARCHAR),
						new SqlOutParameter("out_birth_date", Types.DATE));
		Map<String, Object> out = simpleJdbcCall.execute(in);
		User user = new User();
		user.setName((String) out.get("out_first_name"));
		user.setAge((int) out.get("age"));
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
	public void transactionalMethod(User... users) {
		createUser1(users[0]);
		createUser1(users[1]);

		transactionalSubMethod(users[2], users[3]);

		int i = 1 / 0;
	}

	// 不能在事务方法中进行try-catch
	private void transactionalSubMethod(User... users) {
		createUser1(users[0]);
		createUser1(users[1]);
	}

	// ====================private====================
	private PreparedStatementSetter buildUserPSS(User user) {
		return ps -> {
			ps.setString(1, user.getName());
			ps.setInt(2, user.getAge());
			ps.setDate(3, user.getBirthday());
		};
	}

	private BatchPreparedStatementSetter buildUserBPSS(List<User> users) {
		return new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ps.setInt(1, users.get(i).getAge());
				ps.setString(2, users.get(i).getName());
			}

			@Override
			public int getBatchSize() {
				return users.size();
			}
		};
	}

	private ParameterizedPreparedStatementSetter<User> buildUserPPSS() {
		return (ps, arg) -> {
			ps.setInt(1, arg.getAge());
			ps.setString(2, arg.getName());
		};
	}

	private PreparedStatementCreator buildUserPSC(String sql, User user) {
		return conn -> {
			PreparedStatement ps = conn.prepareStatement(sql, new String[] { "id" });
			ps.setString(1, user.getName());
			ps.setInt(2, user.getAge());
			ps.setDate(3, user.getBirthday());
			return ps;
		};
	}
}
