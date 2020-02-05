package org.ruanwei.demo.springframework.dataAccess.orm.mybatis;

import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.transaction.annotation.Transactional;

@Transactional("transactionManager")
public interface SimpleMyBatisMapper<T, ID> extends MyBatisMapper<T, ID> {

	// ==========Create==========
	@Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
	@Insert("insert into user(name,age,birthday) values(#{name}, #{age}, #{birthday})")
	@Override
	int save(T entity);

	// see user-mapper.xml
	@Override
	int saveAll(Iterable<T> users);

	// ==========Read 1==========
	@Select("select * from user where id = #{id}")
	@Override
	Optional<T> findById(@Param("id") ID id);

	@Override
	default boolean existsById(@Param("id") ID id) {
		return findById(id) != null;
	}

	@Select("select * from user")
	@Override
	List<T> findAll();

	// see user-mapper.xml
	@Override
	List<T> findAllById(Iterable<ID> ids);

	@Select("select * from user where id > #{id}")
	@Override
	List<T> findAllByGtId(@Param("id") ID id);

	@Select("select count(*) from user")
	@Override
	long count();

	// ==========Update==========
	@Update("update user set age = #{age} where name = #{name} and birthday = #{birthday}")
	@Override
	int updateAge(T user);

	// ==========Delete==========
	@Delete("delete from user where id = #{id}")
	@Override
	int deleteById(@Param("id") ID id);

	@Delete("delete from user where name = #{name} and age = #{age} and birthday = #{birthday}")
	@Override
	int delete(T user);

	// see user-mapper.xml
	@Override
	int deleteAll(Iterable<T> users);

	// 由于不支持方法重载，去掉注解，将其改造成默认实现
	// @Delete("delete from user")
	@Override
	default int deleteAll() {
		return deleteAll(null);
	};

	// =====transaction=====
	// 1.事务是默认在抛出运行时异常进行回滚的，因此不能在事务方法中进行try-catch捕获
	// 2.事务是通过代理目标对象实现的，因此只有调用代理的事务方法才生效，调用目标对象(例如同一类中的其他方法)没有事务
	// 3.由于事务传播类型不同，transactionalMethod1会回滚，transactionalMethod2不会回滚
	// 4.事务应该应用在业务逻辑层而不是数据访问层，因此准备重构
	@Override
	@Transactional(rollbackFor = ArithmeticException.class)
	default void transactionalMethod1(T entity1, T entity2) {
		save(entity1);
	}

	default void transactionalMethod1(T user) {
		save(user);

		// transactionalMethod2()

		int i = 1 / 0;
	};

	@Override
	default void transactionalMethod2(T entity) {
		throw new UnsupportedOperationException();
	};
}
