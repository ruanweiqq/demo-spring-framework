package org.ruanwei.demo.springframework.dataAccess.jdbc;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ruanwei.demo.springframework.dataAccess.jdbc.entity.UserJdbcEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * JdbcDaoSupport提供了setDataSource支持 NamedParameterJdbcTemplate支持IN表达式
 * 
 * @author ruanwei
 *
 */
@Transactional("transactionManager")
@Repository("userJdbcDao2")
public class UserJdbcDao2 extends SimpleJdbcDao2<UserJdbcEntity, Integer> {
	private static Log log = LogFactory.getLog(UserJdbcDao2.class);

}
