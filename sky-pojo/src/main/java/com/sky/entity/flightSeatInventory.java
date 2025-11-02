package com.sky.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class flightSeatInventory {
    private Long Id;
    private String flightId;
    private int seatTypeId;
    private String seatNo;
    private String status;
    private int orderID;
    private int passengerId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
