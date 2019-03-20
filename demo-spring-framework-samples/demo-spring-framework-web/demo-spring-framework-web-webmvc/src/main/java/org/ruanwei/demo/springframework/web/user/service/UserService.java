package org.ruanwei.demo.springframework.web.user.service;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.ruanwei.demo.springframework.web.user.service.dto.UserDTO;
import org.springframework.util.concurrent.ListenableFuture;

public interface UserService {

	public List<UserDTO> list4Page(@Valid @NotNull UserDTO user);

	public long count(UserDTO user);

	public List<UserDTO> find(UserDTO user);

	public UserDTO getUser(long id);
	
	public ListenableFuture<UserDTO> getUser0(long id);

	public UserDTO getUser1(long id);

	public UserDTO getUser2(long id);

	public UserDTO getUser3(long id);

	public UserDTO getUser4(long id);

	public UserDTO getUser5(long id);

	public UserDTO getUser6(long id);

	public void edit(UserDTO user);

	public void add(UserDTO user);

	public void deleteUser(long id);

	public void batchDeleteUser(String[] ids);

}
