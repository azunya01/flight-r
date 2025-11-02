package com.sky.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
@Data
public class UserRegisterVO implements Serializable {
    @ApiModelProperty("主键值")
    private Long id;

    @ApiModelProperty("用户名")
    private String userName;


    @ApiModelProperty("jwt令牌")
    private String token;
}
