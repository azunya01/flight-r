package com.sky.vo;

import lombok.Data;

@Data
public class SeatSummaryVO {
    private String  flightId;         // ★ 新增，方便组装
    private Integer seatTypeId;
    private java.math.BigDecimal price;
    private Integer availableSeats;
}
