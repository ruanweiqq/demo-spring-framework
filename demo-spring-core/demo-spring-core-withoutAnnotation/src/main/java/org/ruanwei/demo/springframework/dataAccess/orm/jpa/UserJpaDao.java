package org.ruanwei.demo.springframework.dataAccess.orm.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ruanwei.demo.springframework.dataAccess.orm.jpa.entity.UserEntity;

public class UserJpaDao {
	private static Log log = LogFactory.getLog(UserJpaDao.class);

	private static final String sql_11 = "select name from user where id = 1";
	private static final String sql_12 = "select name from user where id = ?";
	private static final String jpql_12 = "from User as u where u.age = ?1";
	private static final String sql_13 = "select name from user where id = :id";

	private EntityManagerFactory entityManagerFactory;

	public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
		this.entityManagerFactory = entityManagerFactory;
	}

	public void queryForSingleRowWithSingleColumn(int id) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			Query query = entityManager.createQuery(jpql_12);
			query.setParameter(1, 1);
			List<UserEntity> list = query.getResultList();
			list.forEach(e -> log.info("e========" + e));
		} finally {
			if (entityManager != null) {
				entityManager.close();
			}
		}
	}
}
