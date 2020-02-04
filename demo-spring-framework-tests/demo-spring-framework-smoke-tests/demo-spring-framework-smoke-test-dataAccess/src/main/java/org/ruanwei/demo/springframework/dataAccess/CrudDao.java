package org.ruanwei.demo.springframework.dataAccess;

import java.sql.Date;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 一共有12个接口是必须实现的,除接口8外，剩下11个与org.springframework.data.repository.CrudRepository一致
 * 另外，接口6的语义目前还不一样
 * 
 * @author ruanwei
 *
 * @param <T>
 * @param <ID>
 */
public interface CrudDao<T, ID> extends TransactionalDao<T> {

	// ==========Create==========
	int save(T entity); // 1

	ID saveWithKey(T entity);

	int save(Map<String, ?> args);

	ID saveWithKey(Map<String, ?> args);

	int save(String name, int age, Date birthday);

	ID saveWithKey(String name, int age, Date birthday);

	int saveAll(Iterable<T> entities); // 2

	// ==========Read 1==========
	Optional<T> findById(ID id); // 3

	Map<String, ?> findMapById(ID id);

	boolean existsById(ID id); // 4

	Iterable<T> findAll(); // 5

	List<Map<String, Object>> findAllMap();

	Iterable<T> findAllById(Iterable<ID> ids); // 6

	List<T> findAllByGtId(ID id);

	List<Map<String, Object>> findAllMapById(ID id);

	long count(); // 7

	// ==========Read 2 with JdbcTemplate==========
	Optional<T> findById2(ID id);

	Map<String, ?> findMapById2(ID id);

	boolean existsById2(ID id);

	List<T> findAll2();

	List<Map<String, Object>> findAllMap2();

	List<T> findAllById2(Iterable<ID> ids);

	List<T> findAllByGtId2(ID id);

	List<Map<String, Object>> findAllMapById2(ID id);

	long count2();

	// ==========Update==========
	int updateAge(T entity); // 8

	int updateAge(Map<String, ?> args);

	// JdbcTemplate
	int updateAge(String name, int age, Date birthday);

	// ==========Delete==========
	int deleteById(ID id); // 9

	int delete(T entity); // 10

	int delete(Map<String, ?> args);

	// JdbcTemplate
	int delete(String name, int age, Date birthday);

	int deleteAll(Iterable<T> entities); // 11

	int deleteAll(); // 12

	// ==========Batch Create==========
	public int[] batchSave(T[] entities);

	public int[] batchSave(Map<String, Object>[] batchArgs);

	public int[] batchSave(Collection<T> entities);

	// JdbcTemplate
	public int[] batchSave(List<Object[]> batchArgs);

	// ==========Batch Update==========
	public int[] batchUpdateAge(T[] entities);

	public int[] batchUpdateAge(Collection<T> entities);

	public int[] batchUpdateAge(Map<String, Object>[] batchArgs);

	// JdbcTemplate
	public int[] batchUpdateAge(List<Object[]> batchArgs);

	// ==========Batch Delete==========
	int[] batchDelete(T[] entities);

	int[] batchDelete(Collection<T> entities);

	int[] batchDelete(Map<String, Object>[] batchArgs);

	// JdbcTemplate
	int[] batchDelete(List<Object[]> batchArgs);
}
