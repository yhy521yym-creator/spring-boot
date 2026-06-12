package com.example.servicearea.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.servicearea.entity.ServiceArea;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;

/**
 * 服务区信息表 Mapper
 */
@Mapper
public interface ServiceAreaMapper extends BaseMapper<ServiceArea> {

    /**
     * 分页查询服务区（支持按名称、地址模糊搜索）
     */
    IPage<ServiceArea> selectPageWithSearch(Page<ServiceArea> page,
            @Param("name") String name,
            @Param("address") String address);

    /**
     * 查询附近的服务区
     * 使用球面距离公式计算（H2数据库兼容）
     *
     * @param lat    纬度
     * @param lng    经度
     * @param radius 半径（米）
     * @return 服务区列表（包含距离字段）
     */
    List<ServiceArea> selectNearby(@Param("lat") BigDecimal lat,
            @Param("lng") BigDecimal lng,
            @Param("radius") Integer radius);
}
