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

	// JdbcTemplate
	int save(String name, int age, Date birthday);

	int saveWithKey(String name, int age, Date birthday);

	int updateAge(String name, int age, Date birthday);

	int delete(String name, int age, Date birthday);
}
