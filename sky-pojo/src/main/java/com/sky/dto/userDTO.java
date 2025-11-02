package com.sky.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class userDTO implements Serializable {

    private Long userId;

    private String userName;

    private String name;

    private String phone;

    private String sex;

    private String idNumber;

}
