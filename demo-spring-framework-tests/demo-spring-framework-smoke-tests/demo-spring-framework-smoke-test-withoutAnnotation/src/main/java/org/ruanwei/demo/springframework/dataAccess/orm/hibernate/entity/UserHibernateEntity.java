package org.ruanwei.demo.springframework.dataAccess.orm.hibernate.entity;

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
@NamedQueries({ @NamedQuery(name = "hibernate.findAll", query = "select u from UserHibernateEntity u"),
		@NamedQuery(name = "hibernate.countAll", query = "select count(u) from UserHibernateEntity u") })
@DynamicUpdate
@Table(name = "user")
@Entity
public class UserHibernateEntity {

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

	public UserHibernateEntity() {
	}

	public UserHibernateEntity(String name, int age, Date birthday) {
		this.name = name;
		this.age = age;
		this.birthday = birthday;
	}

	@Override
	public String toString() {
		return "UserHibernateEntity [name=" + name + ", age=" + age + ", birthday=" + birthday + "]";
	}

}
