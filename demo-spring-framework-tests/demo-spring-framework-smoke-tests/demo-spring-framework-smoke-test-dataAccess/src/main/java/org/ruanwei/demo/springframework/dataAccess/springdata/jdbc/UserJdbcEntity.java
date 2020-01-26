package org.ruanwei.demo.springframework.dataAccess.springdata.jdbc;

import java.sql.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;

@Data
@Table("user")
public class UserJdbcEntity {
	@Id
	private int id;
	@Column("name")
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

	/*public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}*/

	@Override
	public String toString() {
		return "UserJdbcEntity [id=" + id + ", name=" + name + ", age=" + age + ", birthday=" + birthday + "]";
	}

}
