 package org.ruanwei.demo.springframework.dataAccess.dao;

import java.lang.reflect.ParameterizedType;

/**
 * 接口层次关系如下：
 * <pre>
 * Dao
 *  |--CrudDao
 *      |--PagingAndSortingDao
 *      |--BatchDao
 *      |--ExtendDao
 *          |--JdbcDao
 *          |--JpaDao
 *          |--HibernateDao
 *          |--MyBatisDao
 *          |--JdbcExampleDao
 * MapDao
 *  |--BatchMapDao
 *      |--JdbcDao
 * ExampleDao
 *  |--JdbcExampleDao   
 * </pre>
 * 如图所示：
 * <li>JdbcDao实现了4个接口
 * <li>JdbcExampleDao实现了2个接口
 * <li>JdbcDao和JdbcExampleDao共同实现了ExtendDao
 * 
 * @author ruanwei
 *
 * @param <T>
 * @param <ID>
 */
public interface Dao<T, ID> {

	default Class<T> getTClass() {
		@SuppressWarnings("unchecked")
		Class<T> tClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass())
				.getActualTypeArguments()[0];
		return tClass;
	}
}
