package com.sky.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
public class FlightSearchDTO {
    private String departureCity;     // 出发地（可选）
    private String arrivalCity;       // 到达地（可选）
    private String flightId;          // 航班号（可选，精确匹配）

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime departTimeFrom;  // 出发时间 >= （可选）
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime departTimeTo;    // 出发时间 <= （可选）

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime arrivalTimeFrom; // 到达时间 >= （可选）
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime arrivalTimeTo;   // 到达时间 <= （可选）
}
