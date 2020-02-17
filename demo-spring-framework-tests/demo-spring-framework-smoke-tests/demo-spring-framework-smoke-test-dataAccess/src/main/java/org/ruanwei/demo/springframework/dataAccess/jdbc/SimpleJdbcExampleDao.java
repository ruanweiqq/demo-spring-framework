package org.ruanwei.demo.springframework.dataAccess.jdbc;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
@Transactional("dataSourceTransactionManager")
public class SimpleJdbcExampleDao<T, ID> implements JdbcExampleDao<T, ID> {
	private static Log log = LogFactory.getLog(SimpleJdbcExampleDao.class);

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

	@Autowired
	private ApplicationEventPublisher applicationEventPublisher;

	private static final String sql_select_by_id = "select * from user where id = ?";
	private static final String sql_select_map_by_id = "select id, name, age, birthday from user where id = ?";

	private static final String sql_select_by_ids = "select * from user where id in (?)";
	private static final String sql_select_map_by_ids = "select id, name, age, birthday from user where id in (?)";

	private static final String sql_select_by_gt_id = "select * from user where id > ?";
	private static final String sql_select_map_by_gt_id = "select id, name, age, birthday from user where id > ?";

	private static final String sql_select_all = "select * from user";
	private static final String sql_select_map_all = "select id, name, age, birthday from user";

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
	@Transactional(readOnly = true)
	@Override
	public Optional<T> findById(ID id) {
		log.info("findById(ID id)");

		// RowMapperResultSetExtractor & BeanPropertyRowMapper
		T entity = jdbcTemplate.queryForObject(sql_select_by_id, new Object[] { id },
				new BeanPropertyRowMapper<T>(getTClass()));

		log.info("entity=" + entity);
		return Optional.ofNullable(entity);
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

		List<T> entities = jdbcTemplate.query(sql_select_all, new BeanPropertyRowMapper<T>(getTClass()));

		entities.forEach(entity -> log.info("entity=" + entity));
		return entities;
	}

	@Transactional(readOnly = true)
	@Override
	public List<T> findAllById(Iterable<ID> ids) {
		log.info("findAllById(Iterable<ID> ids)");

		List<T> entities = jdbcTemplate.query(sql_select_by_ids, new Object[] { StringUtils.toString(ids) },
				new BeanPropertyRowMapper<T>(getTClass()));

		entities.forEach(entity -> log.info("entity=" + entity));
		return entities;
	}

	@Transactional(readOnly = true)
	@Override
	public List<T> findAllByGtId(ID id) {
		log.info("findAllByGtId(ID id)");

		List<T> entities = jdbcTemplate.query(sql_select_by_gt_id, new Object[] { id },
				new BeanPropertyRowMapper<T>(getTClass()));
		entities.forEach(entity -> log.info("entity=" + entity));

		PreparedStatementSetter pss0 = ps -> ps.setInt(1, (Integer) id);
		entities = jdbcTemplate.query(sql_select_by_gt_id, pss0, new BeanPropertyRowMapper<T>(getTClass()));
		entities.forEach(entity -> log.info("entity" + entity));

		return entities;
	}

	@Transactional(readOnly = true)
	@Override
	public long count() {
		log.info("count()");

		// RowMapperResultSetExtractor & SingleColumnRowMapper
		Integer count = jdbcTemplate.queryForObject(sql_select_count, Integer.class);

		log.info("count=" + count);
		return count;
	}

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
	public int saveAll(Iterable<T> entities) {
		log.info("saveAll(Iterable<T> entities)");
		throw new UnsupportedOperationException();
	}

	@Override
	public int updateAge(T entity) {
		log.info("updateAge(T entity)");
		throw new UnsupportedOperationException();
	}
	
	@Override
	public int update(T entity) {
		log.info("update(T entity)");
		throw new UnsupportedOperationException();
	}

	@Override
	public int deleteById(ID id) {
		log.info("deleteById(ID id)");
		int rows = jdbcTemplate.update(sql_delete_by_id, id);
		return rows;
	}

	@Override
	public int delete(T entity) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int deleteAll(Iterable<T> entities) {
		log.info("deleteAll(Iterable<T> entities");
		throw new UnsupportedOperationException();
	}

	@Override
	public int deleteAll() {
		log.info("deleteAll()");
		int rows = jdbcTemplate.update(sql_delete_all, new Object[] {});
		return rows;
	}

	// ==========MapDao==========
	@Transactional(readOnly = true)
	@Override
	public Map<String, ?> findMapById(ID id) {
		log.info("findMapById(ID id)");

		// RowMapperResultSetExtractor & ColumnMapRowMapper
		Map<String, Object> mapEntity = jdbcTemplate.queryForMap(sql_select_map_by_id, id);

		mapEntity.forEach((k, v) -> log.info(k + "=" + v));
		return mapEntity;
	}

	@Transactional(readOnly = true)
	@Override
	public List<Map<String, Object>> findAllMap() {
		log.info("findAllMap()");

		List<Map<String, Object>> mapEntities = jdbcTemplate.queryForList(sql_select_map_all, new Object[] {});

		mapEntities.forEach(mapEntity -> mapEntity.forEach((k, v) -> log.info(k + "=" + v)));
		return mapEntities;
	}

