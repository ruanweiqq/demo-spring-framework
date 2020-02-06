package org.ruanwei.demo.springframework.dataAccess.jdbc;

import java.sql.Date;
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
import org.ruanwei.demo.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.EmptySqlParameterSource;
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
 * 本例展示NamedParameterJdbcTemplate
 * @author ruanwei
 *
 */
@Transactional("transactionManager")
public class SimpleJdbcDao<T, ID> implements JdbcDao<T, ID> {
	private static Log log = LogFactory.getLog(SimpleJdbcDao.class);

	// 1.core JdbcTemplate & NamedParameterJdbcTemplate thread-safe
	// named parameters instead of the traditional JDBC "?" placeholders.
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

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

	private static final String sql_select_by_id_namedParam = "select * from user where id = :id";
	private static final String sql_select_map_by_id_namedParam = "select id, name, age, birthday from user where id = :id";

	private static final String sql_select_by_ids_namedParam = "select * from user where id in (:ids)";
	private static final String sql_select_map_by_ids_namedParam2 = "select id, name, age, birthday from user where id in (:ids)";

	private static final String sql_select_by_gt_id_namedParam = "select * from user where id > :id";
	private static final String sql_select_map_by_gt_id_namedParam = "select id, name, age, birthday from user where id > :id";

	private static final String sql_select_all = "select * from user";
	private static final String sql_select_map_all = "select id, name, age, birthday from user";

	private static final String sql_select_count = "select count(*) from user";

	private static final String sql_insert_namedParam = "insert into user(name,age,birthday) values(:name, :age, :birthday)";
	private static final String sql_update_age_namedParam = "update user set age = :age where name = :name and birthday = :birthday";
	private static final String sql_delete_by_id_namedParam = "delete from user where id = :id";
	private static final String sql_delete_namedParam = "delete from user where name = :name and age = :age and birthday = :birthday";
	private static final String sql_delete_all = "delete from user";

	@Autowired
	public void setDataSource(@Qualifier("springDataSource") DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);

