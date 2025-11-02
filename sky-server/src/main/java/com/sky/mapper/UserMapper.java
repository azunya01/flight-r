package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.User;
import com.sky.enumeration.OperationType;
import com.sky.vo.UserRegisterVO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {



    @AutoFill(value = OperationType.INSERT)
    Long insert(User user);

    User getByPhone(String phone);

    //@AutoFill(value = OperationType.UPDATE)
    //void update(User user);

}
