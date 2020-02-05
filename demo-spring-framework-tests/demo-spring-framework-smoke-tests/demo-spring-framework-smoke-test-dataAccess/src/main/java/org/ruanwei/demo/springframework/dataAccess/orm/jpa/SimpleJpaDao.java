package org.ruanwei.demo.springframework.dataAccess.orm.jpa;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ruanwei.demo.springframework.dataAccess.TransactionalDao;
import org.ruanwei.demo.springframework.dataAccess.orm.jpa.entity.UserJpaEntity;
import org.ruanwei.demo.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

/**
 * <b>Java Persistence API (Hibernate provide a implementation):</b><br/>
 * <li>javax.persistence.EntityManagerFactory (org.hibernate.SessionFactory).
 * <li>javax.persistence.EntityManager (org.hibernate.Session).
 * <li>javax.persistence.Transaction (org.hibernate.Transaction).<br/><br/>
 * 
 * EntityManagerFactory is thread-safe,but EntityManager is NOT thread-safe.<br/><br/>
 * 
 * Hibernate 的 HibernateTransactionManager + SessionFactory + Session类比于：<br/>
 * JPA 的 JpaTransactionManager + EntityManagerFactory + EntityManager<br/><br/>
 * 
 * <b>persistence context:</b><br/>
 * Both the org.hibernate.Session API and javax.persistence.EntityManager API represent a context for dealing with persistent data. <br/><br/>
 * 
 * <b>persistent data state:</b><br/>
 * <li>transient:<br/>
 *   the entity has just been instantiated and is not associated with a persistence context. 
 *   It has no persistent representation in the database and typically no identifier value has been assigned (unless the assigned generator was used).<br/><br/>
 *   
 * <li>managed/persistent:<br/>
 *   the entity has an associated identifier and is associated with a persistence context. 
 *   It may or may not physically exist in the database yet.<br/><br/>
 * 
 * <li>detached:<br/>
 *   the entity has an associated identifier but is no longer associated with a persistence context (usually because the persistence context was closed or the instance was evicted from the context)<br/><br/>
 *
 * <li>removed:<br/>
 *   the entity has an associated identifier and is associated with a persistence context, however, it is scheduled for removal from the database.<br/><br/>
 *  
 * <li>save transient instance: session.save(entity) / entityManager.persist(entity)
 * <li>become managed/persistent instance: session.get(Entity.class, id) / entityManager.find(Entity.class, id) / session.byId(Entity.class).load(id).
 * <li>reload managed/persistent instance: session.refresh(entity) / entityManager.refresh(entity).
 * <li>verify managed/persistent instance: session.contains(entity) / entityManager.contains(entity).
 * <li>remove managed/persistent instance: session.delete(entity) / entityManager.remove(entity).前者还可以删除transient
 * <li>synchronize persistence context state with the underlying database: session.flush() / entityManager.flush().see @DynamicUpdate
 * <li>re-attach managed/persistent instance: session.lock(entity, LockMode.NONE ).
 * <li>detach managed/persistent instance: session.evict(entity) / entityManager.detach(entity);
 * <li>detach all instance: session.clear() / entityManager.clear().
 * <li>merge detached instance: session.merge(entity) / entityManager.merge(entity).
 * <li>update detached instance: session.update(entity) / session.saveOrUpdate(entity).
 * 
 * @author ruanwei
 *
 */
@Transactional("jpaTransactionManager")
public class SimpleJpaDao<T, ID> implements JpaDao<T, ID> {
	private static Log log = LogFactory.getLog(SimpleJpaDao.class);

	// @PersistenceContext inject a shared and thread-safe EntityManager proxy.
	// because the default persistence context type is transaction-scoped,otherwise
	// NOT thread-safe.
	@PersistenceContext
	private EntityManager entityManager;
	private EntityManagerFactory entityManagerFactory;

