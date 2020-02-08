package org.ruanwei.demo.springframework.dataAccess.jdbc;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ruanwei.demo.springframework.dataAccess.jdbc.entity.UserJdbcEntity;

/**
 * JdbcDaoSupport提供了setDataSource支持 NamedParameterJdbcTemplate支持IN表达式
 * 
 * @author ruanwei
 *
 */
public class UserJdbcExampleDao extends SimpleJdbcExampleDao<UserJdbcEntity, Integer> {
	private static Log log = LogFactory.getLog(UserJdbcExampleDao.class);

}
