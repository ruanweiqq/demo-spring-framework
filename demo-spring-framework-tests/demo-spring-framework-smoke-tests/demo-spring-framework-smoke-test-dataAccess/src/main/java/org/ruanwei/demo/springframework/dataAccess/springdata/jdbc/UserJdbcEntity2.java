package org.ruanwei.demo.springframework.dataAccess.springdata.jdbc;

import java.sql.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;

@Data
@Table("user")
public class UserJdbcEntity2 {
	@Id
	private int id;
	@Column("name")
	private String name;
	private int age;
	private Date birthday;

	public UserJdbcEntity2() {
	}

	public UserJdbcEntity2(String name, int age, Date birthday) {
		this.name = name;
		this.age = age;
		this.birthday = birthday;
	}

	@Override
	public String toString() {
		return "UserJdbcEntity2 [id=" + id + ", name=" + name + ", age=" + age + ", birthday=" + birthday + "]";
	}

}
