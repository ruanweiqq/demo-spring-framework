package org.ruanwei.demo.springframework.dataAccess.orm.hibernate;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.ruanwei.demo.springframework.dataAccess.DefaultCrudDao;
import org.ruanwei.demo.springframework.dataAccess.TransactionalDao;
import org.ruanwei.demo.springframework.dataAccess.orm.hibernate.entity.UserHibernateEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * <b>Hibernate Native API (implements JPA) :</b><br/>
 * <li>org.hibernate.SessionFactory (javax.persistence.EntityManagerFactory).
 * <li>org.hibernate.Session (javax.persistence.EntityManager).
 * <li>org.hibernate.Transaction (javax.persistence.Transaction).<br/>
 * EntityManagerFactory is thread-safe,but EntityManager is NOT thread-safe.<br/><br/>
 * 
 * Hibernate 的 HibernateTransactionManager + SessionFactory + Session类比于：<br/>
 * JPA 的 JpaTransactionManager + EntityManagerFactory + EntityManager<br/>
 * 
 * @author ruanwei
 *
 */
@Transactional("hibernateTransactionManager")
@Repository
public class UserHibernateDao extends DefaultCrudDao<UserHibernateEntity, Integer> {
	private static Log log = LogFactory.getLog(UserHibernateDao.class);

	private SessionFactory sessionFactory; // thread-safe, expensive to create
	private HibernateTemplate hibernateTemplate; // from Spring ORM

	@Autowired
	private TransactionalDao<UserHibernateEntity> userTransactionnalHibernateDao;

	@Autowired
	public void setSessionFactory(@Qualifier("sessionFactory") SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
		this.hibernateTemplate = new HibernateTemplate(sessionFactory);
	}

	private Session currentSession() {
		// Session session = sessionFactory.openSession();
		return sessionFactory.getCurrentSession();
	}

	// ==========Create==========
	@Override
	public int save(UserHibernateEntity entity) {
		log.info("save(UserHibernateEntity user)");
		return (int) currentSession().save(entity);
	}

	@Override
	public int saveAll(Iterable<UserHibernateEntity> users) {
		log.info("saveAll(Iterable<UserHibernateEntity> users)");

		int rows = 0;
		for (UserHibernateEntity user : users) {
			int row = save(user);
			rows += row;
		}
		return rows;
	}

	// =====Read 1=====
	@Transactional(readOnly = true)
	@Override
	public UserHibernateEntity findById(Integer id) {
		log.info("findById(Integer id)");

		// session.load(UserEntity.class, id)返回的是代理
		UserHibernateEntity userEntity = currentSession().get(UserHibernateEntity.class, id);
		// return Optional.of(userEntity);
		return userEntity;
	}

	@Transactional(readOnly = true)
	@Override
	public boolean existsById(Integer id) {
		log.info("existsById(Integer id)");
		return findById(id) != null;
	}

	@Transactional(readOnly = true)
	@Override
	public List<UserHibernateEntity> findAll() {
		log.info("findAll()");

		Query<UserHibernateEntity> query = currentSession().createNamedQuery("hibernate.findAll",
				UserHibernateEntity.class);
		List<UserHibernateEntity> result = query.list();
		result.forEach(e -> log.info("e========" + e));
		return result;
	}

	@Transactional(readOnly = true)
	@Override
	public List<UserHibernateEntity> findAllById(Integer id) {
		log.info("findAllById(Integer id)");

		// String jpql_1 = "select u from UserHibernateEntity u where u.id > :id";
		String jpql_1 = "from UserHibernateEntity as u where u.id > :id";
		Query<UserHibernateEntity> query1 = currentSession().createQuery(jpql_1, UserHibernateEntity.class);
		query1.setParameter("id", id);
		List<UserHibernateEntity> list1 = query1.getResultList();

		String jpql_2 = "from UserHibernateEntity as u where u.id > ?1";
		Query<UserHibernateEntity> query2 = currentSession().createQuery(jpql_2, UserHibernateEntity.class);
		query2.setParameter(1, id);
		List<UserHibernateEntity> list2 = query2.getResultList();

		String sql = "select * from user where id > :id";
		NativeQuery<UserHibernateEntity> query = currentSession().createNativeQuery(sql, UserHibernateEntity.class);
		query.setParameter("id", id);
		List<UserHibernateEntity> list = query.getResultList();

		list1.forEach(e -> log.info("e========" + e));
		list2.forEach(e -> log.info("e========" + e));
		list.forEach(e -> log.info("e========" + e));
		return list1;
	}

