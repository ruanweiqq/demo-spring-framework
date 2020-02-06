package org.ruanwei.demo.springframework.dataAccess;

import java.util.Map;

/**
 * 
 * @author ruanwei
 *
 * @param <ID>
 */
public interface ExtendDao<T, ID> extends Dao<T, ID> {

	// ==========Read==========
	Map<String, ?> findMapById(ID id);

	Iterable<Map<String, Object>> findAllMap();

	Iterable<Map<String, Object>> findAllMapById(Iterable<ID> ids);

	@Deprecated
	Iterable<Map<String, Object>> findAllMapByGtId(ID id);
}
