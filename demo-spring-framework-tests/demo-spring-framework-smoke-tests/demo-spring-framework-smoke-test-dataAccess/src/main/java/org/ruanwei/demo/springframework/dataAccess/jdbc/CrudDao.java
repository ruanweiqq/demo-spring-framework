package org.ruanwei.demo.springframework.dataAccess.jdbc;

import java.sql.Date;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.ruanwei.demo.springframework.dataAccess.User;

public interface CrudDao<T, ID> extends TransactionnalDao<T> {

	// Create
	int save(T entity);

	ID saveWithKey(T entity);

	int save(Map<String, ?> map);

	ID saveWithKey(Map<String, ?> map);

	int save(String name, int age, Date birthday);

	ID saveWithKey(String name, int age, Date birthday);

	int saveAll(Iterable<T> entities);

	// Read 1
	T findById(ID id);

	Map<String, ?> findMapById(ID id);

	boolean existsById(ID id);

	List<T> findAll();

	List<Map<String, Object>> findAllMap();

	List<T> findAllById(ID id);

	List<Map<String, Object>> findAllMapById(ID id);

	long count();

	// Read 2
	T findById2(ID id);// JdbcTemplate

	Map<String, ?> findMapById2(ID id);// JdbcTemplate

	boolean existsById2(ID id);// JdbcTemplate

	List<T> findAll2();// JdbcTemplate

	List<Map<String, Object>> findAllMap2();// JdbcTemplate

	List<T> findAllById2(ID id);// JdbcTemplate

	List<Map<String, Object>> findAllMapById2(ID id);// JdbcTemplate

	long count2();// JdbcTemplate

	// Update
	int updateAge(User user);

	int updateAge(Map<String, ?> userMap);

	int updateAge(String name, int age, Date birthday);// JdbcTemplate

	// Delete
	int deleteById(ID id);

	int delete(T entity);

	int delete(Map<String, ?> map);

	int delete(String name, int age, Date birthday);// JdbcTemplate

	int deleteAll(Iterable<? extends T> entities);

	int deleteAll();

	// Batch Create
	public int[] batchSave(User[] users);

	public int[] batchSave(Map<String, Object>[] users);

	public int[] batchSave(Collection<User> users);

	public int[] batchSave(List<Object[]> batchArgs);// JdbcTemplate

	// Batch Update
	public int[] batchUpdateAge(User[] users);

	public int[] batchUpdateAge(Collection<User> users);

	public int[] batchUpdateAge(Map<String, Object>[] users);

	public int[] batchUpdateAge(List<Object[]> batchArgs);// JdbcTemplate

	// Batch Delete
	public int[] batchDelete(User[] users);

	public int[] batchDelete(Collection<User> users);

	public int[] batchDelete(Map<String, Object>[] users);

	public int[] batchDelete(List<Object[]> batchArgs);// JdbcTemplate
}
