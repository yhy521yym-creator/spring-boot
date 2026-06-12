package com.example.servicearea.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.servicearea.entity.SiteConfig;
import com.example.servicearea.mapper.SiteConfigMapper;
import com.example.servicearea.service.SiteConfigService;
import org.springframework.stereotype.Service;

/**
 * 站点配置服务实现类
 */
@Service
public class SiteConfigServiceImpl implements SiteConfigService {

    private final SiteConfigMapper siteConfigMapper;

    public SiteConfigServiceImpl(SiteConfigMapper siteConfigMapper) {
        this.siteConfigMapper = siteConfigMapper;
    }

    @Override
    public IPage<SiteConfig> queryPage(Page<SiteConfig> page, String configKey) {
        LambdaQueryWrapper<SiteConfig> wrapper = new LambdaQueryWrapper<>();
        if (configKey != null && !configKey.isBlank()) {
            wrapper.like(SiteConfig::getConfigKey, configKey);
        }
        return siteConfigMapper.selectPage(page, wrapper);
    }

    @Override
    public String getValueByKey(String key) {
        SiteConfig config = siteConfigMapper
                .selectOne(new LambdaQueryWrapper<SiteConfig>().eq(SiteConfig::getConfigKey, key));
        return config != null ? config.getConfigValue() : null;
    }

    @Override
    public boolean saveOrUpdate(SiteConfig config) {
        // 检查是否已存在该键的配置
        SiteConfig existingConfig = siteConfigMapper
                .selectOne(new LambdaQueryWrapper<SiteConfig>().eq(SiteConfig::getConfigKey, config.getConfigKey()));
        if (existingConfig != null) {
            // 更新现有配置
            existingConfig.setConfigValue(config.getConfigValue());
            existingConfig.setDescription(config.getDescription());
            existingConfig.setStatus(config.getStatus());
            return siteConfigMapper.updateById(existingConfig) > 0;
        } else {
            // 新增配置
            return siteConfigMapper.insert(config) > 0;
        }
    }

    @Override
    public boolean delete(Long id) {
        return siteConfigMapper.deleteById(id) > 0;
    }

    @Override
    public String getMapKey() {
        return getValueByKey("map.api.key");
    }
}
