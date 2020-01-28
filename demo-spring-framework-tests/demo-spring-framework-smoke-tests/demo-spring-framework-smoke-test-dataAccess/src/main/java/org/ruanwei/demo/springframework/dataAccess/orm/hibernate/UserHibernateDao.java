package org.ruanwei.demo.springframework.dataAccess.orm.hibernate;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.ruanwei.demo.springframework.dataAccess.DefaultCrudDao;
import org.ruanwei.demo.springframework.dataAccess.orm.jpa.entity.UserJpaEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.transaction.annotation.Transactional;

/**
 * Hibernate Native API:
 * SessionFactory/Session/Transaction
 * 
 * @author ruanwei
 *
 */
@Transactional
//@Repository
public class UserHibernateDao extends DefaultCrudDao<UserJpaEntity, Integer> {
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
	public int save(UserJpaEntity entity) {
		return (int) currentSession().save(entity);
	}

	@Override
	public int[] saveAll(List<UserJpaEntity> entities) {
		entities.forEach(entity -> currentSession().save(entity));
		return null;
	}

	// 2.Update
	@Override
	public int update(UserJpaEntity entity) {
		currentSession().update(entity);
		return 0;
	}

	// 3.1.Read a single row
	@Transactional(readOnly = true)
	@Override
	public UserJpaEntity findById(Integer id) {
		// session.load(UserEntity.class, id)返回的是代理
		UserJpaEntity userEntity = currentSession().get(UserJpaEntity.class, id);
		// return Optional.of(userEntity);
		return userEntity;
	}

	@Transactional(readOnly = true)
	public Optional<UserJpaEntity> findById3(Integer id) {
		return currentSession().byId(UserJpaEntity.class).loadOptional(id);
	}

	@Transactional(readOnly = true)
	@Override
	public boolean existsById(Integer id) {
		return findById(id) != null;
	}

	@Transactional(readOnly = true)
	@Override
	public boolean exists(UserJpaEntity entity) {
		return currentSession().contains(entity);
	}

	@Override
	public UserJpaEntity findByExample(String sql, Object... argValues) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserJpaEntity findByExample(String sql, Map<String, ?> namedParams) {
		// TODO Auto-generated method stub
		return null;
	}

	// 3.2.Read multiple rows
	@Transactional(readOnly = true)
	@Override
	public List<UserJpaEntity> findAll() {
		Query<UserJpaEntity> query1 = currentSession().createQuery("from UserEntity", UserJpaEntity.class);
		Query<UserJpaEntity> query2 = currentSession().createNamedQuery("findAll", UserJpaEntity.class);
		List<UserJpaEntity> result = query2.list();
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
	public List<UserJpaEntity> findAllById(List<Integer> ids) {
		Query<UserJpaEntity> query = currentSession().createQuery("select u from UserEntity u where u.id in :ids",
				UserJpaEntity.class);
		query.setParameter("ids", ids);
		return query.getResultList();
	}

	@Override
	public List<UserJpaEntity> findAllByExample(String sql, Object... argValues) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<UserJpaEntity> findAllByExample(String sql, Map<String, ?> namedParams) {
		// TODO Auto-generated method stub
		return null;
	}

	// 4.Delete
	@Override
	public int deleteById(Integer id) {
		return 0;
	}

	@Override
	public int delete(UserJpaEntity entity) {
		currentSession().delete(entity);
		return 0;
	}

	@Override
	public int[] deleteAll(List<UserJpaEntity> entities) {
		entities.forEach(entity -> currentSession().delete(entity));
		return null;
	}

	@Override
	public int deleteAll() {
		return 0;
	}

	// 5.Save or Update
	@Override
	public int saveOrUpdate(UserJpaEntity entity) {
		currentSession().saveOrUpdate(entity);
		return 0;
	}

	@Override
	public int saveAll(Iterable<UserJpaEntity> entities) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<UserJpaEntity> findAllById(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int updateAge(UserJpaEntity entity) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int deleteAll(Iterable<UserJpaEntity> entities) {
		// TODO Auto-generated method stub
		return 0;
	}

}
