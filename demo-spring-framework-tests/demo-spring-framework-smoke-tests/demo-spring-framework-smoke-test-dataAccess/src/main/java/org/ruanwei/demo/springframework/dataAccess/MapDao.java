package org.ruanwei.demo.springframework.dataAccess;

import java.util.List;
import java.util.Map;

/**
 * 一共有11个接口是必须实现的,与org.springframework.data.repository.CrudRepository一致
 * 
 * @author ruanwei
 *
 * @param <T>
 * @param <ID>
 */
public interface MapDao<ID> {

	// ==========Create==========
	int save(Map<String, ?> args);

	ID saveWithKey(Map<String, ?> args);

	// ==========Read 1==========
	Map<String, ?> findMapById(ID id);

	Iterable<Map<String, Object>> findAllMap();

	Iterable<Map<String, Object>> findAllMapById(ID id);

	// ==========Update==========
	int updateAge(Map<String, ?> args);

	// ==========Delete==========
	int delete(Map<String, ?> args);
}
