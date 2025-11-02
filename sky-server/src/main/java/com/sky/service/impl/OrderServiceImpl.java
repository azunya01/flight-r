// com.sky.service.impl.OrderServiceImpl.java
package com.sky.service.impl;

import com.sky.dto.CancelDTO;
import com.sky.dto.PayDTO;
import com.sky.dto.RebookDTO;
import com.sky.enums.OrderStatus;
import com.sky.mapper.OrderMapper;
import com.sky.service.OrderService;
import com.sky.vo.OrderVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;

    @Transactional
    @Override
    public OrderVO pay(PayDTO dto) {
        Integer orderId = dto.getOrderId();
        String status = orderMapper.selectStatusByOrder(orderId);
        if (status == null) throw new RuntimeException("订单不存在");
        if (!OrderStatus.HOLD.name().equals(status)) {
            throw new RuntimeException("订单状态不允许支付: " + status);
        }
        BigDecimal amount = orderMapper.selectOrderTotal(orderId);
        if (amount == null) amount = BigDecimal.ZERO;

        int ok = orderMapper.markPaid(orderId, amount, dto.getChannel());
        if (ok != 1) throw new RuntimeException("支付失败或订单状态已改变");

        // 返回
        OrderVO vo = new OrderVO();
        vo.setOrderId(orderId);
        vo.setStatus(OrderStatus.PAID.name());
        vo.setTotalPrice(amount);
        vo.setFlightId(orderMapper.selectFlightIdByOrder(orderId));
        return vo;
    }

    @Transactional
    @Override
    public OrderVO cancel(CancelDTO dto) {
        Integer orderId = dto.getOrderId();
        String status = orderMapper.selectStatusByOrder(orderId);
        if (status == null) throw new RuntimeException("订单不存在");
        if (!status.equals(OrderStatus.HOLD.name()) && !status.equals(OrderStatus.PAID.name())) {
            throw new RuntimeException("订单状态不允许取消: " + status);
        }
        // 删除乘客 -> 触发器返还库存 & 订单总价会在 AFTER UPDATE/DELETE 触发器里更新
        orderMapper.deletePassengersByOrder(orderId);

        int ok = orderMapper.markCanceled(orderId, dto.getReason());
        if (ok != 1) throw new RuntimeException("取消失败或订单状态已改变");

        OrderVO vo = new OrderVO();
        vo.setOrderId(orderId);
        vo.setStatus(OrderStatus.CANCELED.name());
        vo.setFlightId(orderMapper.selectFlightIdByOrder(orderId));
        vo.setTotalPrice(BigDecimal.ZERO);
        return vo;
    }

    @Transactional
    @Override
    public OrderVO rebook(RebookDTO dto) {
        Integer oldOrderId = dto.getOrderId();
        String newFlightId = dto.getNewFlightId();

        String status = orderMapper.selectStatusByOrder(oldOrderId);
        if (status == null) throw new RuntimeException("原订单不存在");
        if (!status.equals(OrderStatus.PAID.name()) && !status.equals(OrderStatus.HOLD.name())) {
            throw new RuntimeException("当前状态不可改签: " + status);
        }

        Long userId = orderMapper.selectUserIdByOrder(oldOrderId).longValue();
        // 1) 座位检查：按原订单乘客 seatTypeId 统计，核对新航班可用座
        int shortage = orderMapper.countRebookShortage(oldOrderId, newFlightId);
        if (shortage > 0) {
            throw new RuntimeException("改签失败：新航班对应舱位余票不足");
        }

        // 2) 新建订单（HOLD）
        int newOrderId;
        orderMapper.insertOrderReturnId(newFlightId, OrderStatus.HOLD.name(), userId);
        // MyBatis useGeneratedKeys 会把键写入本地参数对象；这里简单起见再查一次也可
        // 为简洁，直接用 selectLastInsertId 模式：
        newOrderId = getLastInsertId(); // 如果你没有公共方法，可改 Mapper 再写一个 select LAST_INSERT_ID()

        // 3) 复制乘客到新订单（触发器会扣减新航班库存&计算价格&汇总）
        orderMapper.copyPassengersToNewOrder(oldOrderId, newOrderId);

        // 4) 删除旧订单乘客（触发器返还旧航班库存），并把旧单标记 REBOOKED
        orderMapper.deletePassengersByOrder(oldOrderId);
        orderMapper.markRebooked(oldOrderId, newOrderId);

        // 5) 返回新订单（HOLD），总价由触发器在插入乘客时已汇总
        OrderVO vo = new OrderVO();
        vo.setOrderId(newOrderId);
        vo.setStatus(OrderStatus.HOLD.name());
        vo.setFlightId(newFlightId);
        vo.setTotalPrice(orderMapper.selectOrderTotal(newOrderId));
        return vo;
    }

    // 你可以把 LAST_INSERT_ID() 做成一个通用 Mapper；这里给个简易实现思路：
    private int getLastInsertId() {
        // 建议在 OrderMapper 里补：
        // @Select("SELECT LAST_INSERT_ID()")
        // Integer lastInsertId();
        return orderMapper.lastInsertId();
    }
}
