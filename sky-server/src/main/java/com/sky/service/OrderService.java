// com.sky.service.OrderService.java
package com.sky.service;

import com.sky.dto.CancelDTO;
import com.sky.dto.PayDTO;
import com.sky.dto.RebookDTO;
import com.sky.vo.OrderVO;

public interface OrderService {
    OrderVO pay(PayDTO dto);
    OrderVO cancel(CancelDTO dto);
    OrderVO rebook(RebookDTO dto);
}
