package org.ruanwei.demo.springframework.dataAccess;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ruanwei.demo.springframework.dataAccess.jdbc.JdbcDao;
import org.ruanwei.demo.springframework.dataAccess.jdbc.JdbcExampleDao;
import org.ruanwei.demo.springframework.dataAccess.jdbc.SaveEvent;
import org.ruanwei.demo.springframework.dataAccess.jdbc.entity.UserJdbcEntity;
import org.ruanwei.demo.springframework.dataAccess.orm.hibernate.HibernateDao;
import org.ruanwei.demo.springframework.dataAccess.orm.hibernate.entity.UserHibernateEntity;
import org.ruanwei.demo.springframework.dataAccess.orm.jpa.JpaDao;
import org.ruanwei.demo.springframework.dataAccess.orm.jpa.entity.UserJpaEntity;
import org.ruanwei.demo.springframework.dataAccess.orm.mybatis.MyBatisMapper;
import org.ruanwei.demo.springframework.dataAccess.orm.mybatis.entity.UserMyBatisEntity;
import org.ruanwei.demo.springframework.dataAccess.springdata.jdbc.UserJdbcRepository;
import org.ruanwei.demo.springframework.dataAccess.springdata.jpa.UserJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 1.事务是默认在抛出运行时异常进行回滚的，因此不能在事务方法中进行try-catch捕获
 * 2.事务是通过代理目标对象实现的，因此只有调用代理的事务方法才生效，调用目标对象(例如同一类中的其他方法)没有事务
 * 3.事务应该应用在业务逻辑层而不是数据访问层
 * 
 * @author ruanwei
 *
 */
@Service
public class DataAccessServiceImpl implements DataAccessService {
	private static Log log = LogFactory.getLog(DataAccessServiceImpl.class);

	@Autowired
	private JdbcDao<UserJdbcEntity, Integer> userJdbcDao;

	@Autowired
	private JdbcExampleDao<UserJdbcEntity, Integer> userJdbcExampleDao;

	@Autowired
	private JpaDao<UserJpaEntity, Integer> userJpaDao;

	@Autowired
	private HibernateDao<UserHibernateEntity, Integer> userHibernateDao;

	@Autowired
	private MyBatisMapper<UserMyBatisEntity, Integer> userMyBatisMapper;

	// @Autowired
	private UserJdbcRepository userJdbcRepository;

	// @Autowired
	private UserJpaRepository userJpaRepository;

	@Autowired
	private ApplicationEventPublisher applicationEventPublisher;

	@Transactional(transactionManager = "transactionManager", rollbackFor = ArithmeticException.class)
	@Override
	public void doSomethingWithJdbcTransaction(UserJdbcEntity user1, UserJdbcEntity user2) {
		log.info("doSomethingWithJdbcTransaction(UserJdbcEntity user1, UserJdbcEntity user2)");

		// 这个由于REQUIRED，ArithmeticException会回滚
		userJdbcDao.save(user1);
		applicationEventPublisher.publishEvent(new SaveEvent<UserJdbcEntity, Integer>(user1, 111));

		// 这个由于Propagation.REQUIRES_NEW不会回滚
		userJdbcDao.saveWithKey(user2);
		applicationEventPublisher.publishEvent(new SaveEvent<UserJdbcEntity, Integer>(user2, 222));

		// 抛出ArithmeticException
		int i = 1 / 0;
	}

	@Transactional(transactionManager = "transactionManager", rollbackFor = ArithmeticException.class)
	@Override
	public void doSomethingWithJdbcTransaction2(UserJdbcEntity user1, UserJdbcEntity user2) {
		userJdbcExampleDao.save(user1);
		userJdbcExampleDao.saveWithKey(user2);
		int i = 1 / 0;
	}

	@Transactional(transactionManager = "jpaTransactionManager", rollbackFor = ArithmeticException.class)
	@Override
	public void doSomethingWithJpaTransaction(UserJpaEntity user1, UserJpaEntity user2) {
		userJpaDao.save(user1);
		userJpaDao.saveWithKey(user2);
		int i = 1 / 0;
	}

	@Transactional(transactionManager = "hibernateTransactionManager", rollbackFor = ArithmeticException.class)
	@Override
	public void doSomethingWithHibernateTransaction(UserHibernateEntity user1, UserHibernateEntity user2) {
		userHibernateDao.save(user1);
		userHibernateDao.saveWithKey(user2);
		int i = 1 / 0;
	}

	@Transactional(transactionManager = "transactionManager", rollbackFor = ArithmeticException.class)
	@Override
	public void doSomethingWithMybatisTransaction(UserMyBatisEntity user1, UserMyBatisEntity user2) {
		userMyBatisMapper.save(user1);
		userMyBatisMapper.saveWithKey(user2);
		int i = 1 / 0;
	}

	// TODO: It does't work, why?
	@TransactionalEventListener(fallbackExecution = true)
	@Override
	public void handleTransactionalEvent(SaveEvent<UserJdbcEntity, Integer> event) {
		log.info("handleTransactionalEvent" + event);
		log.info("event======================================" + event);
	}
}
