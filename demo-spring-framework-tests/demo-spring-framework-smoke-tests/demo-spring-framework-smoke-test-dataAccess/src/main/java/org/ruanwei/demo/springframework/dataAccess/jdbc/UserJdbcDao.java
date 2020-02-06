package org.ruanwei.demo.springframework.dataAccess.jdbc;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ruanwei.demo.springframework.dataAccess.jdbc.entity.UserJdbcEntity;
import org.springframework.stereotype.Repository;

/**
 * JdbcDaoSupport提供了setDataSource支持 NamedParameterJdbcTemplate支持IN表达式
 * 
 * @author ruanwei
 *
 */
@Repository
public class UserJdbcDao extends SimpleJdbcDao<UserJdbcEntity,Integer> {
	private static Log log = LogFactory.getLog(UserJdbcDao.class);
}
