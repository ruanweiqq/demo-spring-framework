package org.ruanwei.demo.springframework.dataAccess;

import java.sql.Date;
import java.util.List;
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

	ID saveWithKey(T entity);

	int save(String name, int age, Date birthday);

	ID saveWithKey(String name, int age, Date birthday);

	int saveAll(Iterable<T> entities); // 2

	// ==========Read==========
	Optional<T> findById(ID id); // 3

	boolean existsById(ID id); // 4

	Iterable<T> findAll(); // 5

	Iterable<T> findAllById(Iterable<ID> ids); // 6

	List<T> findAllByGtId(ID id);

	long count(); // 7

	// ==========Update==========
	int updateAge(T entity);

	// JdbcTemplate
	int updateAge(String name, int age, Date birthday);

	// ==========Delete==========
	int deleteById(ID id); // 8

	int delete(T entity); // 9

	// JdbcTemplate
	int delete(String name, int age, Date birthday);

	int deleteAll(Iterable<T> entities); // 10

	int deleteAll(); // 11
}
