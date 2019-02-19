package org.ruanwei.demo.springframework.web.user.dao;

import java.util.List;

import org.ruanwei.demo.core.exception.DataAccessException;
import org.ruanwei.demo.springframework.web.user.dao.entity.UserEntity;

public interface UserDao {
	public List<UserEntity> list4page(UserEntity instance) throws DataAccessException;

	public List<UserEntity> findByExample(UserEntity instance) throws DataAccessException;

	public long countByExample(UserEntity instance) throws DataAccessException;

	public UserEntity findById(long id) throws DataAccessException;

	public void save(UserEntity instance) throws DataAccessException;

	public void update(UserEntity instance) throws DataAccessException;

	public void delete(long id) throws DataAccessException;

	public void batchDelete(String[] ids) throws DataAccessException;

}