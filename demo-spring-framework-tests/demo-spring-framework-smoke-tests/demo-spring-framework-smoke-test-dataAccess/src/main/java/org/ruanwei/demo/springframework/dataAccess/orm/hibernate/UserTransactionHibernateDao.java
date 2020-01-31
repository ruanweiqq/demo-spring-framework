package org.ruanwei.demo.springframework.dataAccess.orm.hibernate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.ruanwei.demo.springframework.dataAccess.TransactionalDao;
import org.ruanwei.demo.springframework.dataAccess.orm.hibernate.entity.UserHibernateEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author ruanwei
 *
 */
@Transactional(transactionManager = "hibernateTransactionManager", propagation = Propagation.REQUIRES_NEW)
@Repository
public class UserTransactionHibernateDao implements TransactionalDao<UserHibernateEntity> {
	private static Log log = LogFactory.getLog(UserTransactionHibernateDao.class);

	private SessionFactory sessionFactory;
	private HibernateTemplate hibernateTemplate;

	@Autowired
	public void setSessionFactory(@Qualifier("sessionFactory") SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
		this.hibernateTemplate = new HibernateTemplate(sessionFactory);
	}

	// implements JPA EntityManager
	private Session currentSession() {
		// Session session = sessionFactory.openSession();
		return sessionFactory.getCurrentSession();
	}

	@Override
	public void transactionalMethod1(UserHibernateEntity user) {
		log.info("transactionalMethod1(UserHibernateEntity user)" + user);
		throw new UnsupportedOperationException();
	}

	@Override
	public void transactionalMethod2(UserHibernateEntity user) {
		log.info("transactionalMethod2(UserHibernateEntity user)" + user);
		currentSession().save(user);
	}
}
