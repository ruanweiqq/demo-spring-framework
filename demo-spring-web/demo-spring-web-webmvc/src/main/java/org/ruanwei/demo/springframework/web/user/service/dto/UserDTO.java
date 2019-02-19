package org.ruanwei.demo.springframework.web.user.service.dto;

import org.ruanwei.demo.springframework.web.user.dao.entity.UserEntity;


public class UserDTO extends UserEntity {
    public UserDTO() {
    }


    public UserDTO(String name, int age) {
        super(name, age);
    }
}
