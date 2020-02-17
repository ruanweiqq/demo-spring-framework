package org.ruanwei.demo.springframework.dataAccess.springdata.jpa;

import java.sql.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.concurrent.ListenableFuture;

/**
 * 
 * @RepositoryDefinition 等价于 extends Repository<T,ID>.
 * see also SimpleJdbcRepository.@NoRepositoryBean
 * 
 * Spring Data JDBC supports defining a query manually only as a String in a @Query annotation. 
 * Deriving a query from the name of the method is currently not supported.
 * 
 * @author ruanwei
 *
 */
public interface UserJpaRepository extends JpaRepository<UserJpaEntity2, Integer> {
	// ====================single row====================
	@Query("select name from user where id = :id")
	String findNameById(@Param("id") int id);

	@Query(rowMapperClass = ColumnMapRowMapper.class, value = "select name, age from user where id = :id")
	Map<String, Object> findNameAndAgeById(@Param("id") int id);

	@Query("select * from user where id = :id")
	UserJpaEntity2 findUserById(@Param("id") int id);

	// ====================multiple row====================
	@Query("select name from user where id > :id")
	List<String> findNameListById(@Param("id") int id);

	@Query("select name, age from user where id > :id")
	List<Map<String, Object>> findNameAndAgeListById(@Param("id") int id);

	@Query("select * from user where id > :id")
	List<UserJpaEntity2> findUserListById(@Param("id") int id);

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
	default public void transactionalMethod1(UserJpaEntity2 user) {
		createUser(user.getName(), user.getAge(), user.getBirthday());

		transactionalMethod2(new UserJpaEntity2("ruanwei_tmp", 2, Date.valueOf("1983-07-06")));

		int i = 1 / 0;
	}

	// 不能在事务方法中进行try-catch
	default public void transactionalMethod2(UserJpaEntity2 user) {
		createUser(user.getName(), user.getAge(), user.getBirthday());
	}

	// ====================async query====================
	@Async
	@Query("select * from user")
	Future<List<UserJpaEntity2>> findAllUser1();

	@Async
	@Query("select * from user")
	CompletableFuture<List<UserJpaEntity2>> findAllUser2();

	@Async
	@Query("select * from user")
	ListenableFuture<List<UserJpaEntity2>> findAllUser3();
}
