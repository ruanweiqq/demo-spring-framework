package org.ruanwei.demo.springframework.dataAccess.orm.jpa;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;
import javax.persistence.PersistenceUnitUtil;
import javax.persistence.PersistenceUtil;
import javax.persistence.TypedQuery;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ruanwei.demo.springframework.dataAccess.jdbc.CrudDao2;
import org.ruanwei.demo.springframework.dataAccess.orm.jpa.entity.UserEntity;
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
@Transactional
//@Repository
public class UserJpaDao implements CrudDao2<UserEntity, Integer> {
	private static Log log = LogFactory.getLog(UserJpaDao.class);

	private static final String jpql_12 = "from User as u where u.age = ?1";

	// shared EntityManager,thread-safe proxy
	@PersistenceContext
	private EntityManager entityManager; // implemented by Hibernate Session,not thread-safe.

	private EntityManagerFactory entityManagerFactory; // implemented by Hibernate SessionFactory,thread-safe.
	private PersistenceUnitUtil persistenceUnitUtil;
	private PersistenceUtil persistenceUtil = Persistence.getPersistenceUtil();

	@PersistenceUnit
	public void setEntityManagerFactory(@Qualifier("entityManagerFactory") EntityManagerFactory entityManagerFactory) {
		this.entityManagerFactory = entityManagerFactory;
		this.persistenceUnitUtil = entityManagerFactory.getPersistenceUnitUtil();
	}

	// 1.Create
	@Override
	public int save(UserEntity entity) {
		entityManager.persist(entity);
		return 0;
	}

	@Override
	public int[] saveAll(List<UserEntity> entities) {
		entities.forEach(entity -> entityManager.persist(entity));
		return null;
	}

	// 2.Update
	@Override
	public int update(UserEntity entity) {
		return 0;
	}

	// 3.1.Read a single row
	@Transactional(readOnly = true)
	@Override
	public Optional<UserEntity> findById(Integer id) {
		// entityManager.getReference(UserEntity.class, id)返回的是代理
		UserEntity userEntity = entityManager.find(UserEntity.class, id);
		return Optional.of(userEntity);
	}

	@Transactional(readOnly = true)
	@Override
	public boolean existsById(Integer id) {
		return findById(id).isPresent();
	}

	@Transactional(readOnly = true)
	@Override
	public boolean exists(UserEntity entity) {
		return entityManager.contains(entity);
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
		// always creates a new EntityManager
		// EntityManager entityManager = entityManagerFactory.createEntityManager();
		// Query query = entityManager.createNamedQuery("findAll");
		TypedQuery<UserEntity> query1 = entityManager.createNamedQuery("findAll", UserEntity.class);
		TypedQuery<UserEntity> query2 = entityManager.createQuery("from UserEntity", UserEntity.class);
		List<UserEntity> list = query1.getResultList();
		list.forEach(e -> log.info("e========" + e));
		return list;
	}

	@Transactional(readOnly = true)
	@Override
	public long count() {
		TypedQuery<Integer> query = entityManager.createNamedQuery("countAll", Integer.class);
		return query.getSingleResult();
	}

	@Transactional(readOnly = true)
	@Override
	public List<UserEntity> findAllById(List<Integer> ids) {
		TypedQuery<UserEntity> query = entityManager.createQuery("select u from UserEntity u where u.id in :ids",
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
		entityManager.remove(entity);
		return 0;
	}

	@Override
	public int[] deleteAll(List<UserEntity> entities) {
		return null;
	}

	@Override
	public int deleteAll() {
		return 0;
	}

	// 5.Save or Update
	@Override
	public int saveOrUpdate(UserEntity entity) {
		if (entity.getId() == 0) {
			return save(entity);
		} else {
			return update(entity);
		}
	}

}
