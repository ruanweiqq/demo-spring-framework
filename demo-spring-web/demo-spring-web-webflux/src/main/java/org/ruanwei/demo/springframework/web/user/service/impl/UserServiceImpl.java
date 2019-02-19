package org.ruanwei.demo.springframework.web.user.service.impl;

import java.util.List;

import javax.sql.DataSource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ruanwei.demo.core.exception.DataAccessException;
import org.ruanwei.demo.core.exception.RemoteAccessException;
import org.ruanwei.demo.core.exception.ServiceException;
import org.ruanwei.demo.springframework.remoting.user.param.UserResp;
import org.ruanwei.demo.springframework.remoting.user.service.UserDubboService;
import org.ruanwei.demo.springframework.remoting.user.service.UserHessianService;
import org.ruanwei.demo.springframework.remoting.user.service.UserHttpInvokerService;
import org.ruanwei.demo.springframework.remoting.user.service.UserJmsService;
import org.ruanwei.demo.springframework.remoting.user.service.UserRmiService;
import org.ruanwei.demo.springframework.web.user.dao.UserDao;
import org.ruanwei.demo.springframework.web.user.dao.entity.UserEntity;
import org.ruanwei.demo.springframework.web.user.service.UserService;
import org.ruanwei.demo.util.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.validation.annotation.Validated;

@Validated
@Service("userService")
// @Transactional
public class UserServiceImpl implements UserService {
	private static final Logger logger = LogManager.getLogger();

	// @Autowired
	private UserHessianService userHessianService;

	// @Autowired
	private UserHttpInvokerService userHttpInvokerService;

	// @Autowired
	private UserRmiService userRmiService;

	// @Autowired
	private UserJmsService userJmsService;

	// @Autowired
	// @Resource
	private UserDubboService userDubboService;

	@Autowired
	private UserDao userDao;

	// private JdbcTemplate jdbcTemplate;

	// @Autowired
	// private RedisTemplate<String, String> redisTemplate;

	// @Resource(name = "redisTemplate")
	// private ListOperations<String, String> listOps;

	@Required
	@Autowired
	public void setDataSource(DataSource dataSource) {
		// this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public List<UserEntity> list4Page(@Valid @NotNull UserEntity userEntity) {
		logger.debug("list4Page user==================" + userEntity);
		List<UserEntity> userList;
		try {
			// add your code here.
			userList = userDao.list4page(userEntity);
		} catch (DataAccessException e) {
			logger.error(e.getMessage(), e);
			// add your code here.
			throw new ServiceException(e, -2);
		}
		return userList;
	}

	@Override
	public long count(UserEntity userEntity) {
		logger.debug("count user==================" + userEntity);
		long count;
		try {
			// add your code here.
			count = userDao.countByExample(userEntity);
		} catch (DataAccessException e) {
			logger.error(e.getMessage(), e);
			// add your code here.
			throw new ServiceException(e, -2);
		}
		return count;
	}

	@Override
	public List<UserEntity> find(UserEntity userEntity) {
		logger.debug("find user==================" + userEntity);
		List<UserEntity> list;
		try {
			// add your code here.
			list = userDao.findByExample(userEntity);
		} catch (DataAccessException e) {
			logger.error(e.getMessage(), e);
			// add your code here.
			throw new ServiceException(e, -2);
		}
		return list;
	}

	@Override
	public UserEntity getUser(long id) {
		logger.debug("getUser id==================" + id);
		UserEntity userEntity;
		try {
			// add your code here.
			userEntity = userDao.findById(id);
		} catch (DataAccessException e) {
			logger.error(e.getMessage(), e);
			// add your code here.
			throw new ServiceException(e, -2);
		}
		return userEntity;
	}

	@Override
	@Async
	public ListenableFuture<UserEntity> getUser0(long id) {
		logger.debug("getUser0 id==================" + id);
		UserEntity userEntity;
		try {
			// add your code here.
			userEntity = userDao.findById(id);
		} catch (DataAccessException e) {
			logger.error(e.getMessage(), e);
			// add your code here.
			throw new ServiceException(e, -2);
		}
		return new AsyncResult<UserEntity>(userEntity);
	}

