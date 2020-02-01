package org.ruanwei.demo.springframework.dataAccess.jdbc;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ruanwei.demo.springframework.dataAccess.TransactionalDao;
import org.ruanwei.demo.springframework.dataAccess.jdbc.entity.UserJdbcEntity;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.support.KeyHolder;

/**
 * JdbcDaoSupport提供了setDataSource支持 NamedParameterJdbcTemplate支持IN表达式
 * 
 * @author ruanwei
 *
 */
public class UserTransactionJdbcDao implements TransactionalDao<UserJdbcEntity> {
	private static Log log = LogFactory.getLog(UserTransactionJdbcDao.class);

	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	private static final String sql_insert_namedParam = "insert into user(name,age,birthday) values(:name, :age, :birthday)";

	public void setDataSource(@Qualifier("springDataSource") DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	// ====================transaction====================
	@Override
	public void transactionalMethod1(UserJdbcEntity user) {
		log.info("transactionalMethod1(UserJdbcEntity user)" + user);
		throw new UnsupportedOperationException();
	}

	// 不能在事务方法中进行try-catch
	@Override
	public void transactionalMethod2(UserJdbcEntity user) {
		log.info("transactionalMethod2(UserJdbcEntity user)" + user);
		save(user);
	}

	// ====================private====================

	private int save(UserJdbcEntity user) {
		log.info("save(UserJdbcEntity user)");
		return _update(sql_insert_namedParam, user, null);
	}

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
}
