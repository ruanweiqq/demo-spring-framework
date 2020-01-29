package org.ruanwei.demo.springframework.dataAccess.orm.jpa;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ruanwei.demo.springframework.dataAccess.DefaultCrudDao;
import org.ruanwei.demo.springframework.dataAccess.TransactionalDao;
import org.ruanwei.demo.springframework.dataAccess.orm.jpa.entity.UserJpaEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * <b>Java Persistence API implemented by Hibernate:</b><br/>
 * <li>javax.persistence.EntityManagerFactory (org.hibernate.SessionFactory).
 * <li>javax.persistence.EntityManager (org.hibernate.Session).
 * <li>javax.persistence.Transaction (org.hibernate.Transaction).<br/><br/>
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
@Repository
public class UserJpaDao extends DefaultCrudDao<UserJpaEntity, Integer> {
	private static Log log = LogFactory.getLog(UserJpaDao.class);

	// 1.EntityManagerFactory is thread-safe,but EntityManager is NOT thread-safe.
	// 2.@PersistenceContext inject a shared and thread-safe EntityManager proxy.
	// because the default persistence context type is transaction-scoped,otherwise
	// entityManager is NOT thread-safe.
	@PersistenceContext
	private EntityManager entityManager; // implemented by Hibernate Session.
	private EntityManagerFactory entityManagerFactory; // implemented by Hibernate SessionFactory,.

	// private PersistenceUtil persistenceUtil = Persistence.getPersistenceUtil();
	// private PersistenceUnitUtil persistenceUnitUtil;

	@Autowired
	private TransactionalDao<UserJpaEntity> userTransactionnalJpaDao;

	@PersistenceUnit
	public void setEntityManagerFactory(@Qualifier("entityManagerFactory") EntityManagerFactory entityManagerFactory) {
		this.entityManagerFactory = entityManagerFactory;
		// this.persistenceUnitUtil = entityManagerFactory.getPersistenceUnitUtil();
		// creates a new EntityManager every time
		// this.entityManager = entityManagerFactory.createEntityManager();
	}

	// ==========Create==========
	@Override
	public int save(UserJpaEntity user) {
		log.info("save(UserJpaEntity user)");

		entityManager.persist(user);
		return 0;
	}

	@Override
	public int saveAll(Iterable<UserJpaEntity> users) {
		log.info("saveAll(Iterable<UserJpaEntity> users)");

		int rows = 0;
		for (UserJpaEntity user : users) {
			int row = save(user);
			rows += row;
		}
		return rows;
	}

	// =====Read 1=====
	@Transactional(readOnly = true)
	@Override
	public UserJpaEntity findById(Integer id) {
		log.info("findById(Integer id)");

		// entityManager.getReference(UserEntity.class, id)返回的是代理
		UserJpaEntity userEntity = entityManager.find(UserJpaEntity.class, id);
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
	public List<UserJpaEntity> findAll() {
		log.info("findAll()");

		TypedQuery<UserJpaEntity> query1 = entityManager.createNamedQuery("findAll", UserJpaEntity.class);
		List<UserJpaEntity> list = query1.getResultList();

		list.forEach(e -> log.info("e========" + e));
		return list;
	}

	@Transactional(readOnly = true)
	@Override
	public List<UserJpaEntity> findAllById(Integer id) {
		log.info("findAllById(Integer id)");

		// String jpql_1 = "select u from UserJpaEntity u where u.id > :id";
		String jpql_1 = "from UserJpaEntity as u where u.id > :id";
		TypedQuery<UserJpaEntity> query1 = entityManager.createQuery(jpql_1, UserJpaEntity.class);
		query1.setParameter("id", id);
		List<UserJpaEntity> list1 = query1.getResultList();

		String jpql_2 = "from UserJpaEntity as u where u.id > ?1";
		TypedQuery<UserJpaEntity> query2 = entityManager.createQuery(jpql_2, UserJpaEntity.class);
		query2.setParameter(1, id);
		List<UserJpaEntity> list2 = query2.getResultList();

		String sql = "select * from user where id > :id";
		Query query = entityManager.createNativeQuery(sql, UserJpaEntity.class);
		query.setParameter("id", id);
		List<UserJpaEntity> list = query.getResultList();

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
	public int updateAge(UserJpaEntity user) {
		log.info("updateAge(UserJpaEntity user)");

		// String sql = "update user u set u.age = :age where u.name = :name and
		// u.birthday = :birthday";
		// Query query = entityManager.createNativeQuery(sql, UserJpaEntity.class);
		String jpql = "update UserJpaEntity u set u.age = :age where u.name = :name and u.birthday = :birthday";
		Query query = entityManager.createQuery(jpql);
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
		// Query query = entityManager.createNativeQuery(sql, UserJpaEntity.class);
		String jpql = "delete UserJpaEntity u where u.id = :id";
		Query query = entityManager.createQuery(jpql);
		query.setParameter("id", id);

		return query.executeUpdate();
	}

	@Override
	public int delete(UserJpaEntity entity) {
		log.info("delete(UserJpaEntity user)");
		entityManager.remove(entity);
		return 0;
	}

	@Override
	public int deleteAll(Iterable<UserJpaEntity> users) {
		log.info("deleteAll(Iterable<UserJpaEntity> users");

		int rows = 0;
		for (UserJpaEntity user : users) {
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

	@Transactional(rollbackFor = ArithmeticException.class)
	@Override
	public void transactionalMethod1(UserJpaEntity user) {
		log.info("transactionalMethod1(UserJpaEntity user)" + user);

		save(user);

		// 注意：由于默认使用代理的原因，调用同一类中事务方法时会忽略其的事务，因此需要把事务方法置于另一个类中
		// 2:不能使用单线程的数据源，也不能与其他的DAO共享数据源，否则这里启动事务失败，参考JpaTransactionManager.doBegin方法第403行
		userTransactionnalJpaDao.transactionalMethod2(new UserJpaEntity("ruanwei_tmp", 2, Date.valueOf("1983-07-06")));

		int i = 1 / 0;
	}

	@Override
	public void transactionalMethod2(UserJpaEntity user) {
		throw new UnsupportedOperationException();
	}

	// ======================================================
	@Transactional(readOnly = true)
	@Override
	public boolean exists(UserJpaEntity entity) {
		return entityManager.contains(entity);
	}

	@Transactional(readOnly = true)
	@Override
	public List<UserJpaEntity> findAllByIds(List<Integer> ids) {
		TypedQuery<UserJpaEntity> query = entityManager.createQuery("select u from UserJpaEntity u where u.id in :ids",
				UserJpaEntity.class);
		query.setParameter("ids", ids);
		return query.getResultList();
	}
}
