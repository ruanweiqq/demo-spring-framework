package org.ruanwei.demo.springframework.dataAccess.orm.hibernate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ruanwei.demo.springframework.dataAccess.orm.hibernate.entity.UserHibernateEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author ruanwei
 *
 */
@Transactional("hibernateTransactionManager")
@Repository
public class UserHibernateDao extends SimpleHibernateDao<UserHibernateEntity,Integer> {
	private static Log log = LogFactory.getLog(UserHibernateDao.class);
}
