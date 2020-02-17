package org.ruanwei.demo.springframework.dataAccess.springdata;

import java.sql.Date;
import java.util.Collection;
import java.util.Collections;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.domain.AfterDomainEventPublication;
import org.springframework.data.domain.DomainEvents;

import lombok.Data;

@Data
public class User {
	@Id
	private int id;
	private String name;
	private int age;
	private Date birthday;

	public User() {
	}

	public User(String name, int age, Date birthday) {
		this.name = name;
		this.age = age;
		this.birthday = birthday;
	}

	@PersistenceConstructor
	public User(int id, String name, int age, Date birthday) {
		this.id = id;
		this.name = name;
		this.age = age;
		this.birthday = birthday;
	}

	@DomainEvents
	Collection<Object> domainEvents() {
		// … return events you want to get published here
		return Collections.EMPTY_LIST;
	}

	@AfterDomainEventPublication
	void callbackMethod() {
		// … potentially clean up domain events list
	}

	@Override
	public String toString() {
		return "User [name=" + name + ", age=" + age + ", birthday=" + birthday + "]";
	}

}
