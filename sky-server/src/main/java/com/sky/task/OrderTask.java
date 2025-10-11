package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
@EnableScheduling
public class OrderTask {
    @Autowired
    private OrderMapper orderMapper;
    @Scheduled(cron = "0 * * * * ?")// 每分钟执行一次
    //@Scheduled(cron = "0/5 * * * * ?") // 每5秒执行一次，测试用
    public void processOrders() {
        log.info("定时处理超时订单：{}", LocalDateTime.now().toString());
        LocalDateTime time = LocalDateTime.now().plusMinutes(-15);
        // 这里可以添加处理订单的逻辑
        List<Orders>  ordersList = orderMapper.getByStatusAndOrderTimeLT(Orders.PENDING_PAYMENT, time);
        if(!ordersList.isEmpty()){
            for(Orders orders:ordersList){
                orders.setStatus(Orders.CANCELLED);
                orders.setCancelReason("订单超时，系统自动取消");
                orders.setCancelTime(LocalDateTime.now());
                orderMapper.update(orders);
            }
        }
    }
    @Scheduled(cron = "0 0 1 * * ?") // 每天凌晨1点执行一次
    public void processDeliveryOrders() {
        log.info("定时处理超时派送订单：{}", LocalDateTime.now().toString());
        LocalDateTime time = LocalDateTime.now().plusMinutes(-60);
        // 这里可以添加处理订单的逻辑
        List<Orders>  ordersList = orderMapper.getByStatusAndOrderTimeLT(Orders.DELIVERY_IN_PROGRESS, time);
        if(!ordersList.isEmpty()){
            for(Orders orders:ordersList){
                orders.setStatus(Orders.COMPLETED);
                orders.setDeliveryTime(LocalDateTime.now());
                orderMapper.update(orders);
            }
        }
    }
}
