package org.ruanwei.demo.springframework.remoting.user.service;

import org.ruanwei.demo.core.exception.RemoteAccessException;
import org.ruanwei.demo.springframework.remoting.user.param.UserResp;

public interface UserBurlapService {

	public UserResp getUser(long id) throws RemoteAccessException;

	public void editUser(UserResp userResp) throws RemoteAccessException;

}
