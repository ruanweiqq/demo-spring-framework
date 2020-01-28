package org.ruanwei.demo.springframework.dataAccess.jdbc;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ruanwei.demo.springframework.dataAccess.TransactionnalDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * JdbcDaoSupport提供了setDataSource支持 NamedParameterJdbcTemplate支持IN表达式
 * 
 * @author ruanwei
 *
 */
@Transactional("transactionManager")
@Repository
public class UserTransactionDao implements TransactionnalDao<User> {
	private static Log log = LogFactory.getLog(UserTransactionDao.class);

	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	private static final String sql_insert_namedParam = "insert into user(name,age,birthday) values(:name, :age, :birthday)";

	@Autowired
	public void setDataSource(@Qualifier("springDataSource") DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	// ====================transaction====================
	// 不能在事务方法中进行try-catch
	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void transactionalMethod2(User user) {
		log.info("transactionalMethod2(User user)" + user);
		save(user);
	}

	// ====================private====================

	private int save(User user) {
		log.info("save(User user)");
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