		this.simpleJdbcInsert = new SimpleJdbcInsert(dataSource).withTableName("user").usingGeneratedKeyColumns("id");
		this.simpleJdbcCall = new SimpleJdbcCall(dataSource);
	}

	// ==========CrudDao==========
	@Override
	public int save(T entity) {
		log.info("save(T entity)");
		return _update(sql_insert_namedParam, entity, null);
	}

	@Override
	public int saveWithKey(T entity) {
		log.info("saveWithKey(T entity)");

		KeyHolder keyHolder = new GeneratedKeyHolder();
		int rows = _update(sql_insert_namedParam, entity, keyHolder);
		int key = keyHolder.getKey().intValue();
		// see @TransactionalEventListener
		applicationEventPublisher.publishEvent(new SaveEvent<T, Integer>(entity, key));

		log.info("key=" + key + ",rows=" + rows);
		return key;
	}

	@Override
	public int saveAll(Iterable<T> entities) {
		log.info("saveAll(Iterable<T> entities)");

		int rows = 0;
		for (T entity : entities) {
			int row = save(entity);
			rows += row;
		}
		log.info("rows=" + rows);
		return rows;
	}

	@Transactional(readOnly = true)
	@Override
	public Optional<T> findById(ID id) {
		log.info("findById(ID id)");

		Map<String, ID> paramMap = new HashMap<String, ID>();
		paramMap.put("id", id);

		T entity = namedParameterJdbcTemplate.queryForObject(sql_select_by_id_namedParam, paramMap,
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

		List<T> entities = namedParameterJdbcTemplate.query(sql_select_all, EmptySqlParameterSource.INSTANCE,
				new BeanPropertyRowMapper<T>(getTClass()));

		entities.forEach(entity -> log.info("entity=" + entity));
		return entities;
	}

	@Transactional(readOnly = true)
	@Override
	public List<T> findAllById(Iterable<ID> ids) {
		log.info("findAllById(Iterable<ID> ids)");

		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("ids", StringUtils.toString(ids));
		List<T> entities = namedParameterJdbcTemplate.query(sql_select_by_ids_namedParam, paramMap,
				new BeanPropertyRowMapper<T>(getTClass()));

		entities.forEach(entity -> log.info("entity=" + entity));
		return entities;
	}

	@Transactional(readOnly = true)
	@Override
	public List<T> findAllByGtId(ID id) {
		log.info("findAllByGtId(ID id)");

		Map<String, ID> paramMap = new HashMap<String, ID>();
		paramMap.put("id", id);
		List<T> entities = namedParameterJdbcTemplate.query(sql_select_by_gt_id_namedParam, paramMap,
				new BeanPropertyRowMapper<T>(getTClass()));

		entities.forEach(entity -> log.info("entity=" + entity));
		return entities;
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

	@Override
	public int updateAge(T entity) {
		log.info("updateAge(T entity)");
		return _update(sql_update_age_namedParam, entity, null);
	}

	@Override
	public int deleteById(ID id) {
		log.info("deleteById(ID id)");

		Map<String, ID> paramMap = new HashMap<String, ID>();
		paramMap.put("id", id);

		return _update(sql_delete_by_id_namedParam, paramMap, null);
	}

	@Override
	public int delete(T entity) {
		log.info("delete(T entity)");
		return _update(sql_delete_namedParam, entity, null);
	}

	@Override
	public int deleteAll(Iterable<T> entities) {
		log.info("deleteAll(Iterable<T> entities");

		int rows = 0;
		for (T entity : entities) {
			int row = delete(entity);
			rows += row;
		}
		log.info("rows=" + rows);
		return rows;
	}

	@Override
	public int deleteAll() {
		log.info("deleteAll()");
		return _update(sql_delete_all, Collections.emptyMap(), null);
	}

	// ==========MapDao==========
	@Transactional(readOnly = true)
	@Override
	public Map<String, ?> findMapById(ID id) {
		log.info("findMapById(ID id)");

		Map<String, ID> mapParam = new HashMap<String, ID>();
		mapParam.put("id", id);
		Map<String, ?> mapEntity = namedParameterJdbcTemplate.queryForMap(sql_select_map_by_id_namedParam, mapParam);

		mapEntity.forEach((k, v) -> log.info(k + "=" + v));
		return mapEntity;
	}

	@Transactional(readOnly = true)
	@Override
	public List<Map<String, Object>> findAllMap() {
		log.info("findAllMap()");

		List<Map<String, Object>> mapEntities = namedParameterJdbcTemplate.queryForList(sql_select_map_all,
				EmptySqlParameterSource.INSTANCE);

		mapEntities.forEach(mapEntity -> mapEntity.forEach((k, v) -> log.info(k + "=" + v)));
		return mapEntities;
	}

	@Transactional(readOnly = true)
	@Override
	public List<Map<String, Object>> findAllMapByGtId(ID id) {
		log.info("findAllMapByGtId(ID id)");

		Map<String, ID> mapParam = new HashMap<String, ID>();
		mapParam.put("id", id);
		List<Map<String, Object>> mapEntities = namedParameterJdbcTemplate
				.queryForList(sql_select_map_by_gt_id_namedParam, mapParam);

		mapEntities.forEach(mapEntity -> mapEntity.forEach((k, v) -> log.info(k + "=" + v)));
		return mapEntities;
	}
	
	@Override
	public int save(Map<String, ?> mapParam) {
		log.info("save(Map<String, ?> mapParam)");
		return _update(sql_insert_namedParam, mapParam, null);
	}

	@Override
	public int saveWithKey(Map<String, ?> mapParam) {
		log.info("saveWithKey(Map<String, ?> mapParam)");

		KeyHolder keyHolder = new GeneratedKeyHolder();
		int rows = _update(sql_insert_namedParam, mapParam, keyHolder);
		int key = keyHolder.getKey().intValue();
		log.info("key=" + key + ",rows=" + rows);
		return key;
	}
	
	@Override
	public int updateAge(Map<String, ?> mapParam) {
		log.info("updateAge(Map<String, ?> mapParam)");
		return _update(sql_update_age_namedParam, mapParam, null);
	}
	
	@Override
	public int delete(Map<String, ?> mapParam) {
		log.info("delete(Map<String, ?> mapParam)");
		return _update(sql_delete_namedParam, mapParam, null);
	}

	// ==========BatchDao==========
	@Override
	public int[] batchSave(T[] entities) {
		log.info("batchSave(T[] entities)");
		return _batchUpdate(sql_insert_namedParam, entities);
	}

	@Override
	public int[] batchSave(Collection<T> entities) {
		log.info("batchSave(Collection<T> entities)");
		return _batchUpdate(sql_insert_namedParam, entities);
	}

	@Override
	public int[] batchSave(Map<String, Object>[] mapEntities) {
		log.info("batchSave(Map<String, Object>[] mapEntities)");
		return _batchUpdate(sql_insert_namedParam, mapEntities);
	}

	@Override
	public int[] batchSave(List<Object[]> batchArgs) {
		log.info("batchSave(List<Object[]> batchArgs");
		return _batchUpdate(sql_insert_namedParam, batchArgs);
	}

	@Override
	public int[] batchUpdateAge(T[] entities) {
		log.info("batchUpdateAge(T[] entities)");
		return _batchUpdate(sql_update_age_namedParam, entities);
	}

	@Override
	public int[] batchUpdateAge(Collection<T> entities) {
		log.info("batchUpdateAge(Collection<T> entities)");
		return _batchUpdate(sql_update_age_namedParam, entities);
	}

	@Override
	public int[] batchUpdateAge(Map<String, Object>[] mapEntities) {
		log.info("batchUpdateAge(Map<String, Object>[] mapEntities)");
		return _batchUpdate(sql_update_age_namedParam, mapEntities);
	}

	@Override
	public int[] batchUpdateAge(List<Object[]> batchArgs) {
		log.info("batchUpdateAge(List<Object[]> batchArgs)");
		return _batchUpdate(sql_update_age_namedParam, batchArgs);
	}

	@Override
	public int[] batchDelete(T[] entities) {
		log.info("batchDelete(T[] entities)");
		return _batchUpdate(sql_delete_namedParam, entities);
	}

	@Override
	public int[] batchDelete(Collection<T> entities) {
		log.info("batchDelete(Collection<T> entities)");
		return _batchUpdate(sql_delete_namedParam, entities);
	}

	@Override
	public int[] batchDelete(Map<String, Object>[] mapEntities) {
		log.info("batchDelete(Map<String, Object>[] mapEntities)");
		return _batchUpdate(sql_delete_namedParam, mapEntities);
	}

	@Override
	public int[] batchDelete(List<Object[]> batchArgs) {
		log.info("batchDelete(List<Object[]> batchArgs)");
		return _batchUpdate(sql_delete_namedParam, batchArgs);
	}

	// ==========ExampleDao==========
	@Override
	public int save(String name, int age, Date birthday) {
		log.info("save(String name, int age, Date birthday)");
		throw new UnsupportedOperationException();
	}

	@Override
	public int saveWithKey(String name, int age, Date birthday) {
		log.info("saveWithKey(String name, int age, Date birthday)");
		throw new UnsupportedOperationException();
	}

	

	@Override
	public int updateAge(String name, int age, Date birthday) {
		log.info("updateAge(String name, int age, Date birthday)");
		throw new UnsupportedOperationException();
	}

	@Override
	public int delete(String name, int age, Date birthday) {
		log.info("delete(String name, int age, Date birthday)");
		throw new UnsupportedOperationException();
	}

	// ==========TransactionalDao==========
	// 1.事务是默认在抛出运行时异常进行回滚的，因此不能在事务方法中进行try-catch捕获
	// 2.事务是通过代理目标对象实现的，因此只有调用代理的事务方法才生效，调用目标对象(例如同一类中的其他方法)没有事务
	// 3.由于事务传播类型不同，transactionalMethod1会回滚，transactionalMethod2不会回滚
	// 4.事务应该应用在业务逻辑层而不是数据访问层，因此准备重构
	@Override
	@Transactional(rollbackFor = ArithmeticException.class)
	public void transactionalMethod1(T entity1, T entity2) {
		log.info("transactionalMethod1(T entity1, T entity2)" + entity1);

		save(entity1);

		userTransactionnalJdbcDao.transactionalMethod2(entity2);
		// new UserJdbcEntity("ruanwei_tmp", 2, Date.valueOf("1983-07-06"))
		int i = 1 / 0;
	}

	@Override
	public void transactionalMethod2(T entity) {
		log.info("transactionalMethod2(T entity)" + entity);
		throw new UnsupportedOperationException();
	}

	// ====================private====================
	private int _update(String sql, Object candidate, KeyHolder keyHolder) {
		log.info("_update(String sql, Object candidate, KeyHolder keyHolder)");

		// MapSqlParameterSource or BeanPropertySqlParameterSource
		SqlParameterSource sqlParamSource = SqlParameterSourceUtils.createBatch(candidate)[0];
		if (keyHolder == null) {
			return namedParameterJdbcTemplate.update(sql, sqlParamSource);
		} else {
			return namedParameterJdbcTemplate.update(sql, sqlParamSource, keyHolder);
		}
	}

	private int _update(String sql, Map<String, ?> valueMap, KeyHolder keyHolder) {
		log.info("_update(String sql, Map<String, ?> valueMap, KeyHolder keyHolder)");

		// MapSqlParameterSource or BeanPropertySqlParameterSource
		SqlParameterSource sqlParamSource = SqlParameterSourceUtils.createBatch(valueMap)[0];
		if (keyHolder == null) {
			return namedParameterJdbcTemplate.update(sql, sqlParamSource);
		} else {
			namedParameterJdbcTemplate.update(sql, sqlParamSource, keyHolder);
			return keyHolder.getKey().intValue();
		}
	}

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
}
