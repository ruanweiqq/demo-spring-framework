package org.ruanwei.demo.springframework.dataAccess;

import java.sql.Date;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface CrudDao<T, ID> extends TransactionnalDao<T> {

	// ==========Create==========
	int save(T entity);

	ID saveWithKey(T entity);

	int save(Map<String, ?> args);

	ID saveWithKey(Map<String, ?> args);

	int save(String name, int age, Date birthday);

	ID saveWithKey(String name, int age, Date birthday);

	int saveAll(Iterable<T> entities);

	// ==========Read 1==========
	T findById(ID id);

	Map<String, ?> findMapById(ID id);

	boolean existsById(ID id);

	List<T> findAll();

	List<Map<String, Object>> findAllMap();

	List<T> findAllById(ID id);

	List<Map<String, Object>> findAllMapById(ID id);

	long count();

	// ==========Read 2 with JdbcTemplate==========
	T findById2(ID id);

	Map<String, ?> findMapById2(ID id);

	boolean existsById2(ID id);

	List<T> findAll2();

	List<Map<String, Object>> findAllMap2();

	List<T> findAllById2(ID id);

	List<Map<String, Object>> findAllMapById2(ID id);

	long count2();

	// ==========Update==========
	int updateAge(T entity);

	int updateAge(Map<String, ?> args);

	// JdbcTemplate
	int updateAge(String name, int age, Date birthday);

	// ==========Delete==========
	int deleteById(ID id);

	int delete(T entity);

	int delete(Map<String, ?> args);

	// JdbcTemplate
	int delete(String name, int age, Date birthday);

	int deleteAll(Iterable<T> entities);

	int deleteAll();

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
