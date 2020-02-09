package org.ruanwei.demo.springframework.dataAccess.orm.jpa;

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
public interface JpaDao<T, ID> extends PagingAndSortingDao<T, ID> {

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
