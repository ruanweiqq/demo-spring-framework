package org.ruanwei.demo.springframework.dataAccess;

import java.lang.reflect.ParameterizedType;
import java.sql.Date;
import java.util.Optional;

/**
 * 一共有11个接口是必须实现的,与org.springframework.data.repository.CrudRepository一致
 * 
 * @author ruanwei
 *
 * @param <T>
 * @param <ID>
 */
public interface CrudDao<T, ID> {

	// ==========Create==========
	int save(T entity); // 1

	int saveAll(Iterable<T> entities); // 2

	// ==========Read==========
	Optional<T> findById(ID id); // 3

	boolean existsById(ID id); // 4

	Iterable<T> findAll(); // 5

	Iterable<T> findAllById(Iterable<ID> ids); // 6

	Iterable<T> findAllByGtId(ID id);

	long count(); // 7

	// ==========Update==========
	int updateAge(T entity);

	// ==========Delete==========
	int deleteById(ID id); // 8

	int delete(T entity); // 9

	int deleteAll(Iterable<T> entities); // 10

	int deleteAll(); // 11

	default Class<T> getTClass() {
		Class<T> tClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass())
				.getActualTypeArguments()[0];
		return tClass;
	}
}
