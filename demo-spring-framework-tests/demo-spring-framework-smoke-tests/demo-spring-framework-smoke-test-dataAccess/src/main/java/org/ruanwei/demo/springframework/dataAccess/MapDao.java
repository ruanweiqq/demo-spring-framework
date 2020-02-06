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

	int save(Map<String, ?> args);

	int saveWithKey(Map<String, ?> args);

	int updateAge(Map<String, ?> args);

	int delete(Map<String, ?> args);

	public int[] batchSave(Map<String, ?>[] batchArgs);

	public int[] batchUpdateAge(Map<String, ?>[] batchArgs);

	int[] batchDelete(Map<String, ?>[] batchArgs);
}
