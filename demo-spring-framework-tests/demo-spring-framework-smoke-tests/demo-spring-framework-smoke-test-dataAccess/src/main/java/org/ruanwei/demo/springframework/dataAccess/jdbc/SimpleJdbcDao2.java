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
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
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
 * 本例展示JdbcTemplate
 * @author ruanwei
 *
 */
@Transactional("transactionManager")
public class SimpleJdbcDao2<T, ID> implements JdbcDao<T, ID> {
	private static Log log = LogFactory.getLog(SimpleJdbcDao2.class);

	// 1.core JdbcTemplate & NamedParameterJdbcTemplate thread-safe
	// named parameters instead of the traditional JDBC "?" placeholders.
	private JdbcTemplate jdbcTemplate;

	// 2.core SimpleJdbc classes
	private SimpleJdbcInsert simpleJdbcInsert;
	private SimpleJdbcCall simpleJdbcCall;// 执行存储过程或者函数

	// 3.RdbmsOperation objects.
	private SqlQuery<T> sqlQuery;
	private MappingSqlQuery<T> mappingSqlQuery;
	private UpdatableSqlQuery<T> updatableSqlQuery;
	private SqlUpdate sqlUpdate;
	private StoredProcedure storedProcedure;

	@Qualifier("userTransactionnalJdbcDao")
	@Autowired
	private TransactionalDao<T> userTransactionnalJdbcDao;

	@Autowired
	private ApplicationEventPublisher applicationEventPublisher;

	private static final String sql_select_by_id1 = "select * from user where id = ?";
	private static final String sql_select_by_id2 = "select id, name, age, birthday from user where id = ?";

	private static final String sql_select_by_ids1 = "select * from user where id in (?)";
	private static final String sql_select_by_ids2 = "select id, name, age, birthday from user where id in (?)";

	private static final String sql_select_by_gt_id1 = "select id, name, age, birthday from user where id > ?";
	private static final String sql_select_by_gt_id2 = "select * from user where id > ?";

	private static final String sql_select_all1 = "select * from user";
	private static final String sql_select_all2 = "select id, name, age, birthday from user";

	private static final String sql_select_count = "select count(*) from user";

	private static final String sql_insert = "insert into user(name,age,birthday) values(?, ?, ?)";
	private static final String sql_update_age = "update user set age = ? where name = ? and birthday = ?";
	private static final String sql_delete_by_id = "delete from user where id = ?";
	private static final String sql_delete = "delete from user where name = ? and age = ? and birthday = ?";
	private static final String sql_delete_all = "delete from user";

	@Autowired
	public void setDataSource(@Qualifier("springDataSource") DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);

