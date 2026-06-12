package com.example.servicearea.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.servicearea.entity.Product;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {
}
