package org.ruanwei.demo.springframework.dataAccess.jdbc;

import org.ruanwei.demo.springframework.dataAccess.jdbc.entity.UserJdbcEntity;
import org.springframework.context.ApplicationEvent;

@SuppressWarnings("serial")
public class UserSaveEvent extends ApplicationEvent {

	public UserSaveEvent(UserJdbcEntity source) {
		super(source);
	}

}
