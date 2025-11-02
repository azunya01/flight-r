package com.sky.entity;

import lombok.Data;

@Data
public class Flight {
    private String flightId;
    private String departureCity;
    private String arrivalCity;
    private  String departureTime;
    private String arrivalTime;
}
