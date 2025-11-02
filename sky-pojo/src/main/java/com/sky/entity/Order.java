// com.sky.entity.Order
package com.sky.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Order {
    private Integer orderId;
    private Integer userId;
    private String  flightId;
    private String  status;

    // 列名若是 CreateAt/UpdateAt，就用同名属性最省事；否则写 ResultMap 做映射
    private LocalDateTime createAt;
    private LocalDateTime updateAt;

    private BigDecimal totalPrice;
}