	// private PersistenceUtil persistenceUtil = Persistence.getPersistenceUtil();
	// private PersistenceUnitUtil persistenceUnitUtil;

	@Autowired
	private TransactionalDao<UserJpaEntity> userTransactionnalJpaDao;

	// TODO:这里@Qualifier和@Autowired都不生效，因此使用了@Primary，使其注入SessionFactory
	@PersistenceUnit
	public void setEntityManagerFactory(@Qualifier("entityManagerFactory") EntityManagerFactory entityManagerFactory) {
		this.entityManagerFactory = entityManagerFactory;
		// this.persistenceUnitUtil = entityManagerFactory.getPersistenceUnitUtil();
	}

	private EntityManager createEntityManager() {
		// creates a new EntityManager every time
		return entityManagerFactory.createEntityManager();
	}

	// ==========Create==========
	@Override
	public int save(T user) {
		log.info("save(T user)");

		entityManager.persist(user);
		return 0;
	}

	@Override
	public int saveAll(Iterable<T> users) {
		log.info("saveAll(Iterable<T> users)");

		int rows = 0;
		for (T user : users) {
			int row = save(user);
			rows += row;
		}
		return rows;
	}

	// =====Read 1=====
	@Transactional(readOnly = true)
	@Override
	public Optional<T> findById(ID id) {
		log.info("findById(ID id)");

		// entityManager.getReference(UserEntity.class, id)返回的是代理
		T userEntity = entityManager.find(getTClass(), id);
		// return Optional.of(userEntity);
		return Optional.ofNullable(userEntity);
	}

	@Transactional(readOnly = true)
	@Override
	public boolean existsById(ID id) {
		log.info("existsById(ID id)");
		return findById(id) != null;
	}

	@Transactional(readOnly = true)
	@Override
	public List<T> findAll() {
		log.info("findAll()");

		TypedQuery<T> query = entityManager.createNamedQuery("findAll", getTClass());
		List<T> list = query.getResultList();

		list.forEach(e -> log.info("e========" + e));
		return list;
	}

	@Transactional(readOnly = true)
	@Override
	public List<T> findAllById(Iterable<ID> ids) {
		log.info("findAllById(Iterable<ID> ids)");

		// 注意：Hibernate和JPA直接支持in语句，不用自己拼装
		// String jpql_1 = "select u from UserJpaEntity u where u.id in :id";
		String jpql_1 = "from UserJpaEntity as u where u.id in :ids";
		TypedQuery<T> query1 = entityManager.createQuery(jpql_1, getTClass());
		query1.setParameter("ids", ids);
		List<T> list1 = query1.getResultList();

		// String jpql_2 = "from UserJpaEntity as u where u.id in (?1)";
		String jpql_2 = "from UserJpaEntity as u where u.id in ?1";
		TypedQuery<T> query2 = entityManager.createQuery(jpql_2, getTClass());
		query2.setParameter(1, ids);
		List<T> list2 = query2.getResultList();

		String sql = "select * from user where id in (:ids)";
		Query query = entityManager.createNativeQuery(sql, UserJpaEntity.class);
		query.setParameter("ids", StringUtils.toString(ids));
		List<T> list = query.getResultList();

		list1.forEach(e -> log.info("e========" + e));
		list2.forEach(e -> log.info("e========" + e));
		list.forEach(e -> log.info("e========" + e));
		return list1;
	}

	@Transactional(readOnly = true)
	@Override
	public List<T> findAllByGtId(ID id) {
		log.info("findAllById(ID id)");

		// String jpql_1 = "select u from UserJpaEntity u where u.id > :id";
		String jpql_1 = "from UserJpaEntity as u where u.id > :id";
		TypedQuery<T> query1 = entityManager.createQuery(jpql_1, getTClass());
		query1.setParameter("id", id);
		List<T> list1 = query1.getResultList();

		String jpql_2 = "from UserJpaEntity as u where u.id > ?1";
		TypedQuery<T> query2 = entityManager.createQuery(jpql_2, getTClass());
		query2.setParameter(1, id);
		List<T> list2 = query2.getResultList();

		String sql = "select * from user where id > :id";
		Query query = entityManager.createNativeQuery(sql, UserJpaEntity.class);
		query.setParameter("id", id);
		List<T> list = query.getResultList();

		list1.forEach(e -> log.info("e========" + e));
		list2.forEach(e -> log.info("e========" + e));
		list.forEach(e -> log.info("e========" + e));
		return list1;
	}

