// com.sky.dto.RebookDTO.java
package com.sky.dto;
import lombok.Data;
@Data
public class RebookDTO {
    private Integer orderId;     // 原订单
    private String  newFlightId; // 新航班
    // 简化：沿用原乘客的 SeatTypeID；若需要变更，可扩展一个 map
}
