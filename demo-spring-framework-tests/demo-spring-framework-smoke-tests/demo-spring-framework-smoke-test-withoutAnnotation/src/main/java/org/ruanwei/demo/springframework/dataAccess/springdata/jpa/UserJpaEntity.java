package org.ruanwei.demo.springframework.dataAccess.springdata.jpa;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "user")
public class UserJpaEntity {
	@Id
	private int id;
	@Column
	private String name;
	private int age;
	private Date birthday;

	public UserJpaEntity() {
	}

	public UserJpaEntity(String name, int age, Date birthday) {
		this.name = name;
		this.age = age;
		this.birthday = birthday;
	}

	@Override
	public String toString() {
		return "UserJpaEntity [id=" + id + ", name=" + name + ", age=" + age + ", birthday=" + birthday + "]";
	}

}
