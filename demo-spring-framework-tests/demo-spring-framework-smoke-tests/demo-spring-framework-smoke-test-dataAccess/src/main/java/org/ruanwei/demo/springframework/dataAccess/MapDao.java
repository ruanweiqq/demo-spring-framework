package org.ruanwei.demo.springframework.dataAccess;

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

	// ============JdbcDao中的独特方法=============
	Map<String, ?> findMapById(ID id);

	Iterable<Map<String, Object>> findAllMap();

	// Iterable<Map<String, Object>> findAllMapById(Iterable<ID> ids);

	Iterable<Map<String, Object>> findAllMapByGtId(ID id);
}
