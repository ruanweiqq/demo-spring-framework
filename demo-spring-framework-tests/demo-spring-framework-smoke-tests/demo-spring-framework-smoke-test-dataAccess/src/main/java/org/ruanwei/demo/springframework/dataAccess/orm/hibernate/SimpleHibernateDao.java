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
import org.ruanwei.demo.springframework.dataAccess.TransactionalDao;
import org.ruanwei.demo.springframework.dataAccess.orm.hibernate.entity.UserHibernateEntity;
import org.ruanwei.demo.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.transaction.annotation.Transactional;

/**
 * <b>Hibernate Native API (Hibernate also implements JPA) :</b><br/>
 * <li>org.hibernate.SessionFactory (javax.persistence.EntityManagerFactory).
 * <li>org.hibernate.Session (javax.persistence.EntityManager).
 * <li>org.hibernate.Transaction (javax.persistence.Transaction).<br/><br/>
 * 
 * EntityManagerFactory is thread-safe,but EntityManager is NOT thread-safe.<br/><br/>
 * 
 * Hibernate 的 HibernateTransactionManager + SessionFactory + Session类比于：<br/>
 * JPA 的 JpaTransactionManager + EntityManagerFactory + EntityManager<br/>
 * 
 * @author ruanwei
 *
 */
@Transactional("hibernateTransactionManager")
public class SimpleHibernateDao implements HibernateDao<UserHibernateEntity, Integer> {
	private static Log log = LogFactory.getLog(SimpleHibernateDao.class);

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
	public int save(String name, int age, Date birthday) {
		throw new UnsupportedOperationException();
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

	@Override
	public int saveWithKey(UserHibernateEntity entity) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int saveWithKey(String name, int age, Date birthday) {
		throw new UnsupportedOperationException();
	}

	// =====Read 1=====
	@Transactional(readOnly = true)
	@Override
	public Optional<UserHibernateEntity> findById(Integer id) {
		log.info("findById(Integer id)");

		// session.load(UserEntity.class, id)返回的是代理
		UserHibernateEntity userEntity = currentSession().get(UserHibernateEntity.class, id);
		// return Optional.of(userEntity);
		return Optional.ofNullable(userEntity);
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
	public List<UserHibernateEntity> findAllById(Iterable<Integer> ids) {
		log.info("findAllById(Iterable<Integer> ids)");

		// 注意：Hibernate和JPA直接支持in语句，不用自己拼装
		// String hql_1 = "select u from UserHibernateEntity u where u.id in :ids";
		String hql_1 = "from UserHibernateEntity as u where u.id in :ids";
		Query<UserHibernateEntity> query1 = currentSession().createQuery(hql_1, UserHibernateEntity.class);
		query1.setParameter("ids", ids);
		List<UserHibernateEntity> list1 = query1.getResultList();

		String hql_2 = "from UserHibernateEntity as u where u.id in ?1";
		Query<UserHibernateEntity> query2 = currentSession().createQuery(hql_2, UserHibernateEntity.class);
		query2.setParameter(1, ids);
		List<UserHibernateEntity> list2 = query2.getResultList();

		String sql = "select * from user where id in (:ids)";
		NativeQuery<UserHibernateEntity> query = currentSession().createNativeQuery(sql, UserHibernateEntity.class);
		query.setParameter("ids", StringUtils.toString(ids));
		List<UserHibernateEntity> list = query.getResultList();

		list1.forEach(e -> log.info("e========" + e));
		list2.forEach(e -> log.info("e========" + e));
		list.forEach(e -> log.info("e========" + e));
		return list1;
	}

	@Transactional(readOnly = true)
	@Override
	public List<UserHibernateEntity> findAllByGtId(Integer id) {
		log.info("findAllByGtId(Integer id)");

		// String hql_1 = "select u from UserHibernateEntity u where u.id > :id";
		String hql_1 = "from UserHibernateEntity as u where u.id > :id";
		Query<UserHibernateEntity> query1 = currentSession().createQuery(hql_1, UserHibernateEntity.class);
		query1.setParameter("id", id);
		List<UserHibernateEntity> list1 = query1.getResultList();

		String hql_2 = "from UserHibernateEntity as u where u.id > ?1";
		Query<UserHibernateEntity> query2 = currentSession().createQuery(hql_2, UserHibernateEntity.class);
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
		String hql = "update UserHibernateEntity u set u.age = :age where u.name = :name and u.birthday = :birthday";
		Query<?> query = currentSession().createQuery(hql);
		query.setParameter("age", user.getAge());
		query.setParameter("name", user.getName());
		query.setParameter("birthday", user.getBirthday());

		return query.executeUpdate();
	}

	@Override
	public int updateAge(String name, int age, Date birthday) {
		throw new UnsupportedOperationException();
	};

	// =====Delete=====
	@Override
	public int deleteById(Integer id) {
		log.info("deleteById(Integer id)");

		// String sql = "delete from user u where u.id = :id";
		// NativeQuery query = currentSession().createNativeQuery(sql,
		// UserHibernateEntity.class);
		String hql = "delete UserHibernateEntity u where u.id = :id";
		Query<?> query = currentSession().createQuery(hql);
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

	@Override
	public int delete(String name, int age, Date birthday) {
		throw new UnsupportedOperationException();
	};

	// 1.事务是默认在抛出运行时异常进行回滚的，因此不能在事务方法中进行try-catch捕获
	// 2.事务是通过代理目标对象实现的，因此只有调用代理的事务方法才生效，调用目标对象(例如同一类中的其他方法)没有事务
	// 3.由于事务传播类型不同，transactionalMethod1会回滚，transactionalMethod2不会回滚
	// 4.事务应该应用在业务逻辑层而不是数据访问层，因此准备重构
	@Transactional(rollbackFor = ArithmeticException.class)
	@Override
	public void transactionalMethod1(UserHibernateEntity user) {
		log.info("transactionalMethod1(UserHibernateEntity user)" + user);

		save(user);

		// 注意:不能使用单线程的数据源，也不能与其他的DAO共享数据源，否则这里启动事务失败，参考JpaTransactionManager.doBegin方法第403行
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
	// @Override
	public boolean exists(UserHibernateEntity entity) {
		return currentSession().contains(entity);
	}

	@Transactional(readOnly = true)
	// @Override
	public List<UserHibernateEntity> findAllByIds(List<Integer> ids) {
		Query<UserHibernateEntity> query = currentSession()
				.createQuery("select u from UserHibernateEntity u where u.id in :ids", UserHibernateEntity.class);
		query.setParameter("ids", ids);
		return query.getResultList();
	}

	// @Override
	public int update(UserHibernateEntity entity) {
		currentSession().update(entity);
		return 0;
	}

	// @Override
	public int saveOrUpdate(UserHibernateEntity entity) {
		currentSession().saveOrUpdate(entity);
		return 0;
	}

}
