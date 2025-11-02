package com.sky.controller;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.dto.UserRegisterDTO;
import com.sky.entity.User;
import com.sky.properties.JwtProperties;
import com.sky.result.Result;
import com.sky.service.UserService;
import com.sky.utils.JwtUtil;
import com.sky.vo.UserLoginVO;
import com.sky.vo.UserRegisterVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/user")
@Slf4j
@Api(tags="用户登录相关接口")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private JwtProperties jwtProperties;
    //注册
    @PostMapping("/register")
    @ApiOperation(value="用户注册")
    public Result<UserRegisterVO> register(@RequestBody UserRegisterDTO userRegisterDTO) {
        log.info("用户注册：{}", userRegisterDTO);
        UserRegisterVO userRegisterVO= userService.register(userRegisterDTO);
        //注册成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID, userRegisterVO.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getUserSecretKey(),
                jwtProperties.getUserTtl(),
                claims);
        userRegisterVO.setToken(token);
        return Result.success(userRegisterVO);
    }
    //登录
    @PostMapping("/login")
    @ApiOperation(value="用户登录")
    public Result<UserLoginVO> login(@RequestBody UserLoginDTO userLoginDTO) {
        log.info("用户登录：{}", userLoginDTO);
        User user = userService.login(userLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID, user.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getUserSecretKey(),
                jwtProperties.getUserTtl(),
                claims);

        UserLoginVO userLoginVO = UserLoginVO.builder()
                .id(user.getId())
                .userName(user.getUserName())
                .token(token)
                .build();

        return Result.success(userLoginVO);
    }
    //查询用户是否存在
    @GetMapping("/exists")
    @ApiOperation(value="查询用户是否存在")
    public Result<Boolean> exists(@RequestParam String phone) {
        log.info("查询用户是否存在：{}", phone);
        Boolean exists = userService.exists(phone);
        return Result.success(exists);
    }
/*
    /**
     * 退出
     *
     */
    /*
    @ApiOperation(value = "员工退出")
    @PostMapping("/logout")
    public Result<String> logout() {
        return Result.success();
    }

    @PostMapping
    @ApiOperation("新增员工")
    public Result save(@RequestBody userDTO userDTO)
    {
        log.info("新增员工{}", userDTO);
        userService.save(userDTO);
        return Result.success();
    }




    @PutMapping
    @ApiOperation("编辑员工信息")
    public Result update(@RequestBody userDTO userDTO)
    {
        log.info("员工信息为:{}", userDTO);
        userService.update(userDTO);
        return Result.success();
    }
    */
}
