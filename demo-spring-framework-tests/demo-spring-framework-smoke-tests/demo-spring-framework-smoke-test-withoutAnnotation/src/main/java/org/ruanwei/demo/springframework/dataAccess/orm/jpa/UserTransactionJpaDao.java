package org.ruanwei.demo.springframework.dataAccess.orm.jpa;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ruanwei.demo.springframework.dataAccess.TransactionalDao;
import org.ruanwei.demo.springframework.dataAccess.orm.jpa.entity.UserJpaEntity;

/**
 * 
 * @author ruanwei
 *
 */
public class UserTransactionJpaDao implements TransactionalDao<UserJpaEntity> {
	private static Log log = LogFactory.getLog(UserTransactionJpaDao.class);

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public void transactionalMethod1(UserJpaEntity user) {
		log.info("transactionalMethod1(UserJpaEntity user)" + user);
		throw new UnsupportedOperationException();
	}

	@Override
	public void transactionalMethod2(UserJpaEntity user) {
		log.info("transactionalMethod2(UserJpaEntity user)" + user);
		entityManager.persist(user);
	}
}
