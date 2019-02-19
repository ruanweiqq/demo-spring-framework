package org.ruanwei.demo.springframework.dataAccess.jdbc;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ruanwei.demo.springframework.dataAccess.CrudDao;
import org.ruanwei.demo.springframework.dataAccess.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.ParameterizedPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * JdbcDaoSupport提供了setDataSource支持 NamedParameterJdbcTemplate支持IN表达式
 * 
 * @author ruanwei
 *
 */
@Transactional
@Repository
public class UserJdbcDao extends JdbcDaoSupport implements CrudDao<User, Integer> {
	private static Log log = LogFactory.getLog(UserJdbcDao.class);

	@Required
	@Autowired
	public void setDataSource(@Qualifier("springDataSource") DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	// ====================CrudDao====================
	// 1.Create
	@Override
	public int save(User entity) {
		String sql = "insert into user(name, age, birthday) values(?, ?, ?)";

		PreparedStatementCreator psc = conn -> {
			PreparedStatement ps = conn.prepareStatement(sql, new String[] { "id" });
			ps.setString(1, entity.getName());
			ps.setInt(2, entity.getAge());
			ps.setDate(3, entity.getBirthday());
			return ps;
		};
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(psc, keyHolder);
		int key = keyHolder.getKey().intValue();

		return updateByExample(sql, entity.getName(), entity.getAge(), entity.getBirthday());
	}

	public int save2(User entity) {
		String sql = "insert into user(name, age, birthday) values(:name, :age, :birthday)";

		Map<String, Object> namedParam = new HashMap<String, Object>();
		namedParam.put("name", entity.getName());
		namedParam.put("age", entity.getAge());
		namedParam.put("birthday", entity.getBirthday());

		KeyHolder keyHolder = new GeneratedKeyHolder();
		namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(namedParam), keyHolder);
		int key = keyHolder.getKey().intValue();

		return updateByExample(sql, namedParam);
	}

	@Override
	public int[] saveAll(List<User> entities) {
		String sql = "insert into user(name, age, birthday) values(?, ?, ?)";

		ParameterizedPreparedStatementSetter<User> uppss = (ps, arg) -> {
			ps.setString(1, arg.getName());
			ps.setInt(2, arg.getAge());
			ps.setDate(3, arg.getBirthday());
		};
		jdbcTemplate.batchUpdate(sql, entities, 5, uppss);

		BatchPreparedStatementSetter bpss = new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ps.setString(1, entities.get(i).getName());
				ps.setInt(2, entities.get(i).getAge());
				ps.setDate(3, entities.get(i).getBirthday());
			}

