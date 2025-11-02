package com.sky.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class BookDTO implements Serializable {
    Integer userId;
    String flightId;
    List<PassengerDTO> passengerList;
}
