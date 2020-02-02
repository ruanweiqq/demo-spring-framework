package org.ruanwei.demo.springframework.dataAccess.orm.mybatis;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.ruanwei.demo.springframework.dataAccess.CrudDao;
import org.ruanwei.demo.springframework.dataAccess.orm.mybatis.entity.UserMyBatisEntity;

@Mapper
public interface UserMyBatisMapper extends CrudDao<UserMyBatisEntity, Integer> {

	// ==========Create==========
	@Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
	@Insert("insert into user(name,age,birthday) values(#{name}, #{age}, #{birthday})")
	@Override
	int save(UserMyBatisEntity entity);

	// see user-mapper.xml
	@Override
	int saveAll(Iterable<UserMyBatisEntity> users);

	// ==========Read 1==========
	@Select("select * from user where id = #{id}")
	@Override
	UserMyBatisEntity findById(@Param("id") Integer id);

	@Override
	default boolean existsById(@Param("id") Integer id) {
		return findById(id) != null;
	}

	@Select("select * from user")
	@Override
	List<UserMyBatisEntity> findAll();

	@Select("select * from user where id > #{id}")
	@Override
	List<UserMyBatisEntity> findAllById(@Param("id") Integer id);

	@Select("select count(*) from user")
	@Override
	long count();

	// ==========Update==========
	@Update("update user set age = #{age} where name = #{name} and birthday = #{birthday}")
	@Override
	int updateAge(UserMyBatisEntity user);

	// ==========Delete==========
	@Delete("delete from user where id = #{id}")
	@Override
	int deleteById(@Param("id") Integer id);

	@Delete("delete from user where name = #{name} and age = #{age} and birthday = #{birthday}")
	@Override
	int delete(UserMyBatisEntity user);

	// see user-mapper.xml
	@Override
	int deleteAll(Iterable<UserMyBatisEntity> users);

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
	@Override
	default void transactionalMethod1(UserMyBatisEntity user) {
		save(user);

		// transactionalMethod2()

		int i = 1 / 0;
	};

	@Override
	default void transactionalMethod2(UserMyBatisEntity user) {
		throw new UnsupportedOperationException();
	};
}
