package com.sky.service;

import com.sky.dto.BookDTO;
import com.sky.vo.OrderVO;

public interface BookService {

    OrderVO book(BookDTO bookDTO);
}
