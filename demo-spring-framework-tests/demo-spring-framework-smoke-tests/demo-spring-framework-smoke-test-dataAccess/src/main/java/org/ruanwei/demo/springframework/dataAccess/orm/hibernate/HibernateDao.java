package org.ruanwei.demo.springframework.dataAccess.orm.hibernate;

import java.util.List;

import org.ruanwei.demo.springframework.dataAccess.dao.PagingAndSortingDao;
import org.springframework.data.domain.Sort;

/**
 * 
 * @author ruanwei
 *
 * @param <T>
 * @param <ID>
 */
public interface HibernateDao<T, ID> extends PagingAndSortingDao<T, ID> {

	// ============将CrudDao中的Iterable具体化为List=============
	@Override
	List<T> findAll();

	@Override
	List<T> findAllById(Iterable<ID> ids);

	@Override
	List<T> findAllByGtId(ID id);

	// ============将PagingAndSortingDao中的Iterable具体化为List=============
	@Override
	List<T> findAll(Sort sort);

}
