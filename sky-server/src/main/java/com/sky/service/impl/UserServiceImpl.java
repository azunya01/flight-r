package com.sky.service.impl;

import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.dto.UserRegisterDTO;
import com.sky.entity.User;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.UserMapper;
import com.sky.service.UserService;
import com.sky.vo.UserRegisterVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    /**
     * 员工登录
     *
     */
    public User login(UserLoginDTO userLoginDTO) {
        String phone = userLoginDTO.getPhone();
        String password = userLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        User user = userMapper.getByPhone(phone);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (user == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        // TODO 后期需要进行md5加密，然后再进行比对
        password=DigestUtils.md5DigestAsHex(password.getBytes());
        if (!password.equals(user.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }
        //3、返回实体对象
        return user;
    }

/*
    @Override
    public void save(userDTO userDTO) {
        User user =new User();

        BeanUtils.copyProperties(userDTO, user);

        user.setStatus(StatusConstant.DISABLE);
        user.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));

//        user.setCreateTime(LocalDateTime.now());
//        user.setUpdateTime(LocalDateTime.now());
//
//        // TODO
//        user.setCreateUser(BaseContext.getCurrentId());
//        user.setUpdateUser(BaseContext.getCurrentId());


        employeeMapper.insert(user);
    }

    @Override
    public void update(userDTO userDTO) {
        //log.info("赋值前：{}",employeeService);
        User user =new User();

        BeanUtils.copyProperties(userDTO, user);
        //log.info("新增员工为：{}",user);
        user.setUpdateTime(LocalDateTime.now());
        user.setUpdateUser(BaseContext.getCurrentId());

        employeeMapper.update(user);
    }
*/
    @Override
    public UserRegisterVO register(UserRegisterDTO userRegisterDTO) {
        User user = userMapper.getByPhone(userRegisterDTO.getPhone());
        if (user != null) {
            throw new AccountNotFoundException("手机号已被注册");
        }
        user = new User();
        BeanUtils.copyProperties(userRegisterDTO, user);
        String password = userRegisterDTO.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        user.setPassword(password);

        Long rows = userMapper.insert(user);
        Long id=user.getId();
        log.info("用户注册成功，影响行数：{}，用户id：{}", rows, id);
        UserRegisterVO userRegisterVO = new UserRegisterVO();
        userRegisterVO.setId(id);
        userRegisterVO.setUserName(user.getUserName());
        return userRegisterVO;
    }

    @Override
    public Boolean exists(String phone) {
        User user = userMapper.getByPhone(phone);
        return user != null;
    }

}
