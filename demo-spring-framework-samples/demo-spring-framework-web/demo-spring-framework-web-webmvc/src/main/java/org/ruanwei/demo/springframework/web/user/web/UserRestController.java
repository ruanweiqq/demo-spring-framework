package org.ruanwei.demo.springframework.web.user.web;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ruanwei.demo.springframework.web.conversion.http.JsonParam;
import org.ruanwei.demo.springframework.web.core.Page;
import org.ruanwei.demo.springframework.web.core.PagingResult;
import org.ruanwei.demo.springframework.web.core.Result;
import org.ruanwei.demo.springframework.web.user.dao.entity.UserEntity;
import org.ruanwei.demo.springframework.web.user.service.UserService;
import org.ruanwei.demo.springframework.web.user.service.dto.UserDTO;
import org.ruanwei.demo.springframework.web.user.web.form.UserForm;
import org.ruanwei.demo.springframework.web.user.web.form.conversion.UserTransUtils;
import org.ruanwei.demo.util.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 本文件演示：
 * <li>基于HttpMessageConverter(@ResponseBody)进行渲染的Controller. 
 * <li>支持内容协商.
 * 
 * @author ruanwei
 */
@Validated
@RestController
@RequestMapping(path = "/user/rest/")
public class UserRestController {
	private static final Logger logger = LogManager.getLogger();

	@Autowired
	private UserService userService;

	// http://127.0.0.1:8080/springweb-web/user/rest/list.json?curPage=1&pageSize=3&json={}
	@GetMapping(path = "list", produces = {
			MediaType.APPLICATION_JSON_UTF8_VALUE,
			MediaType.APPLICATION_XML_VALUE })
	public PagingResult<UserForm> list(@JsonParam @Valid @NotNull UserForm userForm, @Valid Page page) {
		logger.debug("list=" + userForm + page);

		// add your code here.

		UserDTO user = BeanUtils.copy(userForm, UserDTO.class);
		long totalRecord = userService.count(user);
		page.setTotalRecord(totalRecord);

		user.setStart(page.getPageSize() * (page.getCurPage() - 1));
		user.setOffset(page.getPageSize());

		List<UserDTO> list = userService.list4Page(user);
		List<UserForm> retList = UserTransUtils.trans2UserFormList(list);

		return PagingResult.<UserForm>builder2().page(page).list(retList).count(totalRecord).build();
	}
	@GetMapping(path = "list2", produces = {
			MediaType.APPLICATION_JSON_UTF8_VALUE,
			MediaType.APPLICATION_XML_VALUE })
	public PagingResult<UserForm> list2(@Valid @NotNull UserForm userForm, @Valid Page page) {
		return list(userForm, page);
	}

	@GetMapping(path = "{uid}")
	public Result<UserForm> get(@PathVariable("uid") @Min(0) int id) {
		logger.debug("get=" + id);

		// add your code here.

		UserForm user = getUser0(id);

		return Result.<UserForm>builder().data(user).build();
	}


	private UserForm getUser0(Integer id) {
		logger.debug("getUser0=" + id);

		UserEntity user = userService.getUser(id);
		logger.debug("1 jdbc======" + user);
		// user = userService.getUser2(id);
		// logger.debug("2 hessian======" + user.toString());
		// user = userService.getUser3(id);
		// logger.debug("3 rmi======" + user.toString());
		// user = userService.getUser4(id);
		// logger.debug("4 http invoker======" + user.toString());
		// user = userService.getUser5(id);
		// logger.debug("5 jms======" + user.toString());
		// user = userService.getUser6(id);
		// logger.debug("6 dubbo======" + user.toString());
		return UserTransUtils.trans2UserForm(user);
	}

}
