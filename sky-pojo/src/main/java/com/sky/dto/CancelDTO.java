// com.sky.dto.CancelDTO.java
package com.sky.dto;
import lombok.Data;
@Data
public class CancelDTO {
    private Integer orderId;
    private String  reason;  // 可选
}
