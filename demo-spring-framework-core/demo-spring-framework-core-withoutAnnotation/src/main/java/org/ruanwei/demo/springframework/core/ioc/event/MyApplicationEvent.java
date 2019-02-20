package org.ruanwei.demo.springframework.core.ioc.event;

import org.springframework.context.ApplicationEvent;

@SuppressWarnings("serial")
public class MyApplicationEvent extends ApplicationEvent {
	private String message;

	public MyApplicationEvent(Object source, String message) {
		super(source);
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}
}
