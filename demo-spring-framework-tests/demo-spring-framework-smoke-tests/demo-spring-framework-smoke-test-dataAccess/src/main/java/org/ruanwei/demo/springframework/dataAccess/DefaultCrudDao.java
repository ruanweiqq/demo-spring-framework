package org.ruanwei.demo.springframework.dataAccess;

import java.sql.Date;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class DefaultCrudDao<T, ID> implements CrudDao<T, ID> {

	// ==========Create==========
	@Override
	public ID saveWithKey(T entity) {
		throw new UnsupportedOperationException();
	};

	@Override
	public int save(Map<String, ?> args) {
		throw new UnsupportedOperationException();
	};

	@Override
	public ID saveWithKey(Map<String, ?> args) {
		throw new UnsupportedOperationException();
	};

	@Override
	public int save(String name, int age, Date birthday) {
		throw new UnsupportedOperationException();
	};

	@Override
	public ID saveWithKey(String name, int age, Date birthday) {
		throw new UnsupportedOperationException();
	};

	// ==========Read 1==========
	@Override
	public Map<String, ?> findMapById(ID id) {
		throw new UnsupportedOperationException();
	};

	@Override
	public List<Map<String, Object>> findAllMap() {
		throw new UnsupportedOperationException();
	};

	@Override
	public List<Map<String, Object>> findAllMapById(ID id) {
		throw new UnsupportedOperationException();
	};

	// ==========Read 2 with JdbcTemplate==========
	@Override
	public Optional<T> findById2(ID id) {
		throw new UnsupportedOperationException();
	};

	@Override
	public Map<String, ?> findMapById2(ID id) {
		throw new UnsupportedOperationException();
	};

	@Override
	public boolean existsById2(ID id) {
		throw new UnsupportedOperationException();
	};

	@Override
	public Iterable<T> findAll2() {
		throw new UnsupportedOperationException();
	};

	@Override
	public List<Map<String, Object>> findAllMap2() {
		throw new UnsupportedOperationException();
	};

	public Iterable<T> findAllById2(Iterable<ID> ids) {
		throw new UnsupportedOperationException();
	};

	@Override
	public List<T> findAllByGtId2(ID id) {
		throw new UnsupportedOperationException();
	};

	@Override
	public List<Map<String, Object>> findAllMapById2(ID id) {
		throw new UnsupportedOperationException();
	};

	@Override
	public long count2() {
		throw new UnsupportedOperationException();
	};

	// ==========Update==========
	@Override
	public int updateAge(Map<String, ?> args) {
		throw new UnsupportedOperationException();
	};

	// JdbcTemplate
	@Override
	public int updateAge(String name, int age, Date birthday) {
		throw new UnsupportedOperationException();
	};

	// ==========Delete==========
	@Override
	public int delete(Map<String, ?> args) {
		throw new UnsupportedOperationException();
	};

	// JdbcTemplate
	@Override
	public int delete(String name, int age, Date birthday) {
		throw new UnsupportedOperationException();
	};

	// ==========Batch Create==========
	@Override
	public int[] batchSave(T[] entities) {
		throw new UnsupportedOperationException();
	};

	@Override
	public int[] batchSave(Map<String, Object>[] batchArgs) {
		throw new UnsupportedOperationException();
	};

	@Override
	public int[] batchSave(Collection<T> entities) {
		throw new UnsupportedOperationException();
	};

	// JdbcTemplate
	@Override
	public int[] batchSave(List<Object[]> batchArgs) {
		throw new UnsupportedOperationException();
	};

	// ==========Batch Update==========
	@Override
	public int[] batchUpdateAge(T[] entities) {
		throw new UnsupportedOperationException();
	};

	@Override
	public int[] batchUpdateAge(Collection<T> entities) {
		throw new UnsupportedOperationException();
	};

	@Override
	public int[] batchUpdateAge(Map<String, Object>[] batchArgs) {
		throw new UnsupportedOperationException();
	};

	// JdbcTemplate
	@Override
	public int[] batchUpdateAge(List<Object[]> batchArgs) {
		throw new UnsupportedOperationException();
	};

	// ==========Batch Delete==========
	@Override
	public int[] batchDelete(T[] entities) {
		throw new UnsupportedOperationException();
	};

	@Override
	public int[] batchDelete(Collection<T> entities) {
		throw new UnsupportedOperationException();
	};

	@Override
	public int[] batchDelete(Map<String, Object>[] batchArgs) {
		throw new UnsupportedOperationException();
	};

	// JdbcTemplate
	@Override
	public int[] batchDelete(List<Object[]> batchArgs) {
		throw new UnsupportedOperationException();
	};

	// ===================待删除===========================
	public int update(T entity) {
		throw new UnsupportedOperationException();
	};

	public boolean exists(T entity) {
		throw new UnsupportedOperationException();
	};

	public T findByExample(String sql, Object... argValues) {
		throw new UnsupportedOperationException();
	};

	public T findByExample(String sql, Map<String, ?> namedParams) {
		throw new UnsupportedOperationException();
	};

	public List<T> findAllByIds(List<ID> ids) {
		throw new UnsupportedOperationException();
	};

	public List<T> findAllByExample(String sql, Object... argValues) {
		throw new UnsupportedOperationException();
	};

	public List<T> findAllByExample(String sql, Map<String, ?> namedParams) {
		throw new UnsupportedOperationException();
	};

	public int saveOrUpdate(T entity) {
		throw new UnsupportedOperationException();
	};

	public int[] deleteAll(List<T> entities) {
		throw new UnsupportedOperationException();
	};

	public int[] saveAll(List<T> entities) {
		throw new UnsupportedOperationException();
	};

	public Optional<T> findById3(Integer id) {
		throw new UnsupportedOperationException();
	};

}
