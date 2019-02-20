package org.ruanwei.demo.springframework.dataAccess.orm.jpa.entity;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import lombok.Data;

@Data
@NamedQueries({ @NamedQuery(name = "findAll", query = "select u from UserEntity u"),
		@NamedQuery(name = "countAll", query = "select count(u) from UserEntity u") })
@DynamicUpdate
@Table(name = "user")
@Entity
public class UserEntity {

	@GenericGenerator(name = "increment", strategy = "increment")
	@GeneratedValue(generator = "increment")
	@Id
	private int id;

	@Column(name = "name")
	private String name;

	@Column(name = "age")
	private int age;

	// @Temporal(TemporalType.DATE)
	@Column(name = "birthday")
	private Date birthday;

	public UserEntity() {
	}

	public UserEntity(String name, int age, Date birthday) {
		this.name = name;
		this.age = age;
		this.birthday = birthday;
	}

	@Override
	public String toString() {
		return "User [name=" + name + ", age=" + age + ", birthday=" + birthday + "]";
	}

}
