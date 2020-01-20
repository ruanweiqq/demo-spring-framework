package org.ruanwei.demo.springframework.dataAccess.orm.hibernate;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.persistence.TypedQuery;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.ruanwei.demo.springframework.dataAccess.jdbc.CrudDao2;
import org.ruanwei.demo.springframework.dataAccess.orm.jpa.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Hibernate Native API:
 * SessionFactory/Session/Transaction
 * 
 * @author ruanwei
 *
 */
@Transactional
@Repository
public class UserHibernateDao implements CrudDao2<UserEntity, Integer> {
	private static Log log = LogFactory.getLog(UserHibernateDao.class);

	private static final String hql = "from demo.User user where user.id=?";

	// implements JPA EntityManagerFactory, thread-safe, expensive to create.
	private SessionFactory sessionFactory;

	// from Spring ORM
	private HibernateTemplate hibernateTemplate;

	@Required
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

	// 1.Create
	@Override
	public int save(UserEntity entity) {
		return (int) currentSession().save(entity);
	}

	@Override
	public int[] saveAll(List<UserEntity> entities) {
		entities.forEach(entity -> currentSession().save(entity));
		return null;
	}

	// 2.Update
	@Override
	public int update(UserEntity entity) {
		currentSession().update(entity);
		return 0;
	}

	// 3.1.Read a single row
	@Transactional(readOnly = true)
	@Override
	public Optional<UserEntity> findById(Integer id) {
		// session.load(UserEntity.class, id)返回的是代理
		UserEntity userEntity = currentSession().get(UserEntity.class, id);
		return Optional.of(userEntity);
	}

	@Transactional(readOnly = true)
	public Optional<UserEntity> findById2(Integer id) {
		return currentSession().byId(UserEntity.class).loadOptional(id);
	}

	@Transactional(readOnly = true)
	@Override
	public boolean existsById(Integer id) {
		return findById(id).isPresent();
	}

	@Transactional(readOnly = true)
	@Override
	public boolean exists(UserEntity entity) {
		return currentSession().contains(entity);
	}

	@Override
	public Optional<UserEntity> findByExample(String sql, Object... argValues) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Optional<UserEntity> findByExample(String sql, Map<String, ?> namedParams) {
		// TODO Auto-generated method stub
		return null;
	}

	// 3.2.Read multiple rows
	@Transactional(readOnly = true)
	@Override
	public List<UserEntity> findAll() {
		Query<UserEntity> query1 = currentSession().createQuery("from UserEntity", UserEntity.class);
		Query<UserEntity> query2 = currentSession().createNamedQuery("findAll", UserEntity.class);
		List<UserEntity> result = query2.list();
		result.forEach(e -> log.info("e========" + e));
		return result;
	}

	@Transactional(readOnly = true)
	@Override
	public long count() {
		Query<Integer> query = currentSession().createNamedQuery("countAll", Integer.class);
		return query.getSingleResult();
	}

	@Transactional(readOnly = true)
	@Override
	public List<UserEntity> findAllById(List<Integer> ids) {
		Query<UserEntity> query = currentSession().createQuery("select u from UserEntity u where u.id in :ids",
				UserEntity.class);
		query.setParameter("ids", ids);
		return query.getResultList();
	}

	@Override
	public List<UserEntity> findAllByExample(String sql, Object... argValues) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<UserEntity> findAllByExample(String sql, Map<String, ?> namedParams) {
		// TODO Auto-generated method stub
		return null;
	}

	// 4.Delete
	@Override
	public int deleteById(Integer id) {
		return 0;
	}

	@Override
	public int delete(UserEntity entity) {
		currentSession().delete(entity);
		return 0;
	}

	@Override
	public int[] deleteAll(List<UserEntity> entities) {
		entities.forEach(entity -> currentSession().delete(entity));
		return null;
	}

	@Override
	public int deleteAll() {
		return 0;
	}

	// 5.Save or Update
	@Override
	public int saveOrUpdate(UserEntity entity) {
		currentSession().saveOrUpdate(entity);
		return 0;
	}

}
