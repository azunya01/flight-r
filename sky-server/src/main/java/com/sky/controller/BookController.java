package com.sky.controller;

import com.sky.dto.BookDTO;
import com.sky.result.Result;
import com.sky.service.BookService;
import com.sky.utils.UserContext;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@Slf4j
@Api(tags="预定机票相关接口")
public class BookController {
    @Autowired
    private BookService bookService;
    @PostMapping("/book")
    @ApiOperation("预定机票接口")
    public Result<OrderVO> book(@RequestBody BookDTO bookDTO) {
        log.info("-----------------------------------------------------");
        Integer id= UserContext.getUserId();
        bookDTO.setUserId(id);
        log.info("预定机票：{}", bookDTO);
        OrderVO orderVO=bookService.book(bookDTO);
        log.info("预定成功，订单信息：{}", orderVO);
        return Result.success(orderVO);
    }


}
