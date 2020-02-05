package org.ruanwei.demo.springframework.dataAccess.orm.hibernate;

import java.util.List;

import org.ruanwei.demo.springframework.dataAccess.CrudDao;
import org.ruanwei.demo.springframework.dataAccess.TransactionalDao;

/**
 * 
 * @author ruanwei
 *
 * @param <T>
 * @param <ID>
 */
public interface HibernateDao<T, ID> extends CrudDao<T, ID>,TransactionalDao<T> {

	// ============将CrudDao中的Iterable具体化为List=============
	@Override
	List<T> findAll();

	@Override
	List<T> findAllById(Iterable<ID> ids);

	@Override
	List<T> findAllByGtId(ID id);

}
