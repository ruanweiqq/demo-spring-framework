package org.ruanwei.demo.springframework.dataAccess.jdbc;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ruanwei.demo.springframework.dataAccess.TransactionalDao;
import org.ruanwei.demo.springframework.dataAccess.jdbc.entity.UserJdbcEntity;
import org.ruanwei.demo.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ParameterizedPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.EmptySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.object.MappingSqlQuery;
import org.springframework.jdbc.object.SqlQuery;
import org.springframework.jdbc.object.SqlUpdate;
import org.springframework.jdbc.object.StoredProcedure;
import org.springframework.jdbc.object.UpdatableSqlQuery;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
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
public class UserJdbcDao extends SimpleJdbcDao {
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
