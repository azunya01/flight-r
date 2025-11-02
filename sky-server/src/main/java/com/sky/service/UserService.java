package com.sky.service;

import com.sky.dto.UserRegisterDTO;
import com.sky.dto.userDTO;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.vo.UserRegisterVO;

public interface UserService {

    /**
     * 员工登录
     */
   User login(UserLoginDTO userLoginDTO);
//
//    void save(userDTO userDTO);
//
//    void update(userDTO userDTO);

    UserRegisterVO register(UserRegisterDTO userRegisterDTO);

    Boolean exists(String phone);
}
