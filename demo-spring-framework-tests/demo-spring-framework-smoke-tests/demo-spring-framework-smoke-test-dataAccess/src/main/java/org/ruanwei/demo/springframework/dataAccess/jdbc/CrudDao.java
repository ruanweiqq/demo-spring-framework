package org.ruanwei.demo.springframework.dataAccess.jdbc;

import java.util.List;
import java.util.Map;

public interface CrudDao<T, ID> {

	int save(T entity);

	int saveMap(Map<String, ?> map);

	ID saveWithKey(T entity);

	ID saveMapWithKey(Map<String, ?> map);

	int saveAll(Iterable<T> entities);
	

	T findById(ID id);

	Map<String, ?> findMapById(ID id);

	boolean existsById(ID id);

	List<T> findAll();

	List<T> findAllById(ID id);

	List<Map<String, Object>> findAllMapById(ID id);

	long count();
	

	int deleteById(ID id);

	int delete(T entity);
	
	int deleteMap(Map<String, ?> map);

	int deleteAll(Iterable<? extends T> entities);

	int deleteAll();

	
}
