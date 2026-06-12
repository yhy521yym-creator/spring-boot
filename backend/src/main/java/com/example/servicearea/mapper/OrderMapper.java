package com.example.servicearea.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.servicearea.entity.Order;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {
}
