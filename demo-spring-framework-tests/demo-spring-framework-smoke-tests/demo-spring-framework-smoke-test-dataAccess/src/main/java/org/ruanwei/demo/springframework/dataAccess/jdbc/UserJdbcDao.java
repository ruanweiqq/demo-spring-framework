package org.ruanwei.demo.springframework.dataAccess.jdbc;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ruanwei.demo.springframework.dataAccess.jdbc.entity.UserJdbcEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * JdbcDaoSupport提供了setDataSource支持 NamedParameterJdbcTemplate支持IN表达式
 * 
 * @author ruanwei
 *
 */
@Transactional("transactionManager")
@Repository
public class UserJdbcDao extends SimpleJdbcDao<UserJdbcEntity,Integer> {
	private static Log log = LogFactory.getLog(UserJdbcDao.class);

	private static final String sql_select_by_id1 = "select * from user where id = ?";
	private static final String sql_select_by_id_namedParam1 = "select * from user where id = :id";

	private static final String sql_select_by_id2 = "select id, name, age, birthday from user where id = ?";
	private static final String sql_select_by_id_namedParam2 = "select id, name, age, birthday from user where id = :id";

	private static final String sql_select_by_ids1 = "select * from user where id in (?)";
	private static final String sql_select_by_ids_namedParam1 = "select * from user where id in (:ids)";

	private static final String sql_select_by_gt_id1 = "select id, name, age, birthday from user where id > ?";
	private static final String sql_select_by_gt_id_namedParam1 = "select id, name, age, birthday from user where id > :id";

	private static final String sql_select_by_gt_id2 = "select * from user where id > ?";
	private static final String sql_select_by_gt_id_namedParam2 = "select * from user where id > :id";

	private static final String sql_select_all1 = "select * from user";
	private static final String sql_select_all2 = "select id, name, age, birthday from user";

	private static final String sql_select_count = "select count(*) from user";

	private static final String sql_insert = "insert into user(name,age,birthday) values(?, ?, ?)";
	private static final String sql_insert_namedParam = "insert into user(name,age,birthday) values(:name, :age, :birthday)";

	private static final String sql_update_age = "update user set age = ? where name = ? and birthday = ?";
	private static final String sql_update_age_namedParam = "update user set age = :age where name = :name and birthday = :birthday";

	private static final String sql_delete = "delete from user where name = ? and age = ? and birthday = ?";
	private static final String sql_delete_namedParam = "delete from user where name = :name and age = :age and birthday = :birthday";

	private static final String sql_delete_all = "delete from user";

}
