package org.ruanwei.demo.springframework.dataAccess.orm.mybatis;

import org.apache.ibatis.annotations.Mapper;
import org.ruanwei.demo.springframework.dataAccess.orm.mybatis.entity.UserMyBatisEntity;

@Mapper
public interface UserMyBatisMapper extends SimpleMyBatisMapper<UserMyBatisEntity, Integer> {

}
