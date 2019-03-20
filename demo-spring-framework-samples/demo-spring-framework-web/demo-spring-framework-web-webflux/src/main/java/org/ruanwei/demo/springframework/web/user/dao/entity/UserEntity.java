package org.ruanwei.demo.springframework.web.user.dao.entity;

import com.fasterxml.jackson.annotation.JsonView;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;


@SuppressWarnings("serial")
public class UserEntity implements Serializable {

    @JsonView(WithoutPageingView.class)
    @Min(value = 0, groups = {Update.class}, message = "ID必须大于0")
    private int id;

    @JsonView(WithoutPageingView.class)
    @Size(min = 0, max = 30, groups = {Create.class, Update.class}, message = "{user.name.limit}")
    private String name = "";

    @JsonView(WithoutPageingView.class)
    private int gender;

    @JsonView(WithoutPageingView.class)
    @NotNull
    @Min(value = 0, message = "{user.age.min}")
    @Max(value = 100, message = "{user.age.max}")
    private int age;

    @JsonView(WithoutPageingView.class)
    @Past
    private Date birthday;

    @JsonView(WithoutPageingView.class)
    private int degree;

    @JsonView(WithoutPageingView.class)
    @Pattern(regexp = "[0-9]{11}", message = "{cellphone.error}")
    private String cellphone;

    @JsonView(WithoutPageingView.class)
    private String email;

    @JsonView(WithoutPageingView.class)
    private int hobby;

    @JsonView(WithoutPageingView.class)
    @Size(min = 1, max = 50, message = "{user.intro}")
    private String intro;

    @JsonView(WithoutPageingView.class)
    private Timestamp lastUpdTime;

    @JsonView(WithPageingView.class)
    private int start;

    @JsonView(WithPageingView.class)
    private int offset;


    public interface WithoutPageingView {

    }


    public interface WithPageingView extends WithoutPageingView {

    }


    public interface Create {

    }


    public interface Update {

    }


    public UserEntity() {
    }


    public UserEntity(String name, int age) {
        this.name = name;
        this.age = age;
    }


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
        builder.append("User [id=").append(id).append(", name=").append(name).append(", gender=").append(gender).append(", age=").append(age).append(", birthday=").append(birthday).append(", " +
                "degree=").append(degree).append(", cellphone=").append(cellphone).append(", email=").append(email).append(", hobby=").append(hobby).append(", intro=").append(intro).append(", " +
                "lastUpdTime=").append(lastUpdTime).append(", start=").append(start).append(", offset=").append(offset).append("]");
        return builder.toString();
    }

}
