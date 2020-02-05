package org.ruanwei.demo.springframework.dataAccess.orm.jpa;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ruanwei.demo.springframework.dataAccess.orm.jpa.entity.UserJpaEntity;
import org.springframework.stereotype.Repository;

/**
 * @author ruanwei
 *
 */
@Repository
public class UserJpaDao extends SimpleJpaDao<UserJpaEntity, Integer> {
	private static Log log = LogFactory.getLog(UserJpaDao.class);

}
