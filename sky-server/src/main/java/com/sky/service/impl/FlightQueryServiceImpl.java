package com.sky.service.impl;

import com.sky.dto.FlightSearchDTO;
import com.sky.mapper.FlightQueryMapper;
import com.sky.service.FlightQueryService;
import com.sky.vo.FlightListItemVO;
import com.sky.vo.SeatSummaryVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FlightQueryServiceImpl implements FlightQueryService {

    private final FlightQueryMapper mapper;

    @Override
    public List<FlightListItemVO> listFlights(FlightSearchDTO dto) {
        // 1) 先查航班
        List<FlightListItemVO> flights = mapper.searchFlights(dto);
        if (flights.isEmpty()) return flights;

        // 2) 批量查所有舱位
        List<String> flightIds = flights.stream()
                .map(FlightListItemVO::getFlightId)
                .distinct()
                .collect(Collectors.toList());

        List<SeatSummaryVO> seats = mapper.listSeatsByFlightIds(flightIds, dto.getFlightId());

        // 3) 分组装配
        Map<String, List<SeatSummaryVO>> seatMap =
                seats.stream().collect(Collectors.groupingBy(SeatSummaryVO::getFlightId));


        // 更稳妥：给 SeatSummaryVO 增加 flightId 字段（推荐，见下方修正版）
        // 这里我们改为直接在 SeatSummaryVO 增加 flightId 字段，然后用下面这句替代上面的复杂分组：
        // Map<String, List<SeatSummaryVO>> seatMap = seats.stream().collect(Collectors.groupingBy(SeatSummaryVO::getFlightId));
        // 4) 组装回每个航班
        for (FlightListItemVO f : flights) {
            List<SeatSummaryVO> list = seatMap.getOrDefault(f.getFlightId(), Collections.emptyList());
            f.setSeats(list);
        }
        return flights;
    }
}
