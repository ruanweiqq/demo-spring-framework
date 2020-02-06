package org.ruanwei.demo.springframework.dataAccess.dao;

import java.util.Collection;

/**
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

	// ==========Batch Update==========
	public int[] batchUpdateAge(T[] entities);

	public int[] batchUpdateAge(Collection<T> entities);

	// ==========Batch Delete==========
	int[] batchDelete(T[] entities);

	int[] batchDelete(Collection<T> entities);
}