	@Transactional(readOnly = true)
	@Override
	public long count() {
		log.info("count()");

		TypedQuery<Integer> query = entityManager.createNamedQuery("countAll", Integer.class);
		return query.getSingleResult();
	}

	// =====Update=====
	@Override
	public int updateAge(T entity) {
		log.info("updateAge(T entity)");

		// String sql = "update user u set u.age = :age where u.name = :name and
		// u.birthday = :birthday";
		// Query query = entityManager.createNativeQuery(sql, UserJpaEntity.class);
		String jpql = "update UserJpaEntity u set u.age = :age where u.name = :name and u.birthday = :birthday";
		Query query = entityManager.createQuery(jpql);
		UserJpaEntity user = (UserJpaEntity) entity;
		query.setParameter("age", user.getAge());
		query.setParameter("name", user.getName());
		query.setParameter("birthday", user.getBirthday());

		return query.executeUpdate();
	}

	// =====Delete=====
	@Override
	public int deleteById(ID id) {
		log.info("deleteById(ID id)");

		// String sql = "delete from user u where u.id = :id";
		// Query query = entityManager.createNativeQuery(sql, UserJpaEntity.class);
		String jpql = "delete UserJpaEntity u where u.id = :id";
		Query query = entityManager.createQuery(jpql);
		query.setParameter("id", id);

		return query.executeUpdate();
	}

	@Override
	public int delete(T entity) {
		log.info("delete(T user)");
		entityManager.remove(entity);
		return 0;
	}

	@Override
	public int deleteAll(Iterable<T> users) {
		log.info("deleteAll(Iterable<T> users");

		int rows = 0;
		for (T user : users) {
			int row = delete(user);
			rows += row;
		}
		return rows;
	}

	@Override
	public int deleteAll() {
		log.info("deleteAll()");

		// String sql = "delete from user";
		// Query query = entityManager.createNativeQuery(sql, UserJpaEntity.class);
		Query query = entityManager.createQuery("delete UserJpaEntity u");
		return query.executeUpdate();
	}

	// 1.事务是默认在抛出运行时异常进行回滚的，因此不能在事务方法中进行try-catch捕获
	// 2.事务是通过代理目标对象实现的，因此只有调用代理的事务方法才生效，调用目标对象(例如同一类中的其他方法)没有事务
	// 3.由于事务传播类型不同，transactionalMethod1会回滚，transactionalMethod2不会回滚
	// 4.事务应该应用在业务逻辑层而不是数据访问层，因此准备重构
	@Transactional(rollbackFor = ArithmeticException.class)
	@Override
	public void transactionalMethod1(T user) {
		log.info("transactionalMethod1(T user)" + user);

		save(user);

		// 注意:不能使用单线程的数据源，也不能与其他的DAO共享数据源，否则这里启动事务失败，参考JpaTransactionManager.doBegin方法第403行
		userTransactionnalJpaDao.transactionalMethod2(new UserJpaEntity("ruanwei_tmp", 2, Date.valueOf("1983-07-06")));

		int i = 1 / 0;
	}

	@Override
	public void transactionalMethod2(T user) {
		log.info("transactionalMethod2(T user)" + user);
		throw new UnsupportedOperationException();
	}

	// ======================================================
	@Transactional(readOnly = true)
	// @Override
	public boolean exists(UserJpaEntity entity) {
		return entityManager.contains(entity);
	}
}
