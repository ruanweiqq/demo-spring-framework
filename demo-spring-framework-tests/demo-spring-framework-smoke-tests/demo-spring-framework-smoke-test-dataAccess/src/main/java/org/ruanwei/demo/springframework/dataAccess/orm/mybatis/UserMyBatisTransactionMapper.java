package org.ruanwei.demo.springframework.dataAccess.orm.mybatis;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.ruanwei.demo.springframework.dataAccess.TransactionalDao;
import org.ruanwei.demo.springframework.dataAccess.orm.mybatis.entity.UserMyBatisEntity;
import org.springframework.transaction.annotation.Transactional;

@Transactional("transactionManager")
@Mapper
public interface UserMyBatisTransactionMapper extends TransactionalDao<UserMyBatisEntity> {

	// =====transaction=====
	// transactionalMethod1会回滚，transactionalMethod2不会回滚
	@Transactional(rollbackFor = ArithmeticException.class)
	@Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
	@Insert("insert into user(name,age,birthday) values(#{name}, #{age}, #{birthday})")
	@Override
	void transactionalMethod2(UserMyBatisEntity user);

	@Override
	default void transactionalMethod1(UserMyBatisEntity user) {
		throw new UnsupportedOperationException();
	};
}
