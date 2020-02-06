package org.ruanwei.demo.springframework.dataAccess;

import java.util.Map;

/**
 * 
 * @author ruanwei
 *
 * @param <T>
 * @param <ID>
 */
public interface MapDao<T, ID> extends CrudDao<T, ID> {

	// ==========Read==========
	Map<String, ?> findMapById(ID id);

	Iterable<Map<String, Object>> findAllMap();

	// Iterable<Map<String, Object>> findAllMapById(Iterable<ID> ids);

	@Deprecated
	Iterable<Map<String, Object>> findAllMapByGtId(ID id);

	// ==========Create==========
	int save(Map<String, ?> args);

	int saveWithKey(Map<String, ?> args);
	
	// ==========Update==========
	int updateAge(Map<String, ?> args);

	// ==========Delete==========
	int delete(Map<String, ?> args);

	@Deprecated
	public int[] batchSave(Map<String, ?>[] batchArgs);

	@Deprecated
	public int[] batchUpdateAge(Map<String, ?>[] batchArgs);

	@Deprecated
	int[] batchDelete(Map<String, ?>[] batchArgs);
}
