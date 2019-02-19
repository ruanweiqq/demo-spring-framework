package org.ruanwei.demo.springframework.dataAccess.jdbc;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

/**
 * RowMapper
 * 
 * @author ruanwei
 *
 */
public class AdvancedJdbcTemplate {
	private static Log log = LogFactory.getLog(AdvancedJdbcTemplate.class);

	private JdbcTemplate jdbcTemplate;
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public AdvancedJdbcTemplate(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		
	}

	// ====================single row====================
	public <T> T queryForObject(String sql, Class<T> requiredType) {
		log.info("queryForObject==================================");
		T obj = jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(
				requiredType));
		log.info("obj=" + obj);
		return obj;
	}

	public <T> T queryForObject(String sql, Object[] args, Class<T> requiredType) {
		log.info("queryForObject==================================");
		T obj = jdbcTemplate.queryForObject(sql, args,
				new BeanPropertyRowMapper<>(requiredType));
		log.info("obj=" + obj);
		return obj;
	}

	public <T> T queryForObject(String sql, Map<String, ?> namedParam,
			Class<T> requiredType) {
		log.info("queryForObject==================================");
		T obj = namedParameterJdbcTemplate.queryForObject(sql, namedParam,
				new BeanPropertyRowMapper<T>(requiredType));
		log.info("obj=" + obj);
		return obj;
	}

	// ====================multiple row====================
	public <T> List<T> queryForObjectList(String sql, Class<T> requiredType) {
		log.info("queryForObjectList==================================");
		List<T> objList = jdbcTemplate.query(sql, new BeanPropertyRowMapper<T>(
				requiredType));
		objList.forEach(obj -> log.info("obj=" + obj));
		return objList;
	}

	public <T> List<T> queryForObjectList(String sql, Object[] args,
			Class<T> requiredType) {
		log.info("queryForObjectList==================================");
		List<T> objList = jdbcTemplate.query(sql, args,
				new BeanPropertyRowMapper<T>(requiredType));
		objList.forEach(obj -> log.info("obj=" + obj));
		return objList;
	}

	public <T> List<T> queryForObjectList(String sql,
			PreparedStatementSetter pss, Class<T> requiredType) {
		log.info("queryForObjectList==================================");
		List<T> objList = jdbcTemplate.query(sql, pss,
				new BeanPropertyRowMapper<T>(requiredType));
		objList.forEach(obj -> log.info("obj=" + obj));
		return objList;
	}

	public <T> List<T> queryForObjectList(String sql,
			Map<String, ?> namedParam, Class<T> requiredType) {
		log.info("queryForObjectList==================================");
		List<T> objList = namedParameterJdbcTemplate.query(sql, namedParam,
				new BeanPropertyRowMapper<T>(requiredType));
		objList.forEach(obj -> log.info("obj=" + obj));
		return objList;
	}

	// ====================multiple row====================

}
