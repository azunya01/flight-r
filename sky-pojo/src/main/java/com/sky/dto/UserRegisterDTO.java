package com.sky.dto;

import lombok.Data;

import java.io.Serializable;
@Data
public class UserRegisterDTO implements Serializable {
    private String userName;

    private String phone;

    private String password;

    private String sex;
}
