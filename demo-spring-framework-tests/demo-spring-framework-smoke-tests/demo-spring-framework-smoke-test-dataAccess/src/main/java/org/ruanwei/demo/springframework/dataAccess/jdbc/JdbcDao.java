package org.ruanwei.demo.springframework.dataAccess.jdbc;

import java.util.List;
import java.util.Map;

import org.ruanwei.demo.springframework.dataAccess.TransactionalDao;
import org.ruanwei.demo.springframework.dataAccess.dao.BatchDao;
import org.ruanwei.demo.springframework.dataAccess.dao.BatchMapDao;
import org.ruanwei.demo.springframework.dataAccess.dao.ExtendDao;

/**
 * 
 * @author ruanwei
 *
 * @param <T>
 * @param <ID>
 */
public interface JdbcDao<T, ID> extends BatchDao<T, ID>, ExtendDao<T,ID>, BatchMapDao, TransactionalDao<T> {

	// ============将CrudDao中的Iterable具体化为List=============
	@Override
	List<T> findAll();

	@Override
	List<T> findAllById(Iterable<ID> ids);

	@Override
	List<T> findAllByGtId(ID id);

	// ============将MapDao中的Iterable具体化为List=============
	@Override
	List<Map<String, Object>> findAllMap();

	@Override
	List<Map<String, Object>> findAllMapById(Iterable<ID> ids);

	@Override
	List<Map<String, Object>> findAllMapByGtId(ID id);
}
