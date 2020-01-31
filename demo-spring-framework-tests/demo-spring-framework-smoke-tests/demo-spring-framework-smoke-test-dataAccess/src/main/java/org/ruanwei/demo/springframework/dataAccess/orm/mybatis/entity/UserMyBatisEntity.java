package org.ruanwei.demo.springframework.dataAccess.orm.mybatis.entity;

import java.sql.Date;

import lombok.Data;

@Data
public class UserMyBatisEntity {

	private int id;
	private String name;
	private int age;
	private Date birthday;

	public UserMyBatisEntity() {
	}

	public UserMyBatisEntity(String name, int age, Date birthday) {
		this.name = name;
		this.age = age;
		this.birthday = birthday;
	}
	
	@Override
	public String toString() {
		return "UserMyBatisEntity [name=" + name + ", age=" + age + ", birthday=" + birthday + "]";
	}

}
