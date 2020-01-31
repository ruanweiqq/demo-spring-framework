package org.ruanwei.demo.springframework.dataAccess.orm.mybatis;

import org.apache.ibatis.annotations.Select;
import org.ruanwei.demo.springframework.dataAccess.orm.mybatis.entity.UserMyBatisEntity;

//@Mapper
public interface UserMyBatisMapper {

	@Select("select * from user where id = #{id}")
	UserMyBatisEntity findById(int id);
}
