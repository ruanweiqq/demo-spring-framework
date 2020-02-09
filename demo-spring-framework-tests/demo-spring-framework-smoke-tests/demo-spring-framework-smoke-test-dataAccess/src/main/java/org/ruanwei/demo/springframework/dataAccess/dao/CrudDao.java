package org.ruanwei.demo.springframework.dataAccess.dao;

import java.util.Optional;

/**
 * 一共13个接口，其中有11个接口是必须实现的,与org.springframework.data.repository.CrudRepository一致
 * 
 * @author ruanwei
 *
 * @param <T>
 * @param <ID>
 */
public interface CrudDao<T, ID> extends Dao<T, ID> {

	// ==========Read==========
	Optional<T> findById(ID id); // 1

	boolean existsById(ID id); // 2

	Iterable<T> findAll(); // 3

	Iterable<T> findAllById(Iterable<ID> ids); // 4

	@Deprecated
	Iterable<T> findAllByGtId(ID id);

	long count(); // 5

	// ==========Create==========
	int save(T entity); // 6

	int saveWithKey(T entity);

	int saveAll(Iterable<T> entities); // 7

	// ==========Update==========
	@Deprecated
	int updateAge(T entity);

	int update(T entity);

	// ==========Delete==========
	int deleteById(ID id); // 8

	int delete(T entity); // 9

	int deleteAll(Iterable<T> entities); // 10

	int deleteAll(); // 11
}
