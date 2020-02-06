package org.ruanwei.demo.springframework.dataAccess;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 一共有11个接口是必须实现的,与org.springframework.data.repository.CrudRepository一致
 * 
 * @author ruanwei
 *
 * @param <T>
 * @param <ID>
 */
public interface BatchDao<T, ID> extends CrudDao<T, ID> {

	// ==========Batch Create==========
	public int[] batchSave(T[] entities);

	public int[] batchSave(Collection<T> entities);

	@Deprecated
	public int[] batchSave(Map<String, Object>[] batchArgs);

	// JdbcTemplate
	public int[] batchSave(List<Object[]> batchArgs);

	// ==========Batch Update==========
	public int[] batchUpdateAge(T[] entities);

	public int[] batchUpdateAge(Collection<T> entities);

	@Deprecated
	public int[] batchUpdateAge(Map<String, Object>[] batchArgs);

	// JdbcTemplate
	public int[] batchUpdateAge(List<Object[]> batchArgs);

	// ==========Batch Delete==========
	int[] batchDelete(T[] entities);

	int[] batchDelete(Collection<T> entities);

	@Deprecated
	int[] batchDelete(Map<String, Object>[] batchArgs);

	// JdbcTemplate
	int[] batchDelete(List<Object[]> batchArgs);
}
