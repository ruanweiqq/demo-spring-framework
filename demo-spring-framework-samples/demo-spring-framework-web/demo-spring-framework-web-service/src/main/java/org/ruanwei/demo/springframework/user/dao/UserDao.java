package org.ruanwei.demo.springframework.user.dao;

import java.util.List;

import org.ruanwei.demo.core.exception.DataAccessException;
import org.ruanwei.demo.springframework.remoting.user.param.UserResp;

public interface UserDao {
	public List<UserResp> list4page(UserResp instance) throws DataAccessException;

	public List<UserResp> findByExample(UserResp instance) throws DataAccessException;

	public long countByExample(UserResp instance) throws DataAccessException;

	public UserResp findById(long id) throws DataAccessException;

	public void save(UserResp instance) throws DataAccessException;

	public void update(UserResp instance) throws DataAccessException;

	public void delete(long id) throws DataAccessException;

	public void batchDelete(String[] ids) throws DataAccessException;

}