// com.sky.controller.OrderController.java
package com.sky.controller;

import com.sky.dto.CancelDTO;
import com.sky.dto.PayDTO;
import com.sky.dto.RebookDTO;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/order")
@Api(tags = "订单闭环接口")
@Slf4j
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/pay")
    @ApiOperation("订单支付（HOLD->PAID）")
    public Result<OrderVO> pay(@RequestBody PayDTO dto) {
        return Result.success(orderService.pay(dto));
    }

    @PostMapping("/cancel")
    @ApiOperation("订单取消（释放库存）")
    public Result<OrderVO> cancel(@RequestBody CancelDTO dto) {
        return Result.success(orderService.cancel(dto));
    }

    @PostMapping("/rebook")
    @ApiOperation("订单改签（旧单->REBOOKED，新单->HOLD）")
    public Result<OrderVO> rebook(@RequestBody RebookDTO dto) {
        return Result.success(orderService.rebook(dto));
    }
}
