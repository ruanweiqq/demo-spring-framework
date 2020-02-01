package org.ruanwei.demo.springframework.dataAccess.jdbc;

import org.springframework.context.ApplicationEvent;

@SuppressWarnings("serial")
public class UserSaveEvent extends ApplicationEvent {

	public UserSaveEvent(User source) {
		super(source);
	}

}
