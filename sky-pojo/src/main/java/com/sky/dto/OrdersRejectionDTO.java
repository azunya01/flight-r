package com.sky.dto;

import lombok.Data;
import org.springframework.context.annotation.Bean;

import java.io.Serializable;

@Data
public class OrdersRejectionDTO implements Serializable {

    private Long id;

    //订单拒绝原因
    private String rejectionReason;

}