	@Override
	public UserEntity getUser2(long id) {
		logger.debug("getUser2 id==================" + id);
		UserEntity userEntity;
		try {
			// add your code here.
			UserResp u = userHessianService
					.getUser(id);
			userEntity = BeanUtils.copy(u, UserEntity.class);
		} catch (RemoteAccessException e) {
			logger.error(e.getMessage(), e);
			// add your code here.
			throw new ServiceException(e, -2);
		}
		return userEntity;
	}

	@Override
	public UserEntity getUser3(long id) {
		logger.debug("getUser3 id==================" + id);
		UserEntity userEntity;
		try {
			// add your code here.
			UserResp u = userHttpInvokerService.getUser(id);
			userEntity = BeanUtils.copy(u, UserEntity.class);
		} catch (RemoteAccessException e) {
			logger.error(e.getMessage(), e);
			// add your code here.
			throw new ServiceException(e, -2);
		}
		return userEntity;
	}

	@Override
	public UserEntity getUser4(long id) {
		logger.debug("getUser4 id==================" + id);
		UserEntity userEntity;
		try {
			// add your code here.
			UserResp u = userRmiService.getUser(id);
			userEntity = BeanUtils.copy(u, UserEntity.class);
		} catch (RemoteAccessException e) {
			logger.error(e.getMessage(), e);
			// add your code here.
			throw new ServiceException(e, -2);
		}
		return userEntity;
	}

	@Override
	public UserEntity getUser5(long id) {
		logger.debug("getUser5 id==================" + id);
		UserEntity userEntity;
		try {
			// add your code here.
			UserResp u = userJmsService.getUser(id);
			userEntity = BeanUtils.copy(u, UserEntity.class);
		} catch (RemoteAccessException e) {
			logger.error(e.getMessage(), e);
			// add your code here.
			throw new ServiceException(e, -2);
		}
		return userEntity;
	}

	@Override
	public UserEntity getUser6(long id) {
		logger.debug("getUser6 id==================" + id);
		UserEntity userEntity;
		try {
			// add your code here.
			UserResp u = userDubboService.getUser(id);
			userEntity = BeanUtils.copy(u, UserEntity.class);
		} catch (RemoteAccessException e) {
			logger.error(e.getMessage(), e);
			// add your code here.
			throw new ServiceException(e, -2);
		}
		return userEntity;
	}

	@Override
	public void edit(UserEntity userEntity) {
		logger.debug("edit user==================" + userEntity);
		try {
			// add your code here.
			userDao.update(userEntity);
		} catch (DataAccessException e) {
			logger.error(e.getMessage(), e);
			// add your code here.
			throw new ServiceException(e, -2);
		}
	}

	@Override
	public void add(UserEntity userEntity) {
		logger.debug("add user==================" + userEntity);
		try {
			// add your code here.
			userDao.save(userEntity);
		} catch (DataAccessException e) {
			logger.error(e.getMessage(), e);
			// add your code here.
			throw new ServiceException(e, -2);
		}
	}

	@Override
	public void deleteUser(long id) {
		logger.debug("deleteUser id==================" + id);
		try {
			// add your code here.
			userDao.delete(id);
		} catch (DataAccessException e) {
			logger.error(e.getMessage(), e);
			// add your code here.
			throw new ServiceException(e, -2);
		}
	}

	@Override
	public void batchDeleteUser(String[] ids) {
		logger.debug("batchDeleteUser ids==================" + ids);
		try {
			// add your code here.
			userDao.batchDelete(ids);
		} catch (DataAccessException e) {
			logger.error(e.getMessage(), e);
			// add your code here.
			throw new ServiceException(e, -2);
		}
	}

	public static void main(String[] args) {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
				new String[] { "spring/test.xml" });
		context.registerShutdownHook();

		logger.info("======================================================================================");
		UserDubboService userDubboService = context.getBean("userDubboService",
				UserDubboService.class);
		try {
			logger.info("================================="
					+ userDubboService.getUser(1));
			Thread.currentThread().join();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