	@Transactional(readOnly = true)
	@Override
	public List<Map<String, Object>> findAllMapByGtId(ID id) {
		log.info("findAllMapById(ID id)");

		List<Map<String, Object>> mapEntities = jdbcTemplate.queryForList(sql_select_map_by_gt_id, new Object[] { id });
		mapEntities.forEach(mapEntity -> mapEntity.forEach((k, v) -> log.info(k + "=" + v)));

		/*PreparedStatementSetter pss = ps -> ps.setInt(1, (Integer) id);
		mapEntities = jdbcTemplate.queryForList(sql_select_map_by_gt_id, pss);
		mapEntities.forEach(mapEntity -> log.info("mapEntity" + mapEntity));*/

		return mapEntities;
	}

	@Transactional(readOnly = true)
	@Override
	public List<Map<String, Object>> findAllMapById(Iterable<ID> ids) {
		log.info("findAllMapById(Iterable<ID> ids");

		List<Map<String, Object>> mapEntities = jdbcTemplate.queryForList(sql_select_map_by_ids,
				new Object[] { StringUtils.toString(ids) });
		mapEntities.forEach(mapEntity -> mapEntity.forEach((k, v) -> log.info(k + "=" + v)));

		/*PreparedStatementSetter pss = ps -> ps.setString(1, StringUtils.toString(ids));
		mapEntities = jdbcTemplate.queryForList(sql_select_map_by_ids, pss);
		mapEntities.forEach(mapEntity -> log.info("mapEntity" + mapEntity));*/

		return mapEntities;
	}

	// ==========ExampleDao==========
	@Override
	public int[] batchSave(List<Object[]> batchArgs) {
		log.info("batchSave(List<Object[]> batchArgs");
		return jdbcTemplate.batchUpdate(sql_insert, batchArgs);
	}

	@Override
	public int[] batchUpdateAge(List<Object[]> batchArgs) {
		log.info("batchUpdateAge(List<Object[]> batchArgs)");
		return jdbcTemplate.batchUpdate(sql_update_age, batchArgs);
	}

	@Override
	public int[] batchDelete(List<Object[]> batchArgs) {
		log.info("batchDelete(List<Object[]> batchArgs)");
		return jdbcTemplate.batchUpdate(sql_delete, batchArgs);
	}

	@Override
	public int save(String name, int age, Date birthday) {
		log.info("save(String name, int age, Date birthday)");

		int rows = _update2(sql_insert, name, age, birthday, null);
		return rows;
	}

	@Override
	public int saveWithKey(String name, int age, Date birthday) {
		log.info("saveWithKey(String name, int age, Date birthday)");

		KeyHolder keyHolder = new GeneratedKeyHolder();
		int rows = _update2(sql_insert, name, age, birthday, keyHolder);
		int key = keyHolder.getKey().intValue();
		log.info("key=" + key + ",rows=" + rows);
		return key;
	}

	public void saveWithKey2(String name, int age, Date birthday) {
		log.info("saveWithKey2(String name, int age, Date birthday)");
		Map<String, Object> parameters = new HashMap<String, Object>(3);
		parameters.put("name", name);
		parameters.put("age", age);
		parameters.put("birthday", birthday);

		simpleJdbcInsert.execute(parameters);
		Number newId = simpleJdbcInsert.executeAndReturnKey(parameters);
		log.info("generatedKey=" + newId.longValue());
	}

	@Override
	public int updateAge(String name, int age, Date birthday) {
		log.info("updateAge(String name, int age, Date birthday)");
		return _update2(sql_update_age, name, age, birthday, null);
	}

	@Override
	public int delete(String name, int age, Date birthday) {
		log.info("delete(String name, int age, Date birthday)");
		return _update2(sql_delete, name, age, birthday, null);
	}

	// ====================private====================
	private int _update2(String sql, String name, int age, Date birthday, KeyHolder keyHolder) {
		log.info("_update2(String sql, String name, int age, Date birthday, KeyHolder keyHolder)");

		int rows = jdbcTemplate.update(sql, name, age, birthday);

		PreparedStatementSetter pss = ps -> {
			ps.setString(1, name);
			ps.setInt(2, age);
			ps.setDate(3, birthday);
		};
		rows = jdbcTemplate.update(sql, pss);

		PreparedStatementCreator psc = conn -> {
			PreparedStatement ps = conn.prepareStatement(sql, new String[] { "id" });
			ps.setString(1, name);
			ps.setInt(2, age);
			ps.setDate(3, birthday);
			return ps;
		};
		if (keyHolder == null) {
			rows = jdbcTemplate.update(psc);
		} else {
			rows = jdbcTemplate.update(psc, keyHolder);
		}

		return rows;
	}

	private int[] _batchUpdate(String sql, List<Object[]> batchArgs) {
		log.info("_batchUpdate(String sql, List<Object[]> batchArgs)");

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
		int[] updateCounts = jdbcTemplate.batchUpdate(sql, bpss);

		ParameterizedPreparedStatementSetter<Object[]> ppss = (ps, args) -> {
			ps.setString(1, (String) args[0]);
			ps.setInt(2, (Integer) args[1]);
			ps.setDate(3, (Date) args[2]);
		};
		int[][] updateCounts2 = jdbcTemplate.batchUpdate(sql, batchArgs, 2, ppss);

		return updateCounts;
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

		log.info("name=" + out.get("out_name"));
		log.info("age=" + out.get("out_age"));
		log.info("birthday=" + out.get("out_birthday"));
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
