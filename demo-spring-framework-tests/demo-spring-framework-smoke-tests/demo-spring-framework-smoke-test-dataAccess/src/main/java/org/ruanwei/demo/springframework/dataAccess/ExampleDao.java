package org.ruanwei.demo.springframework.dataAccess;

import java.sql.Date;
import java.util.Map;

/**
 * 一共有11个接口是必须实现的,与org.springframework.data.repository.CrudRepository一致
 * 
 * @author ruanwei
 *
 * @param <T>
 * @param <ID>
 */
public interface ExampleDao {

	// =====NamedParameterJdbcTemplate=====


	// =====JdbcTemplate=====
	@Deprecated
	int save(String name, int age, Date birthday);

	@Deprecated
	int saveWithKey(String name, int age, Date birthday);

	@Deprecated
	int updateAge(String name, int age, Date birthday);

	@Deprecated
	int delete(String name, int age, Date birthday);
}
