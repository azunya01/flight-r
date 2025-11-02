package com.sky.service;

import com.sky.dto.FlightSearchDTO;
import com.sky.vo.FlightListItemVO;

import java.util.List;

public interface FlightQueryService {
    List<FlightListItemVO> listFlights(FlightSearchDTO dto);
}
