package org.ruanwei.demo.springframework.dataAccess.jdbc;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;

@SuppressWarnings("serial")
public class SaveEvent<T, ID> extends ApplicationEvent {

	@Getter
	private ID id;

	public SaveEvent(T source) {
		super(source);
	}

	public SaveEvent(T source, ID id) {
		super(source);
		this.id = id;
	}
}
