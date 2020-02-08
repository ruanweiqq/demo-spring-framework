package org.ruanwei.demo.springframework.dataAccess.dao;

import java.util.Map;

/**
 * 
 * @author ruanwei
 *
 * @param <ID>
 */
public interface ExtendDao<T, ID> extends CrudDao<T, ID> {

	// ==========Read==========
	Map<String, ?> findMapById(ID id);

	Iterable<Map<String, Object>> findAllMap();

	@Deprecated
	Iterable<Map<String, Object>> findAllMapByGtId(ID id);
	
	Iterable<Map<String, Object>> findAllMapById(Iterable<ID> ids);
}