	@Transactional(readOnly = true)
	@Override
	public long count() {
		log.info("count()");

		Query<Integer> query = currentSession().createNamedQuery("hibernate.countAll", Integer.class);
		return query.getSingleResult();
	}

	// =====Update=====
	@Override
	public int updateAge(UserHibernateEntity user) {
		log.info("updateAge(UserHibernateEntity user)");

		// String sql = "update user u set u.age = :age where u.name = :name and
		// u.birthday = :birthday";
		// NativeQuery query = currentSession().createNativeQuery(sql,
		// UserHibernateEntity.class);
		String jpql = "update UserHibernateEntity u set u.age = :age where u.name = :name and u.birthday = :birthday";
		Query<?> query = currentSession().createQuery(jpql);
		query.setParameter("age", user.getAge());
		query.setParameter("name", user.getName());
		query.setParameter("birthday", user.getBirthday());

		return query.executeUpdate();
	}

	// =====Delete=====
	@Override
	public int deleteById(Integer id) {
		log.info("deleteById(Integer id)");

		// String sql = "delete from user u where u.id = :id";
		// NativeQuery query = currentSession().createNativeQuery(sql,
		// UserHibernateEntity.class);
		String jpql = "delete UserHibernateEntity u where u.id = :id";
		Query<?> query = currentSession().createQuery(jpql);
		query.setParameter("id", id);

		return query.executeUpdate();
	}

	@Override
	public int delete(UserHibernateEntity user) {
		log.info("delete(UserHibernateEntity user)");
		currentSession().delete(user);
		return 0;
	}

	@Override
	public int deleteAll(Iterable<UserHibernateEntity> users) {
		log.info("deleteAll(Iterable<UserHibernateEntity> users)");

		int rows = 0;
		for (UserHibernateEntity user : users) {
			int row = delete(user);
			rows += row;
		}
		return rows;
	}

	@Override
	public int deleteAll() {
		log.info("deleteAll()");

		// String sql = "delete from user";
		// NativeQuery query = entityManager.createNativeQuery(sql,
		// UserHibernateEntity.class);
		Query<?> query = currentSession().createQuery("delete UserHibernateEntity u");
		return query.executeUpdate();
	}

	@Transactional(rollbackFor = ArithmeticException.class)
	@Override
	public void transactionalMethod1(UserHibernateEntity user) {
		log.info("transactionalMethod1(UserHibernateEntity user)" + user);

		save(user);

		// 注意：由于默认使用代理的原因，调用同一类中事务方法时会忽略其的事务，因此需要把事务方法置于另一个类中
		// 2:不能使用单线程的数据源，也不能与其他的DAO共享数据源，否则这里启动事务失败，参考JpaTransactionManager.doBegin方法第403行
		userTransactionnalHibernateDao
				.transactionalMethod2(new UserHibernateEntity("ruanwei_tmp", 2, Date.valueOf("1983-07-06")));

		int i = 1 / 0;
	}

	@Override
	public void transactionalMethod2(UserHibernateEntity user) {
		log.info("transactionalMethod2(UserHibernateEntity user)" + user);
		throw new UnsupportedOperationException();
	}

	// ======================================================

	@Transactional(readOnly = true)
	public Optional<UserHibernateEntity> findById3(Integer id) {
		return currentSession().byId(UserHibernateEntity.class).loadOptional(id);
	}

	@Transactional(readOnly = true)
	@Override
	public boolean exists(UserHibernateEntity entity) {
		return currentSession().contains(entity);
	}

	@Transactional(readOnly = true)
	@Override
	public List<UserHibernateEntity> findAllByIds(List<Integer> ids) {
		Query<UserHibernateEntity> query = currentSession()
				.createQuery("select u from UserHibernateEntity u where u.id in :ids", UserHibernateEntity.class);
		query.setParameter("ids", ids);
		return query.getResultList();
	}

	@Override
	public int update(UserHibernateEntity entity) {
		currentSession().update(entity);
		return 0;
	}

	@Override
	public int saveOrUpdate(UserHibernateEntity entity) {
		currentSession().saveOrUpdate(entity);
		return 0;
	}

}
