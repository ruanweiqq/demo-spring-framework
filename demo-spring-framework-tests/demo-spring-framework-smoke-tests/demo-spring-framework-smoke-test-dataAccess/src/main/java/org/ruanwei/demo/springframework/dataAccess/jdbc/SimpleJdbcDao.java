package org.ruanwei.demo.springframework.dataAccess.jdbc;

import java.lang.reflect.ParameterizedType;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ruanwei.demo.springframework.dataAccess.TransactionalDao;
import org.ruanwei.demo.springframework.dataAccess.jdbc.entity.UserJdbcEntity;
import org.ruanwei.demo.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
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
import org.springframework.transaction.annotation.Transactional;

/**
 * JdbcDaoSupport提供了setDataSource支持 NamedParameterJdbcTemplate支持IN表达式
 * 
 * @author ruanwei
 *
 */
@Transactional("transactionManager")
public class SimpleJdbcDao<T, ID> implements JdbcDao<T, ID> {
	private static Log log = LogFactory.getLog(SimpleJdbcDao.class);

	// 1.core JdbcTemplate & NamedParameterJdbcTemplate thread-safe
	private JdbcTemplate jdbcTemplate;
	// named parameters instead of the traditional JDBC "?" placeholders.
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	// 2.core SimpleJdbc classes
	private SimpleJdbcInsert simpleJdbcInsert;
	private SimpleJdbcCall simpleJdbcCall;// 执行存储过程或者函数

	// 3.RdbmsOperation objects.
	private SqlQuery<UserJdbcEntity> sqlQuery;
	private MappingSqlQuery<UserJdbcEntity> mappingSqlQuery;
	private UpdatableSqlQuery<UserJdbcEntity> updatableSqlQuery;
	private SqlUpdate sqlUpdate;
	private StoredProcedure storedProcedure;

	private Entity<T> entity;

	class Entity<T> {

	}

	@Autowired
	private TransactionalDao<UserJdbcEntity> userTransactionnalJdbcDao;

	@Autowired
	private ApplicationEventPublisher applicationEventPublisher;

	private static final String sql_select_by_id_namedParam1 = "select * from user where id = :id";

	private static final String sql_select_by_id_namedParam2 = "select id, name, age, birthday from user where id = :id";

	private static final String sql_select_by_ids_namedParam1 = "select * from user where id in (:ids)";

	private static final String sql_select_by_gt_id_namedParam1 = "select id, name, age, birthday from user where id > :id";

	private static final String sql_select_by_gt_id_namedParam2 = "select * from user where id > :id";

	private static final String sql_select_all1 = "select * from user";
	private static final String sql_select_all2 = "select id, name, age, birthday from user";

	private static final String sql_select_count = "select count(*) from user";

	private static final String sql_insert = "insert into user(name,age,birthday) values(?, ?, ?)";
	private static final String sql_insert_namedParam = "insert into user(name,age,birthday) values(:name, :age, :birthday)";

	private static final String sql_update_age = "update user set age = ? where name = ? and birthday = ?";
	private static final String sql_update_age_namedParam = "update user set age = :age where name = :name and birthday = :birthday";

	private static final String sql_delete = "delete from user where name = ? and age = ? and birthday = ?";
	private static final String sql_delete_namedParam = "delete from user where name = :name and age = :age and birthday = :birthday";

	private static final String sql_delete_all = "delete from user";

	@Autowired
	public void setDataSource(@Qualifier("springDataSource") DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);

