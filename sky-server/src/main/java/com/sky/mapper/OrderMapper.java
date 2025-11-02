// com.sky.mapper.OrderMapper.java
package com.sky.mapper;

import com.sky.vo.PassengerVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface OrderMapper {

    // 基本获取
    Integer selectUserIdByOrder(@Param("orderId") Integer orderId);
    String  selectFlightIdByOrder(@Param("orderId") Integer orderId);
    String  selectStatusByOrder(@Param("orderId") Integer orderId);
    BigDecimal selectOrderTotal(@Param("orderId") Integer orderId);

    // 支付
    int markPaid(@Param("orderId") Integer orderId,
                 @Param("amount")  BigDecimal amount,
                 @Param("channel") String channel);

    // 取消
    int markCanceled(@Param("orderId") Integer orderId,
                     @Param("reason")  String reason);

    // 改签：新建订单
    int insertOrderReturnId(@Param("flightId") String flightId,
                            @Param("status")   String status,
                            @Param("userId")   Long userId);

    // 复制乘客到新订单（触发器会扣减新航班库存）
    int copyPassengersToNewOrder(@Param("oldOrderId") Integer oldOrderId,
                                 @Param("newOrderId") Integer newOrderId);

    // 删除旧订单乘客（触发器会返还旧航班库存）
    int deletePassengersByOrder(@Param("orderId") Integer orderId);

    // 旧单标记 REBOOKED，并记录关联
    int markRebooked(@Param("oldOrderId") Integer oldOrderId,
                     @Param("newOrderId") Integer newOrderId);

    // 可用票校验（按原乘客 seatTypeId 汇总）
    int countRebookShortage(@Param("oldOrderId") Integer oldOrderId,
                            @Param("newFlightId") String newFlightId);
    Integer lastInsertId();

}
