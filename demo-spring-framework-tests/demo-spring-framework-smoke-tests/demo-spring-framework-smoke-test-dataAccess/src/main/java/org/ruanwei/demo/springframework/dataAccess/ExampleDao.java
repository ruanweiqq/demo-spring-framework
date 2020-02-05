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

	// ==========Create==========
	int save(Map<String, ?> args);

	int saveWithKey(Map<String, ?> args);

	// JdbcTemplate
	int save(String name, int age, Date birthday);

	// JdbcTemplate
	int saveWithKey(String name, int age, Date birthday);

	// ==========Read==========

	// ==========Update==========
	int updateAge(Map<String, ?> args);

	// JdbcTemplate
	int updateAge(String name, int age, Date birthday);

	// ==========Delete==========
	int delete(Map<String, ?> args);

	// JdbcTemplate
	int delete(String name, int age, Date birthday);
}
