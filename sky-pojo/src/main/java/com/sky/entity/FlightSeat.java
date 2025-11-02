package com.sky.entity;

import lombok.Data;

@Data
public class FlightSeat {
    String flightId;
    int seatTypeId;
    String SeatName;
    int availableSeats;
    int totalSeats;
    Long priceMultiplier;
}
