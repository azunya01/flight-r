package com.sky.service.impl;

import com.sky.dto.BookDTO;
import com.sky.entity.Flight;
import com.sky.entity.Order;
import com.sky.entity.Passenger;
import com.sky.mapper.BookMapper;
import com.sky.service.BookService;
import com.sky.vo.OrderVO;
import com.sky.vo.PassengerVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookMapper bookMapper;

    @Transactional
    @Override
    public OrderVO book(BookDTO bookDTO) {
        // 1) 校验航班
        Flight flight = bookMapper.selectFlightById(bookDTO.getFlightId());
        if (flight == null) {
            throw new RuntimeException("没有您所搜索的航班：" + bookDTO.getFlightId());
        }

        // 2) 创建订单（状态：HOLD）
        Order order = new Order();
        order.setFlightId(flight.getFlightId());
        order.setStatus("HOLD");                 // 与当前 CHECK 约束匹配
        order.setUserId(bookDTO.getUserId());
        bookMapper.insertOrder(order);           // 回填自增 OrderID

        List<PassengerVO> passengerVOList = new ArrayList<>();

        // 3) 为每位乘客选座并落库
        bookDTO.getPassengerList().forEach(paxDTO -> {
            // 3.1 重复预订校验（同手机号、同航班）
            Long exists = bookMapper.checkPassengerExists(paxDTO.getPhone(), bookDTO.getFlightId());
            if (exists != null && exists > 0) {
                throw new RuntimeException("乘客 " + paxDTO.getName() + " 已预订该航班，请勿重复预订");
            }

            // 3.2 余票校验（舱位维度）
            int seatTypeId = paxDTO.getSeatTypeId();
            Integer available = bookMapper.getAvailableSeats(bookDTO.getFlightId(), seatTypeId);
            if (available == null || available <= 0) {
                throw new RuntimeException("乘客 " + paxDTO.getName() + " 预订的座位类型已无余票，请选择其他座位类型");
            }

            // 3.3 获取一个可售座位号
            String seatNo = bookMapper.getSeatNo(bookDTO.getFlightId(), seatTypeId);
            if (seatNo == null) {
                throw new RuntimeException("暂无可售具体座位，请稍后重试");
            }

            // 3.4 原子锁定该座位（AVAILABLE -> BOOKED），写入 orderId / userId
            int booked = bookMapper.bookSeat(
                    bookDTO.getFlightId(), seatTypeId, seatNo, order.getOrderId(), bookDTO.getUserId());
            if (booked != 1) {
                throw new RuntimeException("锁定座位失败，可能被他人抢先，请重试");
            }

            // 3.5 扣减舱位余票（若你已有触发器维护，也可以去掉这一步）
            //bookMapper.decrementAvailableSeats(bookDTO.getFlightID(), seatTypeId);

            // 3.6 落库乘客信息

            Passenger passenger = new Passenger();
            BeanUtils.copyProperties(paxDTO, passenger);
            passenger.setUserId(bookDTO.getUserId());
            passenger.setSeatTypeId(paxDTO.getSeatTypeId());
            passenger.setIdNumber(paxDTO.getIdNumber());
            passenger.setOrderId(order.getOrderId());

            // 如果 Passenger 有 userId 字段可按需设置；没有可忽略
            int aff=bookMapper.insertPassenger(passenger);
            if(aff!=1){
                throw new RuntimeException("乘客信息落库失败");
            }
            var paxPrice = bookMapper.selectPassengerPrice(passenger.getPassengerId());
            passenger.setPrice(paxPrice);
            // 3.7 组装返回

            PassengerVO vo = new PassengerVO();
            BeanUtils.copyProperties(passenger, vo);
            vo.setSeatNo(seatNo);   // VO 里建议加个 seatNo 字段
            passengerVOList.add(vo);
        });

        // 4) 返回订单视图
        OrderVO orderVO = new OrderVO();
        orderVO.setOrderId(order.getOrderId());
        orderVO.setFlightId(flight.getFlightId());
        orderVO.setStatus(order.getStatus());
        orderVO.setPassengerVOList(passengerVOList);
        var total = bookMapper.selectOrderTotal(order.getOrderId());
        orderVO.setTotalPrice(total);
        return orderVO;
    }
}
