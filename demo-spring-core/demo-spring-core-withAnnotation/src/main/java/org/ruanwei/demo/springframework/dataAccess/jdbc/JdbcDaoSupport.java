package org.ruanwei.demo.springframework.dataAccess.jdbc;

import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ruanwei.demo.springframework.dataAccess.User;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.object.MappingSqlQuery;
import org.springframework.jdbc.object.SqlQuery;
import org.springframework.jdbc.object.SqlUpdate;
import org.springframework.jdbc.object.StoredProcedure;
import org.springframework.jdbc.object.UpdatableSqlQuery;

public abstract class JdbcDaoSupport {
	private static Log log = LogFactory.getLog(JdbcDaoSupport.class);

	// 1.core JdbcTemplate & NamedParameterJdbcTemplate thread-safe
	protected JdbcTemplate jdbcTemplate;
	// named parameters instead of the traditional JDBC "?" placeholders.
	protected NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	// 2.core SimpleJdbc classes
	private SimpleJdbcInsert simpleJdbcInsert;
	private SimpleJdbcCall simpleJdbcCall;// 执行存储过程或者函数

	// 3.RdbmsOperation objects.
	private SqlQuery<User> sqlQuery;
	private MappingSqlQuery<User> mappingSqlQuery;
	private UpdatableSqlQuery<User> updatableSqlQuery;
	private SqlUpdate sqlUpdate;
	private StoredProcedure storedProcedure;

	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);

		this.simpleJdbcInsert = new SimpleJdbcInsert(dataSource).withTableName("user").usingGeneratedKeyColumns("id");
		this.simpleJdbcCall = new SimpleJdbcCall(dataSource);
	}

	// 1.Read single row
	// RowMapperResultSetExtractor & BeanPropertyRowMapper
	public <T> Optional<T> findBeanByExample(String sql, Class<T> requiredType, Object... args) {
		T bean = jdbcTemplate.queryForObject(sql, args, new BeanPropertyRowMapper<>(requiredType));
		return Optional.of(bean);
	}

	public <T> Optional<T> findBeanByExample(String sql, Class<T> requiredType, Map<String, ?> namedParam) {
		T bean = namedParameterJdbcTemplate.queryForObject(sql, namedParam, new BeanPropertyRowMapper<>(requiredType));
		return Optional.of(bean);
	}

	// RowMapperResultSetExtractor & ColumnMapRowMapper
	public Map<String, ?> findColumnMapByExample(String sql, Object... args) {
		return jdbcTemplate.queryForMap(sql, args);
	}

	public Map<String, ?> findColumnMapByExample(String sql, Map<String, ?> namedParam) {
		return namedParameterJdbcTemplate.queryForMap(sql, namedParam);
	}

	// RowMapperResultSetExtractor & SingleColumnRowMapper
	public <E> Optional<E> findSingleColumnByExample(String sql, Class<E> requiredType, Object... argValues) {
		E column = jdbcTemplate.queryForObject(sql, argValues, requiredType);
		return Optional.of(column);
	}

	public <E> Optional<E> findSingleColumnByExample(String sql, Class<E> requiredType, Map<String, ?> namedParams) {
		// MapSqlParameterSource
		E column = namedParameterJdbcTemplate.queryForObject(sql, namedParams, requiredType);
		return Optional.of(column);
	}

	// 2.Read multiple rows
	// RowMapperResultSetExtractor & BeanPropertyRowMapper
	public <T> List<T> findAllBeanByExample(String sql, Class<T> requiredType, Object... args) {
		// 可以用PreparedStatementSetter代替args
		List<T> beanList = jdbcTemplate.query(sql, args, new BeanPropertyRowMapper<T>(requiredType));
		beanList.forEach(bean -> log.info("bean=" + bean));
		return beanList;
	}

	public <T> List<T> findAllBeanByExample(String sql, Class<T> requiredType, Map<String, ?> namedParam) {
		List<T> beanList = namedParameterJdbcTemplate.query(sql, namedParam, new BeanPropertyRowMapper<T>(requiredType));
		beanList.forEach(bean -> log.info("bean=" + bean));
		return beanList;
	}

	// RowMapperResultSetExtractor & ColumnMapRowMapper
	public List<Map<String, Object>> findAllColumnMapByExample(String sql, Object... args) {
		return jdbcTemplate.queryForList(sql, args);
	}

	public List<Map<String, Object>> findAllColumnMapByExample(String sql, Map<String, ?> namedParam) {
		return namedParameterJdbcTemplate.queryForList(sql, namedParam);
	}

	// RowMapperResultSetExtractor & SingleColumnRowMapper
	public <E> List<E> findAllSingleColumnByExample(String sql, Class<E> requiredType, Object... argValues) {
		List<E> columnList = jdbcTemplate.queryForList(sql, argValues, requiredType);
		columnList.forEach(column -> log.info("column=" + column));
		return columnList;
	}

	public <E> List<E> findAllSingleColumnByExample(String sql, Class<E> requiredType, Map<String, ?> namedParams) {
		List<E> columnList = namedParameterJdbcTemplate.queryForList(sql, namedParams, requiredType);
		columnList.forEach(column -> log.info("column=" + column));
		return columnList;
	}

	// 3.Create/Update/Delete
	public int updateByExample(String sql, Object... args) {
		// 可以用PreparedStatementSetter代替args
		return jdbcTemplate.update(sql, args);
	}

	public int updateByExample(String sql, Map<String, ?> namedParam) {
		return namedParameterJdbcTemplate.update(sql, namedParam);
	}

	// 4.Batch
	public int[] batchUpdateByExample(String sql, List<Object[]> batchArgs) {
		// 可以使用BatchPreparedStatementSetter代替batchArgs
		return jdbcTemplate.batchUpdate(sql, batchArgs);
	}

	public int[] batchUpdateByExample(String sql, Map<String, ?>[] batchValues) {
		/* SqlParameterSource[] batchValues = SqlParameterSourceUtils.createBatch(batchValues);
		   SqlParameterSource[] batchValues = SqlParameterSourceUtils.createBatch(list);
		*/
		return namedParameterJdbcTemplate.batchUpdate(sql, batchValues);
	}

	// ====================SimpleJdbc====================
	private void insertUser6(User user) {
		log.info("insertUser6(User user)");
		Map<String, Object> parameters = new HashMap<String, Object>(3);
		parameters.put("name", user.getName());
		parameters.put("age", user.getAge());
		parameters.put("birthday", user.getBirthday());
		simpleJdbcInsert.execute(parameters);
		Number newId = simpleJdbcInsert.executeAndReturnKey(parameters);
		log.info("generatedKey=" + newId.longValue());
	}

	private void callProcedure() {
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

}
