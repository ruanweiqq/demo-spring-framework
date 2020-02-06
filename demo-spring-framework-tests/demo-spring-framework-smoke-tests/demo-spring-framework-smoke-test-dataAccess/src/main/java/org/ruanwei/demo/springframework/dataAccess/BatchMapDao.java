package org.ruanwei.demo.springframework.dataAccess;

import java.util.Map;

/**
 * 
 * @author ruanwei
 *
 */
public interface BatchMapDao extends MapDao {

	public int[] batchSave(Map<String, ?>[] batchArgs);

	public int[] batchUpdateAge(Map<String, ?>[] batchArgs);

	int[] batchDelete(Map<String, ?>[] batchArgs);
}
