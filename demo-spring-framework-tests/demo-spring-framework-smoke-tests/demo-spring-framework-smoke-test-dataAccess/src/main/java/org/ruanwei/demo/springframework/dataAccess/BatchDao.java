package org.ruanwei.demo.springframework.dataAccess;

import java.util.Collection;
import java.util.List;

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

	// JdbcTemplate
	public int[] batchSave(List<Object[]> batchArgs);

	// ==========Batch Update==========
	public int[] batchUpdateAge(T[] entities);

	public int[] batchUpdateAge(Collection<T> entities);

	// JdbcTemplate
	public int[] batchUpdateAge(List<Object[]> batchArgs);

	// ==========Batch Delete==========
	int[] batchDelete(T[] entities);

	int[] batchDelete(Collection<T> entities);

	// JdbcTemplate
	int[] batchDelete(List<Object[]> batchArgs);
}
