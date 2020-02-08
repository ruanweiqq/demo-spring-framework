package org.ruanwei.demo.springframework.dataAccess.dao;

import java.util.Map;

/**
 * 
 * @author ruanwei
 *
 * @param <T>
 * @param <ID>
 */
public interface MapDao {

	int save(Map<String, ?> args);

	int saveWithKey(Map<String, ?> args);

	int updateAge(Map<String, ?> args);

	int delete(Map<String, ?> args);

	public int[] batchSave(Map<String, ?>[] batchArgs);

	public int[] batchUpdateAge(Map<String, ?>[] batchArgs);

	int[] batchDelete(Map<String, ?>[] batchArgs);
}
