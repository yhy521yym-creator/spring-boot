package com.example.servicearea.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.servicearea.entity.SiteConfig;

/**
 * 站点配置服务接口
 */
public interface SiteConfigService {

    /**
     * 分页查询配置
     *
     * @param page      分页参数
     * @param configKey 配置键名（模糊搜索）
     * @return 分页结果
     */
    IPage<SiteConfig> queryPage(Page<SiteConfig> page, String configKey);

    /**
     * 根据键获取配置值
     *
     * @param key 配置键名
     * @return 配置值
     */
    String getValueByKey(String key);

    /**
     * 新增或更新配置
     *
     * @param config 配置信息
     * @return 是否成功
     */
    boolean saveOrUpdate(SiteConfig config);

    /**
     * 删除配置
     *
     * @param id 配置ID
     * @return 是否成功
     */
    boolean delete(Long id);

    /**
     * 获取地图API Key
     *
     * @return 地图API Key
     */
    String getMapKey();
}
