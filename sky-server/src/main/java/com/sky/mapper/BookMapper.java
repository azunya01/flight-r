package com.sky.mapper;

import com.sky.entity.Flight;
import com.sky.entity.Order;
import com.sky.entity.Passenger;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

@Mapper
public interface BookMapper {

    Flight selectFlightById(@Param("flightId") String flightId);

    int insertOrder(Order order);   // useGeneratedKeys 回填

    int insertPassenger(Passenger passenger);

    Long checkPassengerExists(@Param("phone") String phone,
                              @Param("flightId") String flightId);

    Integer getAvailableSeats(@Param("flightId") String flightId,
                              @Param("seatTypeId") Integer seatTypeId);

    String getSeatNo(@Param("flightId") String flightId,
                     @Param("seatTypeId") Integer seatTypeId);

    int bookSeat(@Param("flightId") String flightId,
                 @Param("seatTypeId") Integer seatTypeId,
                 @Param("seatNo") String seatNo,
                 @Param("orderId") Integer orderId,
                 @Param("userId") Integer userId);

    int decrementAvailableSeats(@Param("flightId") String flightId,
                                @Param("seatTypeId") Integer seatTypeId);


    BigDecimal selectPassengerPrice(@Param("passengerId") Integer passengerId);
    BigDecimal selectOrderTotal(@Param("orderId") Integer orderId);
    int recalcOrderTotal(@Param("orderId") Integer orderId); // 兜底：服务层手动汇总
}
