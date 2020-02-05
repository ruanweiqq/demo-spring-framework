package org.ruanwei.demo.springframework.dataAccess.jdbc;

import java.util.List;
import java.util.Map;

import org.ruanwei.demo.springframework.dataAccess.BatchDao;
import org.ruanwei.demo.springframework.dataAccess.CrudDao;
import org.ruanwei.demo.springframework.dataAccess.MapDao;
import org.ruanwei.demo.springframework.dataAccess.TransactionalDao;

/**
 * 
 * @author ruanwei
 *
 * @param <T>
 * @param <ID>
 */
public interface JdbcDao<T, ID> extends CrudDao<T, ID>, BatchDao<T, ID>, MapDao<ID>, TransactionalDao<T> {

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
	List<Map<String, Object>> findAllMapById(ID id);

}
