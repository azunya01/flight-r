package com.sky.controller;

import com.sky.dto.FlightSearchDTO;
import com.sky.result.Result;
import com.sky.service.FlightQueryService;
import com.sky.vo.FlightListItemVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/flight")
@Api(tags = "航班查询接口")
@Slf4j
@RequiredArgsConstructor
public class FlightQueryController {

    private final FlightQueryService flightQueryService;

    @GetMapping("/list")
    @ApiOperation("按出发地/到达地/航班号/时间区间查询航班（含舱位）")
    public Result<List<FlightListItemVO>> listFlights(FlightSearchDTO dto) {
        log.info("航班搜索: {}", dto);
        List<FlightListItemVO> list = flightQueryService.listFlights(dto);
        return Result.success(list);
    }
}
