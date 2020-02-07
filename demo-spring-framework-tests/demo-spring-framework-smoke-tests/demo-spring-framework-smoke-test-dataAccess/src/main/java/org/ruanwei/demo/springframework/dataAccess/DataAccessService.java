package org.ruanwei.demo.springframework.dataAccess;

import org.ruanwei.demo.springframework.dataAccess.jdbc.SaveEvent;
import org.ruanwei.demo.springframework.dataAccess.jdbc.entity.UserJdbcEntity;
import org.ruanwei.demo.springframework.dataAccess.orm.hibernate.entity.UserHibernateEntity;
import org.ruanwei.demo.springframework.dataAccess.orm.jpa.entity.UserJpaEntity;
import org.ruanwei.demo.springframework.dataAccess.orm.mybatis.entity.UserMyBatisEntity;
import org.springframework.transaction.event.TransactionalEventListener;

public interface DataAccessService {

	void doSomethingWithJdbcTransaction(UserJdbcEntity user1, UserJdbcEntity user2);

	void doSomethingWithJdbcTransaction2(UserJdbcEntity user1, UserJdbcEntity user2);

	void doSomethingWithJpaTransaction(UserJpaEntity user1, UserJpaEntity user2);

	void doSomethingWithHibernateTransaction(UserHibernateEntity user1, UserHibernateEntity user2);

	void doSomethingWithMybatisTransaction(UserMyBatisEntity user1, UserMyBatisEntity user2);

	@TransactionalEventListener
	public void handleTransactionalEvent(SaveEvent<UserJdbcEntity, Integer> event);
}
