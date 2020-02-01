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
@NamedQueries({ @NamedQuery(name = "findAll", query = "select u from UserJpaEntity u"),
		@NamedQuery(name = "countAll", query = "select count(u) from UserJpaEntity u") })
@DynamicUpdate
@Table(name = "user")
@Entity
public class UserJpaEntity {

	@GenericGenerator(name = "increment", strategy = "increment")
	@GeneratedValue(generator = "increment")//查询最大值
	@Id
	private int id;

	@Column(name = "name")
	private String name;

	@Column(name = "age")
	private int age;

	// @Temporal(TemporalType.DATE)
	@Column(name = "birthday")
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
		return "UserJpaEntity [name=" + name + ", age=" + age + ", birthday=" + birthday + "]";
	}

}
