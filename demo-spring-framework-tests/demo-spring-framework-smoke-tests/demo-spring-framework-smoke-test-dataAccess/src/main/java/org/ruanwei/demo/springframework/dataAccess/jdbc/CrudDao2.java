package org.ruanwei.demo.springframework.dataAccess.jdbc;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface CrudDao2<T, ID> {

	// 1.Create
	int save(T entity);

	default int[] saveAll(List<T> entities) {
		throw new UnsupportedOperationException();
	}

	// 2.Update
	int update(T entity);

	// 3.1.Read single row
	Optional<T> findById(ID id);

	boolean existsById(ID id);

	boolean exists(T entity);

	Optional<T> findByExample(String sql, Object... argValues);

	Optional<T> findByExample(String sql, Map<String, ?> namedParams);

	// 3.2 Read multiple row
	List<T> findAll();

	long count();

	List<T> findAllById(List<ID> ids);

	List<T> findAllByExample(String sql, Object... argValues);

	List<T> findAllByExample(String sql, Map<String, ?> namedParams);

	// 4.Delete
	int deleteById(ID id);

	int delete(T entity);

	default int[] deleteAll(List<T> entities) {
		throw new UnsupportedOperationException();
	}

	int deleteAll();

	// 5.Save or Update
	int saveOrUpdate(T entity);

}
