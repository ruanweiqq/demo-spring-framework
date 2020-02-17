package org.ruanwei.demo.springframework.dataAccess.springdata.jdbc;

import java.sql.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import org.ruanwei.demo.springframework.dataAccess.springdata.User;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.concurrent.ListenableFuture;

/**
 * 
 * 加上 @RepositoryDefinition 等价于 extends Repository<T,ID>.
 * 
 * 加上 @NoRepositoryBean 的接口不会被实例化
 * 
 * Spring Data JDBC supports defining a query manually only as a String in a @Query annotation. 
 * Deriving a query from the name of the method is currently not supported.
 * 
 * SimpleJdbcRepository是默认实现.
 * 
 * @author ruanwei
 *
 */

@Transactional
@Repository
public interface UserJdbcRepository extends PagingAndSortingRepository<User, Integer> {
	// ====================single row====================
	@Query("select name from user where id = :id")
	String findNameById(@Param("id") int id);

	@Query(rowMapperClass = ColumnMapRowMapper.class, value = "select name, age from user where id = :id")
	Map<String, Object> findNameAndAgeById(@Param("id") int id);

	@Query("select * from user where id = :id")
	User findUserById(@Param("id") int id);

	// ====================multiple row====================
	@Query("select name from user where id > :id")
	List<String> findNameListById(@Param("id") int id);

	@Query("select name, age from user where id > :id")
	List<Map<String, Object>> findNameAndAgeListById(@Param("id") int id);

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

	// ====================transaction====================
	// 不能在事务方法中进行try-catch
	default public void transactionalMethod1(User user) {
		createUser(user.getName(), user.getAge(), user.getBirthday());


		int i = 1 / 0;
	}

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

	// 3.2 Read multiple row
	// CrudRepository.findAll
	// CrudRepository.count

	// 4 Delete
	// CrudRepository.deleteById
	// CrudRepository.delete

	// ====================multiple row====================

	// ====================async query====================
	@Async
	@Query("select * from user")
	Future<List<User>> findAllUser1();

	@Async
	@Query("select * from user")
	CompletableFuture<List<User>> findAllUser2();

	@Async
	@Query("select * from user")
	ListenableFuture<List<User>> findAllUser3();
}
