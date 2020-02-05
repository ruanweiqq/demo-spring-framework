package org.ruanwei.demo.springframework.dataAccess.orm.jpa;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author ruanwei
 *
 */
@Transactional("jpaTransactionManager")
@Repository
public class UserJpaDao extends SimpleJpaDao {
	private static Log log = LogFactory.getLog(UserJpaDao.class);
	
}
