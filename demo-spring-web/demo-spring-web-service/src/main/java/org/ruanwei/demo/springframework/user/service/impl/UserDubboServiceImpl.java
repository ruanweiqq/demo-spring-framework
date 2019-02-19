package org.ruanwei.demo.springframework.user.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ruanwei.demo.core.exception.RemoteAccessException;
import org.ruanwei.demo.springframework.remoting.user.param.UserResp;
import org.ruanwei.demo.springframework.remoting.user.service.UserDubboService;
import org.ruanwei.demo.springframework.user.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("userDubboService")
public class UserDubboServiceImpl implements UserDubboService {
	private static final Logger logger = LogManager.getLogger();

	@Autowired
	private UserDao userDao;


	@Override
	public UserResp getUser(long id) throws RemoteAccessException {
		try {
			logger.debug("getUser(long id)" + id);

			// add your code here.

			UserResp userResp = userDao.findById(id);
			return userResp;
		} catch (Exception e) {
			// add your code here.
			logger.error(e.getMessage(), e);
			throw new RemoteAccessException(e, -3);
		}
	}
}
