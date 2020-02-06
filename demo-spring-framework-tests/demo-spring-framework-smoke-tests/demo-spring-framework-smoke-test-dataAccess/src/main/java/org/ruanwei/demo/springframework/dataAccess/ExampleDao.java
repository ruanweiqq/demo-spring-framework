package org.ruanwei.demo.springframework.dataAccess;

import java.sql.Date;
import java.util.List;

/**
 * 一共有11个接口是必须实现的,与org.springframework.data.repository.CrudRepository一致
 * 
 * @author ruanwei
 *
 * @param <T>
 * @param <ID>
 */
public interface ExampleDao {

	// =====JdbcTemplate=====
	@Deprecated
	int save(String name, int age, Date birthday);

	@Deprecated
	int saveWithKey(String name, int age, Date birthday);

	@Deprecated
	int updateAge(String name, int age, Date birthday);

	@Deprecated
	int delete(String name, int age, Date birthday);

	public int[] batchSave(List<Object[]> batchArgs);

	public int[] batchUpdateAge(List<Object[]> batchArgs);

	int[] batchDelete(List<Object[]> batchArgs);
}
