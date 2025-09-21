package com.sky.mapper;


import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.SetmealDish;
import com.sky.enumeration.OperationType;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper
public interface SetmealDishMapper {




    List<Long> getSetmealDishIds( List<Long> dishIds);

    void insertBatch(List<SetmealDish> setmealDish);


    @Delete("Delete from setmeal_dish where setmeal_id=#{setmealId}")
    void deleteBySetmealId(Long setmealId);

    /**
     * 根据套餐id查询套餐和菜品的关联关系
     * @param setmealId
     * @return
     */
    @Select("select * from setmeal_dish where setmeal_id = #{setmealId}")
    List<SetmealDish> getBySetmealId(Long setmealId);
}
