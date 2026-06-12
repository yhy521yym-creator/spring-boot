package com.example.servicearea.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.servicearea.entity.Product;
import com.example.servicearea.entity.Merchant;
import com.example.servicearea.entity.SysUser;
import com.example.servicearea.mapper.ProductMapper;
import com.example.servicearea.mapper.MerchantMapper;
import com.example.servicearea.security.DataScopeHelper;
import com.example.servicearea.security.SecurityUtils;
import com.example.servicearea.support.MerchantAccessHelper;
import com.example.servicearea.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;
    private final MerchantMapper merchantMapper;
    private final DataScopeHelper dataScopeHelper;
    private final MerchantAccessHelper merchantAccessHelper;

    @Override
    public IPage<Product> getPage(int current, int size, String type, Long merchantId, Integer status) {
        Page<Product> page = new Page<>(current, size);
        LambdaQueryWrapper<Product> wrapper = buildQueryWrapper(type, merchantId, status, null);
        wrapper.orderByDesc(Product::getCreateTime);
        return productMapper.selectPage(page, wrapper);
    }

    @Override
    public List<Product> getList(String type, Long merchantId, Integer status, Long serviceAreaId) {
        LambdaQueryWrapper<Product> wrapper = buildQueryWrapper(type, merchantId, status, serviceAreaId);
        wrapper.orderByDesc(Product::getCreateTime);
        return productMapper.selectList(wrapper);
    }

    private LambdaQueryWrapper<Product> buildQueryWrapper(String type, Long merchantId, Integer status, Long serviceAreaId) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        Long scopedMerchantId = dataScopeHelper.scopedMerchantId(merchantId);
        if (StringUtils.hasText(type)) {
            wrapper.eq(Product::getType, type);
        }
        if (scopedMerchantId != null) {
            wrapper.eq(Product::getMerchantId, scopedMerchantId);
        }
        if (status != null) {
            wrapper.eq(Product::getStatus, status);
        }
        if (serviceAreaId != null && SecurityUtils.isPlatformAdmin()) {
            List<Long> merchantIds = getMerchantIdsByServiceAreaId(serviceAreaId);
            if (!merchantIds.isEmpty()) {
                wrapper.in(Product::getMerchantId, merchantIds);
            } else {
                wrapper.eq(Product::getId, -1L);
            }
        }
        merchantAccessHelper.applyBookableMerchantFilterForEndUser(wrapper);
        return wrapper;
    }

    private List<Long> getMerchantIdsByServiceAreaId(Long serviceAreaId) {
        LambdaQueryWrapper<Merchant> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Merchant::getServiceAreaId, serviceAreaId);
        wrapper.eq(Merchant::getStatus, 1);
        List<Merchant> merchants = merchantMapper.selectList(wrapper);
        List<Long> ids = new ArrayList<>();
        for (Merchant m : merchants) {
            ids.add(m.getId());
        }
        return ids;
    }

    @Override
    public Product getById(Long id) {
        return productMapper.selectById(id);
    }

    @Override
    public boolean save(Product product) {
        if (SecurityUtils.isMerchant()) {
            SysUser current = dataScopeHelper.requireCurrentUser();
            product.setMerchantId(current.getMerchantId());
        }
        return productMapper.insert(product) > 0;
    }

    @Override
    public boolean update(Product product) {
        assertProductAccess(product.getId());
        if (SecurityUtils.isMerchant()) {
            SysUser current = dataScopeHelper.requireCurrentUser();
            product.setMerchantId(current.getMerchantId());
        }
        return productMapper.updateById(product) > 0;
    }

    @Override
    public boolean delete(Long id) {
        assertProductAccess(id);
        return productMapper.deleteById(id) > 0;
    }

    @Override
    public boolean updateStatus(Long id, Integer status) {
        assertProductAccess(id);
        Product product = new Product();
        product.setId(id);
        product.setStatus(status);
        return productMapper.updateById(product) > 0;
    }

    private void assertProductAccess(Long productId) {
        if (productId == null || SecurityUtils.isPlatformAdmin()) {
            return;
        }
        Product existing = productMapper.selectById(productId);
        if (existing == null) {
            return;
        }
        if (SecurityUtils.isMerchant()) {
            SysUser current = dataScopeHelper.requireCurrentUser();
            if (current.getMerchantId() != null && current.getMerchantId().equals(existing.getMerchantId())) {
                return;
            }
            SecurityUtils.deny("无权操作该商品");
        }
    }
}
