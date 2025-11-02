package com.sky.dto;

import lombok.Data;

import java.io.Serializable;
@Data
public class PassengerDTO implements Serializable {
    private String phone;
    private String name;
    private String idNumber;
    private String gender;
    int seatTypeId;
}
