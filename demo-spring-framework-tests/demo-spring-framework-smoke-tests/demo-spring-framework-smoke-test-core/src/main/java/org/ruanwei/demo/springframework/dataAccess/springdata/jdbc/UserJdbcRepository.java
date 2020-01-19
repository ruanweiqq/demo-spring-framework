package org.ruanwei.demo.springframework.dataAccess.springdata.jdbc;

import java.sql.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import org.ruanwei.demo.springframework.dataAccess.User;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.RepositoryDefinition;
import org.springframework.data.repository.query.Param;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.concurrent.ListenableFuture;

/**
 * 
 * @RepositoryDefinition 等价于 extends Repository<T,ID>.
 * see also SimpleJdbcRepository.
 * 
 * Spring Data JDBC supports defining a query manually only as a String in a @Query annotation. 
 * Deriving a query from the name of the method is currently not supported.
 * 
 * @author ruanwei
 *
 */
@Transactional
@RepositoryDefinition(domainClass = User.class, idClass = Integer.class)
public interface UserJdbcRepository extends PagingAndSortingRepository<User, Integer> {

	// 1 Create
	// CrudRepository#save
	// CrudRepository#saveAll

	// 2 Update
	@Modifying
	@Query("update user set name = :name,age = :age,birthday = :birthday where id = :id")
	int update(@Param("name") String name, @Param("age") int age);

	// 3.1 Read single row
	// CrudRepository.findById
	// CrudRepository.existsById
	@Transactional(readOnly = true)
	@Query("select * from user where id = :id")
	User findUserById(@Param("id") int id);

	// 3.2 Read multiple row
	// CrudRepository.findAll
	// CrudRepository.count

	// 4 Delete
	// CrudRepository.deleteById
	// CrudRepository.delete

	@Transactional(readOnly = true)
	@Query("select name from user where id = :id")
	String findNameById(@Param("id") int id);

	@Transactional(readOnly = true)
	@Query(rowMapperClass = ColumnMapRowMapper.class, value = "select name, age from user where id = :id")
	Map<String, Object> findNameAndAgeById(@Param("id") int id);

	// ====================multiple row====================
	@Transactional(readOnly = true)
	@Query("select name from user where id > :id")
	List<String> findNameListById(@Param("id") int id);

	@Transactional(readOnly = true)
	@Query("select name, age from user where id > :id")
	List<Map<String, Object>> findNameAndAgeListById(@Param("id") int id);

	@Transactional(readOnly = true)
	@Query("select * from user where id > :id")
	List<User> findUserListById(@Param("id") int id);

	// ====================update====================
	@Modifying
	@Query("insert into user(name, age, birthday) values(:name, :age, :birthday)")
	int createUser(@Param("name") String name, @Param("age") int age, @Param("birthday") Date birthday);

	@Modifying
	@Query("update user set age = :age where name = :name")
	int updateUser(@Param("name") String name, @Param("age") int age);

	@Modifying
	@Query("delete from user where id > :id")
	int deleteUser(@Param("id") int largerThanId);

	// ====================transactional====================
	// 不能在事务方法中进行try-catch
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { ArithmeticException.class })
	default public void transactionalMethod(User... users) {
		createUser(users[0].getName(), users[0].getAge(), users[0].getBirthday());
		createUser(users[1].getName(), users[1].getAge(), users[1].getBirthday());

		transactionalSubMethod(users[2], users[3]);

		int i = 1 / 0;
	}

	// 不能在事务方法中进行try-catch
	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = { ArithmeticException.class })
	default public void transactionalSubMethod(User... users) {
		createUser(users[0].getName(), users[0].getAge(), users[0].getBirthday());
		createUser(users[1].getName(), users[1].getAge(), users[1].getBirthday());
	}

	// ====================async query====================
	@Async
	@Transactional(readOnly = true)
	@Query("select * from user")
	Future<List<User>> findAllUser1();

	@Async
	@Transactional(readOnly = true)
	@Query("select * from user")
	CompletableFuture<List<User>> findAllUser2();

	@Async
	@Transactional(readOnly = true)
	@Query("select * from user")
	ListenableFuture<List<User>> findAllUser3();
}
