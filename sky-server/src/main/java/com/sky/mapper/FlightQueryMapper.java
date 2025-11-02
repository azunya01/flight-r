package com.sky.mapper;

import com.sky.dto.FlightSearchDTO;
import com.sky.vo.FlightListItemVO;
import com.sky.vo.SeatSummaryVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FlightQueryMapper {

    // 1) 查航班主信息（按条件过滤）
    List<FlightListItemVO> searchFlights(@Param("q") FlightSearchDTO q);

    // 2) 批量查每个航班的舱位+价格+余票（聚合）
    List<SeatSummaryVO> listSeatsByFlightIds(@Param("flightIds") List<String> flightIds,
                                             @Param("flightId") String flightIdFilter); // 兼容空列表时单航班查询
}
