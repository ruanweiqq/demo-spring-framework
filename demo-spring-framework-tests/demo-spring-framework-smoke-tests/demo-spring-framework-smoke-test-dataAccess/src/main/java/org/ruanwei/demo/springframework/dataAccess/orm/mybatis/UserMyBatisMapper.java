package org.ruanwei.demo.springframework.dataAccess.orm.mybatis;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.ruanwei.demo.springframework.dataAccess.CrudDao;
import org.ruanwei.demo.springframework.dataAccess.orm.mybatis.entity.UserMyBatisEntity;
import org.springframework.transaction.annotation.Transactional;

@Transactional("transactionManager")
@Mapper
public interface UserMyBatisMapper extends CrudDao<UserMyBatisEntity, Integer> {

	// ==========Create==========
	@Override
	int save(UserMyBatisEntity entity); // 1

	@Override
	int saveAll(Iterable<UserMyBatisEntity> entities); // 2

	// ==========Read 1==========
	@Select("select * from user where id = #{id}")
	@Override
	UserMyBatisEntity findById(@Param("id") Integer id); // 3

	@Override
	boolean existsById(Integer id); // 4

	@Override
	List<UserMyBatisEntity> findAll(); // 5

	@Override
	List<UserMyBatisEntity> findAllById(Integer id); // 6

	@Override
	long count(); // 7

	// ==========Update==========
	@Override
	int updateAge(UserMyBatisEntity entity); // 8

	// ==========Delete==========
	@Override
	int deleteById(Integer id); // 9

	@Override
	int delete(UserMyBatisEntity entity); // 10

	@Override
	int deleteAll(Iterable<UserMyBatisEntity> entities); // 11

	@Override
	int deleteAll(); // 12

	// =====transaction=====
	// transactionalMethod1会回滚，transactionalMethod2不会回滚
	@Override
	@Transactional(rollbackFor = ArithmeticException.class)
	public void transactionalMethod1(UserMyBatisEntity user);

	@Override
	public void transactionalMethod2(UserMyBatisEntity user);
}
