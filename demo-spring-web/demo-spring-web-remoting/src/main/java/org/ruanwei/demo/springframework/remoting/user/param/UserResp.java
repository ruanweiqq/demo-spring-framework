package org.ruanwei.demo.springframework.remoting.user.param;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;

@SuppressWarnings("serial")
public class UserResp implements Serializable {
	private int id;
	private String name = "";
	private int gender;
	private int age;
	private Date birthday;
	private int degree;
	private String cellphone;
	private String email;
	private int hobby;
	private String intro;
	private Timestamp lastUpdTime;

	private int start;
	private int offset;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
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

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public int getGender() {
		return gender;
	}

	public void setGender(int gender) {
		this.gender = gender;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public int getDegree() {
		return degree;
	}

	public void setDegree(int degree) {
		this.degree = degree;
	}

	public String getCellphone() {
		return cellphone;
	}

	public void setCellphone(String cellphone) {
		this.cellphone = cellphone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getHobby() {
		return hobby;
	}

	public void setHobby(int hobby) {
		this.hobby = hobby;
	}

	public String getIntro() {
		return intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}

	public Timestamp getLastUpdTime() {
		return lastUpdTime;
	}

	public void setLastUpdTime(Timestamp lastUpdTime) {
		this.lastUpdTime = lastUpdTime;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("User [id=").append(id).append(", name=").append(name)
				.append(", gender=").append(gender).append(", age=")
				.append(age).append(", birthday=").append(birthday)
				.append(", degree=").append(degree).append(", cellphone=")
				.append(cellphone).append(", email=").append(email)
				.append(", hobby=").append(hobby).append(", intro=")
				.append(intro).append(", lastUpdTime=").append(lastUpdTime)
				.append(", start=").append(start).append(", offset=")
				.append(offset).append("]");
		return builder.toString();
	}

}
