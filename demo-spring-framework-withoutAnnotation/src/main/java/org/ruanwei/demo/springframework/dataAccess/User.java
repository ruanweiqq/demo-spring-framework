package org.ruanwei.demo.springframework.dataAccess;

import java.sql.Date;

import org.springframework.data.annotation.Id;

import lombok.Data;

@Data
public class User {
	@Id
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
		return "User [name=" + name + ", age=" + age + ", birthday=" + birthday
				+ "]";
	}

}
