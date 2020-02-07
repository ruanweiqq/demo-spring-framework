package org.ruanwei.demo.springframework.dataAccess.jdbc;

import java.util.List;
import java.util.Map;

import org.ruanwei.demo.springframework.dataAccess.dao.ExampleDao;
import org.ruanwei.demo.springframework.dataAccess.dao.ExtendDao;

/**
 * 
 * @author ruanwei
 *
 * @param <T>
 * @param <ID>
 */
public interface JdbcExampleDao<T, ID> extends ExtendDao<T, ID>, ExampleDao {

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
	List<Map<String, Object>> findAllMapByGtId(ID id);

	@Override
	List<Map<String, Object>> findAllMapById(Iterable<ID> ids);
}
