// com.sky.dto.PayDTO.java
package com.sky.dto;
import lombok.Data;
@Data
public class PayDTO {
    private Integer orderId;
    private String  channel; // ALIPAY/WECHAT/CARD...
}
