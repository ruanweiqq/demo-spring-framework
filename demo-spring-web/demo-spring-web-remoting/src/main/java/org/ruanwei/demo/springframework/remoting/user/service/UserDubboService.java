package org.ruanwei.demo.springframework.remoting.user.service;

import org.ruanwei.demo.core.exception.RemoteAccessException;
import org.ruanwei.demo.springframework.remoting.user.param.UserResp;

public interface UserDubboService {

	public UserResp getUser(long id) throws RemoteAccessException;

}
