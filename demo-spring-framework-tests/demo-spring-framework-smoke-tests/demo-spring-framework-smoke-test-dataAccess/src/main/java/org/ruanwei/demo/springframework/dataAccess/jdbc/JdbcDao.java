package org.ruanwei.demo.springframework.dataAccess.jdbc;

import java.util.List;
import java.util.Map;

import org.ruanwei.demo.springframework.dataAccess.dao.BatchDao;
import org.ruanwei.demo.springframework.dataAccess.dao.BatchMapDao;
import org.ruanwei.demo.springframework.dataAccess.dao.ExtendDao;
import org.ruanwei.demo.springframework.dataAccess.dao.PagingAndSortingDao;
import org.springframework.data.domain.Sort;

/**
 * 
 * @author ruanwei
 *
 * @param <T>
 * @param <ID>
 */
public interface JdbcDao<T, ID>
		extends PagingAndSortingDao<T, ID>, BatchDao<T, ID>, ExtendDao<T, ID>, BatchMapDao {

	@Deprecated
	int deleteAllByGtId(ID id);

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

	// ============将ExtendDao中的Iterable具体化为List=============
	@Override
	List<Map<String, Object>> findAllMap();

	@Override
	List<Map<String, Object>> findAllMapByGtId(ID id);

	@Override
	List<Map<String, Object>> findAllMapById(Iterable<ID> ids);
}
