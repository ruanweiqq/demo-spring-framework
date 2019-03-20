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
import org.ruanwei.demo.springframework.web.user.web.form.UserForm;
import org.ruanwei.demo.util.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

/**
 * 本文件演示： <li>基于HttpMessageConverter(@ResponseBody)进行渲染的Controller. <li>支持内容协商.
 * 
 * @author ruanwei
 */
@RestController
@RequestMapping(path = "/user/rest/")
public class UserRestController {
	private static final Logger logger = LogManager.getLogger();

	@Autowired
	private UserService userService;

	@GetMapping(path = "hello")
	public Mono<String> hello() {
		logger.debug("hello=");

		// add your code here.

		return Mono.just("Hello World");
	}

	// TODO:按照统一的返回类型，返回rest格式，列表，对象，映射
	@GetMapping(path = "list", produces = {
			MediaType.APPLICATION_JSON_UTF8_VALUE,
			MediaType.APPLICATION_XML_VALUE })
	public PagingResult<UserEntity> list(
			@Valid @NotNull @JsonParam(required = false) UserForm userForm,
			Page page) {
		logger.debug("list=" + userForm + page);

		// add your code here.

		UserEntity userEntity = BeanUtils.copy(userForm, UserEntity.class);
		long totalRecord = userService.count(userEntity);
		page.setTotalRecord(totalRecord);

		userEntity.setStart(page.getPageSize() * (page.getCurPage() - 1));
		userEntity.setOffset(page.getPageSize());

		List<UserEntity> list = userService.list4Page(userEntity);

		return PagingResult.<UserEntity>builder2().page(page).list(list).count(totalRecord)
				.build();
	}

	@GetMapping(path = "{uid}")
	public Result<UserEntity> get(@PathVariable("uid") @Min(0) int id) {
		logger.debug("get=" + id);

		// add your code here.

		UserEntity userEntity = getUser0(id);

		return Result.<UserEntity>builder().data(userEntity).build();
	}

	private UserEntity getUser0(Integer id) {
		logger.debug("getUser0=" + id);

		UserEntity userEntity = userService.getUser(id);
		logger.debug("1 jdbc======" + userEntity);
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
		return userEntity;
	}

}
