package org.ruanwei.demo.springframework.web.user.service;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.ruanwei.demo.springframework.web.user.dao.entity.UserEntity;
import org.springframework.util.concurrent.ListenableFuture;

public interface UserService {

	public List<UserEntity> list4Page(@Valid @NotNull UserEntity userEntity);

	public long count(UserEntity userEntity);

	public List<UserEntity> find(UserEntity userEntity);

	public UserEntity getUser(long id);
	
	public ListenableFuture<UserEntity> getUser0(long id);

	public UserEntity getUser2(long id);

	public UserEntity getUser3(long id);

	public UserEntity getUser4(long id);

	public UserEntity getUser5(long id);

	public UserEntity getUser6(long id);

	public void edit(UserEntity userEntity);

	public void add(UserEntity userEntity);

	public void deleteUser(long id);

	public void batchDeleteUser(String[] ids);

}
