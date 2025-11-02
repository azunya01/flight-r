package com.sky.vo;

import lombok.Data;

// com.sky.vo.PassengerVO
@Data
public class PassengerVO {
    private Long id;         // 映射 PassengerID（可选）
    private String name;
    private String phone;
    private String seatNo;
    private java.math.BigDecimal price;   // ★ 金额用 BigDecimal
}
