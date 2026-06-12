package com.example.servicearea.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.servicearea.entity.ServiceArea;

import java.math.BigDecimal;
import java.util.List;

/**
 * 服务区信息服务接口
 */
public interface ServiceAreaService extends IService<ServiceArea> {

    /**
     * 分页查询服务区（支持按名称、地址模糊搜索）
     *
     * @param page    分页参数
     * @param name    服务区名称（可选）
     * @param address 地址（可选）
     * @return 分页结果
     */
    IPage<ServiceArea> queryPage(Page<ServiceArea> page, String name, String address);

    /**
     * 查询附近的服务区
     *
     * @param lat    纬度
     * @param lng    经度
     * @param radius 半径（米）
     * @return 服务区列表（按距离排序）
     */
    List<ServiceArea> findNearby(BigDecimal lat, BigDecimal lng, Integer radius);

    /**
     * 新增服务区
     *
     * @param serviceArea 服务区信息
     * @return 是否成功
     */
    boolean addServiceArea(ServiceArea serviceArea);

    /**
     * 修改服务区
     *
     * @param serviceArea 服务区信息
     * @return 是否成功
     */
    boolean updateServiceArea(ServiceArea serviceArea);

    /**
     * 删除服务区
     *
     * @param id 服务区ID
     * @return 是否成功
     */
    boolean deleteServiceArea(Long id);
}