			@Override
			public int getBatchSize() {
				return entities.size();
			}
		};
		jdbcTemplate.batchUpdate(sql, bpss);

		List<Object[]> batchArgs = new ArrayList<Object[]>();
		for (User user : entities) {
			Object[] values = new Object[] { user.getName(), user.getAge(), user.getBirthday() };
			batchArgs.add(values);
		}
		return batchUpdateByExample(sql, batchArgs);

	}

	public int[] saveAll2(List<User> entities) {
		String sql = "insert into user(name, age, birthday) values(:name, :age, :birthday)";

		Map<String, Object>[] batchValues = new HashMap[entities.size()];
		for (int i = 0; i < entities.size(); i++) {
			Map<String, Object> values = new HashMap<String, Object>();
			batchValues[i] = values;
			User user = entities.get(i);
			values.put("name", user.getName());
			values.put("age", user.getAge());
			values.put("birthday", user.getBirthday());
		}
		return batchUpdateByExample(sql, batchValues);
	}

	// 2.Update
	@Override
	public int update(User entity) {
		String sql = "update user set name = ? and age = ? and birthday = ? where id = ?";
		if (entity.getId() == 0) {
			throw new UnsupportedOperationException();
		}
		return updateByExample(sql, entity.getName(), entity.getAge(), entity.getBirthday(), entity.getId());
	}

	public int update2(User entity) {
		String sql = "update user set name = :name and age = :age and birthday = :birthday where id = :id";
		if (entity.getId() == 0) {
			throw new UnsupportedOperationException();
		}

		Map<String, Object> namedParam = new HashMap<String, Object>();
		namedParam.put("id", entity.getId());
		namedParam.put("name", entity.getName());
		namedParam.put("age", entity.getAge());
		namedParam.put("birthday", entity.getBirthday());
		return updateByExample(sql, namedParam);
	}

	// 3.1.Read single row
	@Transactional(readOnly = true)
	@Override
	public Optional<User> findById(Integer id) {
		String sql = "select * from user where id = ?";
		return findByExample(sql, id);
	}

	@Transactional(readOnly = true)
	public Optional<User> findById2(Integer id) {
		String sql = "select * from user where id = :id";
		Map<String, Integer> namedParam = new HashMap<String, Integer>();
		namedParam.put("id", id);
		return findByExample(sql, namedParam);
	}

	@Transactional(readOnly = true)
	@Override
	public boolean existsById(Integer id) {
		return findById(id).isPresent();
	}

	@Transactional(readOnly = true)
	public boolean existsById2(Integer id) {
		return findById2(id).isPresent();
	}

	@Transactional(readOnly = true)
	@Override
	public boolean exists(User entity) {
		String sql = "select * from user where name = ? and age = ? and birthday = ?";
		if (entity.getId() != 0) {
			return existsById(entity.getId());
		}

		return findByExample(sql, entity.getName(), entity.getAge(), entity.getBirthday()).isPresent();
	}

	@Transactional(readOnly = true)
	public boolean exists2(User entity) {
		String sql = "select * from user where name = :name and age = :age and birthday = :birthday";
		if (entity.getId() != 0) {
			return existsById2(entity.getId());
		}

		Map<String, Object> namedParam = new HashMap<String, Object>();
		namedParam.put("name", entity.getName());
		namedParam.put("age", entity.getAge());
		namedParam.put("birthday", entity.getBirthday());
		return findByExample(sql, namedParam).isPresent();
	}

	@Transactional(readOnly = true)
	@Override
	public Optional<User> findByExample(String sql, Object... args) {
		return findBeanByExample(sql, User.class, args);
	}

	@Transactional(readOnly = true)
	@Override
	public Optional<User> findByExample(String sql, Map<String, ?> namedParam) {
		return findBeanByExample(sql, User.class, namedParam);
	}

	// 3.2 Read multiple row
	@Transactional(readOnly = true)
	@Override
	public List<User> findAll() {
		String sql = "select * from user";
		return findAllBeanByExample(sql, User.class);
	}

	@Transactional(readOnly = true)
	@Override
	public long count() {
		String sql = "select count(*) from user";
		return findSingleColumnByExample(sql, Long.class).orElse(0L);
	}

	@Transactional(readOnly = true)
	@Override
	public List<User> findAllById(List<Integer> ids) {
		throw new UnsupportedOperationException();
	}

	@Transactional(readOnly = true)
	@Override
	public List<User> findAllByExample(String sql, Object... args) {
		return findAllBeanByExample(sql, User.class, args);
	}

	@Transactional(readOnly = true)
	@Override
	public List<User> findAllByExample(String sql, Map<String, ?> namedParam) {
		return findAllBeanByExample(sql, User.class, namedParam);
	}

	// 4.Delete
	@Override
	public int deleteById(Integer id) {
		String sql = "delete from user where id = ?";
		return updateByExample(sql, id);
	}

	public int deleteById2(Integer id) {
		String sql = "delete from user where id = :id";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("id", id);
		return updateByExample(sql, paramMap);
	}

	@Override
	public int delete(User entity) {
		String sql = "delete from user where name = ? and age = ? and birthday = ?";
		if (entity.getId() != 0) {
			return deleteById(entity.getId());
		}

		return updateByExample(sql, entity.getName(), entity.getAge(), entity.getBirthday());
	}

	public int delete2(User entity) {
		String sql = "delete from user where name = :name and age = :age and birthday = :birthday";
		if (entity.getId() != 0) {
			return deleteById2(entity.getId());
		}

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("name", entity.getName());
		paramMap.put("age", entity.getAge());
		paramMap.put("birthday", entity.getBirthday());
		return updateByExample(sql, paramMap);
	}

	@Override
	public int[] deleteAll(List<User> entities) {
		String sql = "delete from user where name = ? and age = ? and birthday = ?";

		List<Object[]> batchArgs = new ArrayList<Object[]>();
		for (User user : entities) {
			Object[] values = new Object[] { user.getName(), user.getAge(), user.getBirthday() };
			batchArgs.add(values);
		}
		return batchUpdateByExample(sql, batchArgs);
	}

	public int[] deleteAll2(List<User> entities) {
		String sql = "delete from user where name = ? and age = ? and birthday = ?";

		Map<String, Object>[] batchValues = new HashMap[entities.size()];
		for (int i = 0; i < entities.size(); i++) {
			Map<String, Object> values = new HashMap<String, Object>();
			batchValues[i] = values;
			User user = entities.get(i);
			values.put("name", user.getName());
			values.put("age", user.getAge());
			values.put("birthday", user.getBirthday());
		}
		return batchUpdateByExample(sql, batchValues);
	}

	@Override
	public int deleteAll() {
		String sql = "delete from user";
		return updateByExample(sql);
	}

	// 5.Save or Update
	@Override
	public int saveOrUpdate(User entity) {
		if (entity.getId() == 0) {
			return save(entity);
		} else {
			return update(entity);
		}
	}

	public int saveOrUpdate2(User entity) {
		if (entity.getId() == 0) {
			return save2(entity);
		} else {
			return update2(entity);
		}
	}

	// ====================transactional====================
	// 不能在事务方法中进行try-catch
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { ArithmeticException.class })
	public void transactionalMethod(User... users) {
		save(users[0]);
		save(users[1]);

		transactionalSubMethod(users[2], users[3]);

		int i = 1 / 0;
	}

	// 不能在事务方法中进行try-catch
	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = { ArithmeticException.class })
	private void transactionalSubMethod(User... users) {
		save(users[0]);
		save(users[1]);
	}

	private void createTable() {
		log.info("createTable()");
		String sql = "create table user(id int(11) not null AUTO_INCREMENT,name varchar(128) not null default '',gender int(11) not null default 0,age int(4) not null default 0";
		jdbcTemplate.execute(sql);
	}

}