		this.simpleJdbcInsert = new SimpleJdbcInsert(dataSource).withTableName("user").usingGeneratedKeyColumns("id");
		this.simpleJdbcCall = new SimpleJdbcCall(dataSource);
	}

	// ==========CrudDao==========
	@Override
	public int save(T entity) {
		log.info("save(T entity)");
		throw new UnsupportedOperationException();
	}

	@Override
	public int saveWithKey(T entity) {
		log.info("saveWithKey(T entity)");
		throw new UnsupportedOperationException();
	}

	@Override
	public int saveAll(Iterable<T> users) {
		log.info("saveAll(Iterable<T> users)");
		throw new UnsupportedOperationException();
	}

	// RowMapperResultSetExtractor & BeanPropertyRowMapper
	@Transactional(readOnly = true)
	@Override
	public Optional<T> findById(ID id) {
		log.info("findById(ID id)");

		T user = jdbcTemplate.queryForObject(sql_select_by_id1, new Object[] { id }, getTClass());

		log.info("user=" + user);
		return Optional.ofNullable(user);
	}

	@Transactional(readOnly = true)
	@Override
	public boolean existsById(ID id) {
		log.info("existsById(ID id)");
		return findById(id) == null;
	}

	@Transactional(readOnly = true)
	@Override
	public List<T> findAll() {
		log.info("findAll()");

		List<T> userList = jdbcTemplate.queryForList(sql_select_all1, getTClass());

		userList.forEach(user -> log.info("user=" + user));
		return userList;
	}

	@Transactional(readOnly = true)
	@Override
	public List<T> findAllById(Iterable<ID> ids) {
		log.info("findAllById(Iterable<Integer> ids)");

		List<T> userList = jdbcTemplate.queryForList(sql_select_by_ids1, new Object[] { StringUtils.toString(ids) },
				getTClass());

		userList.forEach(user -> log.info("user=" + user));
		return userList;
	}

	@Transactional(readOnly = true)
	@Override
	public List<T> findAllByGtId(ID id) {
		log.info("findAllByGtId(Integer id)");

		List<T> userList = jdbcTemplate.queryForList(sql_select_by_gt_id2, new Object[] { id }, getTClass());
		userList.forEach(user -> log.info("user1=" + user));

		PreparedStatementSetter pss0 = ps -> ps.setInt(1, (Integer) id);
		userList = jdbcTemplate.query(sql_select_by_gt_id2, pss0, new BeanPropertyRowMapper<T>(getTClass()));
		userList.forEach(user -> log.info("user2" + user));

		return userList;
	}

	// RowMapperResultSetExtractor & SingleColumnRowMapper
	@Transactional(readOnly = true)
	@Override
	public long count() {
		log.info("count()");

		Integer count = jdbcTemplate.queryForObject(sql_select_count, Integer.class);

		log.info("count=" + count);
		return count;
	}

	@Override
	public int updateAge(T user) {
		log.info("updateAge(T user)");
		throw new UnsupportedOperationException();
	}

	@Override
	public int deleteById(ID id) {
		log.info("deleteById(ID id)");

		Map<String, ID> paramMap = new HashMap<String, ID>();
		paramMap.put("id", id);

		return _update2(sql_delete_by_id, paramMap, null);
	}

	@Override
	public int delete(T user) {
		log.info("delete(T user)");
		throw new UnsupportedOperationException();
	}

	@Override
	public int deleteAll(Iterable<T> users) {
		log.info("deleteAll(Iterable<T> users");
		throw new UnsupportedOperationException();
	}

	@Override
	public int deleteAll() {
		log.info("deleteAll()");
		return _update2(sql_delete_all, Collections.emptyMap(), null);
	}

	// ==========MapDao==========
	// RowMapperResultSetExtractor & ColumnMapRowMapper
	@Transactional(readOnly = true)
	@Override
	public Map<String, ?> findMapById(ID id) {
		log.info("findMapById(ID id)");

		Map<String, Object> columnMap = jdbcTemplate.queryForMap(sql_select_by_id2, id);

		columnMap.forEach((k, v) -> log.info(k + "=" + v));
		return columnMap;
	}

	@Transactional(readOnly = true)
	@Override
	public List<Map<String, Object>> findAllMap() {
		log.info("findAllMap()");

		List<Map<String, Object>> columnMapList = jdbcTemplate.queryForList(sql_select_all2,
				EmptySqlParameterSource.INSTANCE);

		columnMapList.forEach(columbMap -> columbMap.forEach((k, v) -> log.info(k + "=" + v)));
		return columnMapList;
	}

	@Transactional(readOnly = true)
	@Override
	public List<Map<String, Object>> findAllMapByGtId(ID id) {
		log.info("findAllMapById(ID id)");

		List<Map<String, Object>> columnMapList = jdbcTemplate.queryForList(sql_select_by_gt_id1, new Object[] { id });
		columnMapList.forEach(columbMap -> columbMap.forEach((k, v) -> log.info(k + "=" + v)));

		PreparedStatementSetter pss0 = ps -> ps.setInt(1, (Integer) id);
		columnMapList = jdbcTemplate.queryForList(sql_select_by_gt_id1, pss0);
		columnMapList.forEach(columbMap -> log.info("user2" + columbMap));

		return columnMapList;
	}

	// ==========BatchDao==========
	@Override
	public int[] batchSave(T[] users) {
		log.info("batchSave(T[] users)");
		throw new UnsupportedOperationException();
	}

	@Override
	public int[] batchSave(Collection<T> users) {
		log.info("batchSave(Collection<T> users)");
		throw new UnsupportedOperationException();
	}

	@Override
	public int[] batchSave(Map<String, Object>[] users) {
		log.info("batchSave(Map<String, Object>[] users)");
		throw new UnsupportedOperationException();
	}

	@Override
	public int[] batchSave(List<Object[]> batchArgs) {
		log.info("batchSave(List<Object[]> batchArgs");
		return _batchUpdate2(sql_insert, batchArgs);
	}

	@Override
	public int[] batchUpdateAge(T[] users) {
		log.info("batchUpdateAge(T[] users)");
		throw new UnsupportedOperationException();
	}

	@Override
	public int[] batchUpdateAge(Collection<T> users) {
		log.info("batchUpdateAge(Collection<T> users)");
		throw new UnsupportedOperationException();
	}

	@Override
	public int[] batchUpdateAge(Map<String, Object>[] users) {
		log.info("batchUpdateAge(Map<String, Object>[] users)");
		throw new UnsupportedOperationException();
	}

	@Override
	public int[] batchUpdateAge(List<Object[]> batchArgs) {
		log.info("batchUpdateAge(List<Object[]> batchArgs)");
		return _batchUpdate2(sql_update_age, batchArgs);
	}

	@Override
	public int[] batchDelete(T[] users) {
		log.info("batchDelete(T[] users)");
		throw new UnsupportedOperationException();
	}

	@Override
	public int[] batchDelete(Collection<T> users) {
		log.info("batchDelete(Collection<T> users)");
		throw new UnsupportedOperationException();
	}

	@Override
	public int[] batchDelete(Map<String, Object>[] users) {
		log.info("batchDelete(Map<String, Object>[] users)");
		throw new UnsupportedOperationException();
	}

	@Override
	public int[] batchDelete(List<Object[]> batchArgs) {
		log.info("batchDelete(List<Object[]> batchArgs)");
		return _batchUpdate2(sql_delete, batchArgs);
	}

	// ==========ExampleDao==========
	@Override
	public int save(Map<String, ?> paramMap) {
		log.info("save(Map<String, ?> paramMap)");
		return _update2(sql_insert, paramMap, null);
	}

	@Override
	public int saveWithKey(Map<String, ?> userMap) {
		log.info("saveWithKey(Map<String, ?> userMap)");

		int key = _update2(sql_insert, userMap, new GeneratedKeyHolder());
		log.info("generatedKey=" + key);
		return key;
	}

	@Override
	public int save(String name, int age, Date birthday) {
		log.info("save(String name, int age, Date birthday)");

		int rows = _update2(sql_insert, name, age, birthday, null);
		return rows;
	}

	public void save2(String name, int age, Date birthday) {
		log.info("insertUserJdbcEntity6(UserJdbcEntity user)");
		Map<String, Object> parameters = new HashMap<String, Object>(3);
		parameters.put("name", name);
		parameters.put("age", age);
		parameters.put("birthday", birthday);

		simpleJdbcInsert.execute(parameters);
		Number newId = simpleJdbcInsert.executeAndReturnKey(parameters);
		log.info("generatedKey=" + newId.longValue());
	}

	@Override
	public int saveWithKey(String name, int age, Date birthday) {
		log.info("saveWithKey(String name, int age, Date birthday)");

		int key = _update2(sql_insert, name, age, birthday, new GeneratedKeyHolder());
		log.info("generatedKey=" + key);
		return key;
	}

	@Override
	public int updateAge(Map<String, ?> userMap) {
		log.info("updateAge(Map<String, ?> userMap)");
		return _update2(sql_update_age, userMap, null);
	}

	@Override
	public int updateAge(String name, int age, Date birthday) {
		log.info("updateAge(String name, int age, Date birthday)");
		return _update2(sql_update_age, name, age, birthday, null);
	}

	@Override
	public int delete(Map<String, ?> mapUser) {
		log.info("delete(Map<String, ?> mapUser)");
		return _update2(sql_delete, mapUser, null);
	}

	@Override
	public int delete(String name, int age, Date birthday) {
		log.info("delete(String name, int age, Date birthday)");
		return _update2(sql_delete, name, age, birthday, null);
	}

	// ==========TransactionalDao==========
	// 1.事务是默认在抛出运行时异常进行回滚的，因此不能在事务方法中进行try-catch捕获
	// 2.事务是通过代理目标对象实现的，因此只有调用代理的事务方法才生效，调用目标对象(例如同一类中的其他方法)没有事务
	// 3.由于事务传播类型不同，transactionalMethod1会回滚，transactionalMethod2不会回滚
	// 4.事务应该应用在业务逻辑层而不是数据访问层，因此准备重构
	@Override
	@Transactional(rollbackFor = ArithmeticException.class)
	public void transactionalMethod1(T entity1, T entity2) {
		log.info("transactionalMethod1(T entity1, T entity2)" + entity1 + entity2);

		save(entity1);

		userTransactionnalJdbcDao.transactionalMethod2(entity2);
				
		int i = 1 / 0;
	}

	@Override
	public void transactionalMethod2(T user) {
		log.info("transactionalMethod2(T user)" + user);
		throw new UnsupportedOperationException();
	}

	// ====================private====================

	private int _update2(String sql, String name, int age, Date birthday, KeyHolder keyHolder) {
		log.info("_update2(String sql, String name, int age, Date birthday, KeyHolder keyHolder)");

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

	private int _update2(String sql, Map<String, ?> paramMap, KeyHolder keyHolder) {
		log.info("_update2(String sql, String name, int age, Date birthday, KeyHolder keyHolder)");

		PreparedStatementSetter pss = ps -> {
			ps.setString(1, (String) paramMap.get("name"));
			ps.setInt(2, (Integer) paramMap.get("age"));
			ps.setDate(3, (Date) paramMap.get("birthday"));
		};
		int updateCounts = jdbcTemplate.update(sql, pss);

		PreparedStatementCreator psc = conn -> {
			PreparedStatement ps = conn.prepareStatement(sql, new String[] { "id" });
			ps.setString(1, (String) paramMap.get("name"));
			ps.setInt(2, (Integer) paramMap.get("age"));
			ps.setDate(3, (Date) paramMap.get("birthday"));
			return ps;
		};
		if (keyHolder == null) {
			updateCounts = jdbcTemplate.update(psc);
		} else {
			jdbcTemplate.update(psc, keyHolder);
			updateCounts = keyHolder.getKey().intValue();
		}

		updateCounts = jdbcTemplate.update(sql, paramMap.get("name"), paramMap.get("age"), paramMap.get("birthday"));

		return updateCounts;
	}

	// ====================batch update====================
	private int[] _batchUpdate2(String sql, List<Object[]> batchArgs) {
		log.info("_batchUpdate2(String sql, List<Object[]> batchArgs)");

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

		return updateCounts;
	}

	private int[][] _batchUpdate2(String sql, Collection<Object[]> batchArgs) {
		log.info("_batchUpdate2(String sql, Collection<Object[]> batchArgs)");

		ParameterizedPreparedStatementSetter<Object[]> uppss = (ps, args) -> {
			ps.setString(1, (String) args[0]);
			ps.setInt(2, (Integer) args[1]);
			ps.setDate(3, (Date) args[2]);
		};
		int[][] updateCounts2 = jdbcTemplate.batchUpdate(sql, batchArgs, 2, uppss);

		return updateCounts2;
	}

	// ====================SimpleJdbc====================
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
