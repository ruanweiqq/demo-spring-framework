package org.ruanwei.demo.springframework.dataAccess.jdbc.entity;

import java.sql.Date;

import lombok.Data;

@Data
public class UserJdbcEntity {
	private int id;
	private String name;
	private int age;
	private Date birthday;

	public UserJdbcEntity() {
	}

	public UserJdbcEntity(String name, int age, Date birthday) {
		this.name = name;
		this.age = age;
		this.birthday = birthday;
	}

	public UserJdbcEntity(int id, String name, int age, Date birthday) {
		this.id = id;
		this.name = name;
		this.age = age;
		this.birthday = birthday;
	}

	@Override
	public String toString() {
		return "UserJdbcEntity [name=" + name + ", age=" + age + ", birthday=" + birthday + "]";
	}

}
