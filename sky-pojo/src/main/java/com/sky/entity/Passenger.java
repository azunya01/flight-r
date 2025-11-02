package com.sky.entity;

import lombok.Data;

import javax.validation.constraints.DecimalMin;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class Passenger implements Serializable {
    private Integer passengerId;
    private Integer userId;
    private Integer orderId;
    private String name;
    private String idNumber;
    private String phone;
    private int seatTypeId;
    private String gender;
    private BigDecimal price;
}