		this.simpleJdbcInsert = new SimpleJdbcInsert(dataSource).withTableName("user").usingGeneratedKeyColumns("id");
		this.simpleJdbcCall = new SimpleJdbcCall(dataSource);
	}

	// =====Create=====
	@Override
	public int save(T user) {
		log.info("save(T user)");
		return _update(sql_insert_namedParam, user, null);
	}

	@Override
	public int saveWithKey(T user) {
		log.info("saveWithKey(T user)");

		KeyHolder keyHolder = new GeneratedKeyHolder();
		int key = _update(sql_insert_namedParam, user, keyHolder);

		// see @TransactionalEventListener
		applicationEventPublisher.publishEvent(new SaveEvent<T, Integer>(user, key));

		log.info("generatedKey=" + key);
		return key;
	}

	@Override
	public int save(Map<String, ?> paramMap) {
		log.info("save(Map<String, ?> userMap)");
		return _update(sql_insert_namedParam, paramMap, null);
	}

	@Override
	public int saveWithKey(Map<String, ?> userMap) {
		log.info("saveWithKey(Map<String, ?> userMap)");

		int key = _update(sql_insert_namedParam, userMap, new GeneratedKeyHolder());
		log.info("generatedKey=" + key);
		return key;
	}

	@Override
	public int save(String name, int age, Date birthday) {
		log.info("save(String name, int age, Date birthday)");

		int rows = _update(sql_insert, name, age, birthday, null);
		return rows;
	}

	@Override
	public int saveWithKey(String name, int age, Date birthday) {
		log.info("saveWithKey(String name, int age, Date birthday)");

		int key = _update(sql_insert, name, age, birthday, new GeneratedKeyHolder());
		log.info("generatedKey=" + key);
		return key;
	}

	@Override
	public int saveAll(Iterable<T> users) {
		log.info("saveAll(Iterable<ID> users)");

		int rows = 0;
		for (T user : users) {
			int row = save(user);
			rows += row;
		}
		return rows;
	}

	// =====Read=====
	@Transactional(readOnly = true)
	@Override
	public Optional<T> findById(ID id) {
		log.info("findById(ID id)");

		Map<String, ID> paramMap = new HashMap<String, ID>();
		paramMap.put("id", id);

		T user = namedParameterJdbcTemplate.queryForObject(sql_select_by_id_namedParam1, paramMap,
				new BeanPropertyRowMapper<T>(getTClass()));

		log.info("user=" + user);
		return Optional.ofNullable(user);
	}

	@Transactional(readOnly = true)
	@Override
	public Map<String, ?> findMapById(ID id) {
		log.info("findMapById(ID id)");

		Map<String, ID> paramMap = new HashMap<String, ID>();
		paramMap.put("id", id);
		Map<String, ?> columnMap = namedParameterJdbcTemplate.queryForMap(sql_select_by_id_namedParam2, paramMap);

		columnMap.forEach((k, v) -> log.info(k + "=" + v));
		return columnMap;
	}

	@Transactional(readOnly = true)
	@Override
	public boolean existsById(ID id) {
		log.info("existsById(ID id)");
		return findById(id) != null;
	}

	@Transactional(readOnly = true)
	@Override
	public List<T> findAll() {
		log.info("findAll()");

		List<T> userList = namedParameterJdbcTemplate.query(sql_select_all1, EmptySqlParameterSource.INSTANCE,
				new BeanPropertyRowMapper<T>(getTClass()));

		userList.forEach(user -> log.info("user=" + user));
		return userList;
	}

	@Transactional(readOnly = true)
	@Override
	public List<Map<String, Object>> findAllMap() {
		log.info("findAllMap()");

		List<Map<String, Object>> columnMapList = namedParameterJdbcTemplate.queryForList(sql_select_all2,
				EmptySqlParameterSource.INSTANCE);

		columnMapList.forEach(columbMap -> columbMap.forEach((k, v) -> log.info(k + "=" + v)));
		return columnMapList;
	}

	@Transactional(readOnly = true)
	@Override
	public List<T> findAllById(Iterable<ID> ids) {
		log.info("findAllById(Iterable<Integer> ids)");

		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("ids", StringUtils.toString(ids));
		List<T> userList = namedParameterJdbcTemplate.query(sql_select_by_ids_namedParam1, paramMap,
				new BeanPropertyRowMapper<T>(getTClass()));

		userList.forEach(user -> log.info("user=" + user));
		return userList;
	}

	@Transactional(readOnly = true)
	@Override
	public List<T> findAllByGtId(ID id) {
		log.info("findAllByGtId(ID id)");

		Map<String, ID> paramMap = new HashMap<String, ID>();
		paramMap.put("id", id);
		List<T> userList = namedParameterJdbcTemplate.query(sql_select_by_gt_id_namedParam2, paramMap,
				new BeanPropertyRowMapper<T>(getTClass()));

		userList.forEach(user -> log.info("user=" + user));
		return userList;
	}

	@Transactional(readOnly = true)
	@Override
	public List<Map<String, Object>> findAllMapById(ID id) {
		log.info("findAllMapById(Integer id)");

		Map<String, ID> paramMap = new HashMap<String, ID>();
		paramMap.put("id", id);
		List<Map<String, Object>> columnMapList = namedParameterJdbcTemplate
				.queryForList(sql_select_by_gt_id_namedParam1, paramMap);

		columnMapList.forEach(columbMap -> columbMap.forEach((k, v) -> log.info(k + "=" + v)));
		return columnMapList;
	}

	// RowMapperResultSetExtractor & SingleColumnRowMapper
	@Transactional(readOnly = true)
	@Override
	public long count() {
		log.info("count()");

		Integer count = namedParameterJdbcTemplate.queryForObject(sql_select_count, EmptySqlParameterSource.INSTANCE,
				Integer.class);

		log.info("count=" + count);
		return count;
	}

	// =====Update=====
	@Override
	public int updateAge(T user) {
		log.info("updateAge(UserJdbcEntity user)");
		return _update(sql_update_age_namedParam, user, null);
	}

	@Override
	public int updateAge(Map<String, ?> userMap) {
		log.info("updateAge(Map<String, ?> userMap)");
		return _update(sql_update_age_namedParam, userMap, null);
	}

	@Override
	public int updateAge(String name, int age, Date birthday) {
		log.info("updateAge(String name, int age, Date birthday)");
		return _update(sql_update_age, name, age, birthday, null);
	}

	// =====Delete=====
	@Override
	public int deleteById(ID id) {
		log.info("deleteById(Integer id)");

		String sql = "delete from user where id = :id";
		Map<String, ID> paramMap = new HashMap<String, ID>();
		paramMap.put("id", id);

		return _update(sql, paramMap, null);
	}

	@Override
	public int delete(T user) {
		log.info("delete(UserJdbcEntity user)");
		return _update(sql_delete_namedParam, user, null);
	}

	@Override
	public int delete(Map<String, ?> mapUser) {
		log.info("delete(Map<String, ?> mapUser)");
		return _update(sql_delete_namedParam, mapUser, null);
	}

	@Override
	public int delete(String name, int age, Date birthday) {
		log.info("delete(String name, int age, Date birthday)");
		return _update(sql_delete, name, age, birthday, null);
	}

	@Override
	public int deleteAll(Iterable<T> users) {
		log.info("deleteAll(Iterable<UserJdbcEntity> users");

		int rows = 0;
		for (T user : users) {
			int row = delete(user);
			rows += row;
		}
		return rows;
	}

	@Override
	public int deleteAll() {
		log.info("deleteAll()");
		return _update(sql_delete_all, Collections.emptyMap(), null);
	}

	// =====Batch Create=====
	@Override
	public int[] batchSave(T[] users) {
		log.info("batchSave(UserJdbcEntity[] users)");
		return _batchUpdate(sql_insert_namedParam, users);
	}

	@Override
	public int[] batchSave(Map<String, Object>[] users) {
		log.info("batchSave(Map<String, Object>[] users)");
		return _batchUpdate(sql_insert_namedParam, users);
	}

	@Override
	public int[] batchSave(Collection<T> users) {
		log.info("batchSave(Collection<UserJdbcEntity> users)");
		return _batchUpdate(sql_insert_namedParam, users);
	}

	@Override
	public int[] batchSave(List<Object[]> batchArgs) {
		log.info("batchSave(List<Object[]> batchArgs");
		return _batchUpdate(sql_insert, batchArgs);
	}

	// B=====atch Update=====
	@Override
	public int[] batchUpdateAge(T[] users) {
		log.info("batchUpdateAge(T[] users)");
		return _batchUpdate(sql_update_age_namedParam, users);
	}

	@Override
	public int[] batchUpdateAge(Collection<T> users) {
		log.info("batchUpdateAge(Collection<T> users)");
		return _batchUpdate(sql_update_age_namedParam, users);
	}

	@Override
	public int[] batchUpdateAge(Map<String, Object>[] users) {
		log.info("batchUpdateAge(Map<String, Object>[] users)");
		return _batchUpdate(sql_update_age_namedParam, users);
	}

	@Override
	public int[] batchUpdateAge(List<Object[]> batchArgs) {
		log.info("batchUpdateAge(List<Object[]> batchArgs)");
		return _batchUpdate(sql_update_age, batchArgs);
	}

	// ======Batch Delete=====
	@Override
	public int[] batchDelete(T[] users) {
		log.info("batchDelete(T[] users)");
		return _batchUpdate(sql_delete_namedParam, users);
	}

	@Override
	public int[] batchDelete(Collection<T> users) {
		log.info("batchDelete(Collection<T> users)");
		return _batchUpdate(sql_delete_namedParam, users);
	}

	@Override
	public int[] batchDelete(Map<String, Object>[] users) {
		log.info("batchDelete(Map<String, Object>[] users)");
		return _batchUpdate(sql_delete_namedParam, users);
	}

	@Override
	public int[] batchDelete(List<Object[]> batchArgs) {
		log.info("batchDelete(List<Object[]> batchArgs)");
		return _batchUpdate(sql_delete, batchArgs);
	}

	// =====transaction=====
	// 1.事务是默认在抛出运行时异常进行回滚的，因此不能在事务方法中进行try-catch捕获
	// 2.事务是通过代理目标对象实现的，因此只有调用代理的事务方法才生效，调用目标对象(例如同一类中的其他方法)没有事务
	// 3.由于事务传播类型不同，transactionalMethod1会回滚，transactionalMethod2不会回滚
	// 4.事务应该应用在业务逻辑层而不是数据访问层，因此准备重构
	@Override
	@Transactional(rollbackFor = ArithmeticException.class)
	public void transactionalMethod1(T user) {
		log.info("transactionalMethod1(T user)" + user);

		save(user);

		userTransactionnalJdbcDao
				.transactionalMethod2(new UserJdbcEntity("ruanwei_tmp", 2, Date.valueOf("1983-07-06")));

		int i = 1 / 0;
	}

	@Override
	public void transactionalMethod2(T user) {
		log.info("transactionalMethod2(T user)" + user);
		throw new UnsupportedOperationException();
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

	private int[] _batchUpdate(String sql, List<Object[]> batchArgs) {
		log.info("_batchUpdate(List<Object[]> batchArgs");

		int[] updateCounts = jdbcTemplate.batchUpdate(sql, batchArgs);

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
		updateCounts = jdbcTemplate.batchUpdate(sql, bpss);

		ParameterizedPreparedStatementSetter<Object[]> uppss = (ps, args) -> {
			ps.setString(1, (String) args[0]);
			ps.setInt(2, (Integer) args[1]);
			ps.setDate(3, (Date) args[2]);
		};
		int[][] updateCounts2 = jdbcTemplate.batchUpdate(sql, batchArgs, 2, uppss);

		return updateCounts;
	}

	// ====================SimpleJdbc====================
	public void insertUserJdbcEntity6(String name, int age, Date birthday) {
		log.info("insertUserJdbcEntity6(String name, int age, Date birthday)");
		Map<String, Object> parameters = new HashMap<String, Object>(3);
		parameters.put("name", name);
		parameters.put("age", age);
		parameters.put("birthday", birthday);

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
		UserJdbcEntity user = new UserJdbcEntity();
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
}
