package org.ruanwei.demo.springframework.dataAccess.orm.mybatis;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.transaction.annotation.Transactional;

@Transactional("transactionManager")
@Mapper
public interface UserMyBatisMapper extends SimpleMyBatisMapper {

}
