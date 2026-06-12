package com.example.servicearea.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.servicearea.entity.Merchant;
import com.example.servicearea.entity.MerchantStatus;
import com.example.servicearea.mapper.MerchantMapper;
import com.example.servicearea.security.SecurityUtils;
import com.example.servicearea.service.MerchantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MerchantServiceImpl implements MerchantService {

    private final MerchantMapper merchantMapper;

    @Override
    public IPage<Merchant> getPage(int current, int size, String type, Long serviceAreaId, String keyword,
            Integer status) {
        Page<Merchant> page = new Page<>(current, size);
        LambdaQueryWrapper<Merchant> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(type)) {
            wrapper.eq(Merchant::getType, type);
        }
        if (serviceAreaId != null) {
            wrapper.eq(Merchant::getServiceAreaId, serviceAreaId);
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Merchant::getName, keyword).or().like(Merchant::getContactName, keyword));
        }
        if (status != null) {
            wrapper.eq(Merchant::getStatus, status);
        }
        wrapper.orderByDesc(Merchant::getCreateTime);
        return merchantMapper.selectPage(page, wrapper);
    }

    @Override
    public List<Merchant> getList(String type, Long serviceAreaId) {
        LambdaQueryWrapper<Merchant> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(type)) {
            wrapper.eq(Merchant::getType, type);
        }
        if (serviceAreaId != null) {
            wrapper.eq(Merchant::getServiceAreaId, serviceAreaId);
        }
        if (SecurityUtils.isEndUser()) {
            wrapper.eq(Merchant::getStatus, MerchantStatus.ACTIVE.getCode());
        } else {
            wrapper.ne(Merchant::getStatus, MerchantStatus.CLOSED.getCode());
        }
        wrapper.orderByDesc(Merchant::getCreateTime);
        return merchantMapper.selectList(wrapper);
    }

    @Override
    public Merchant getById(Long id) {
        return merchantMapper.selectById(id);
    }

    @Override
    public boolean save(Merchant merchant) {
        return merchantMapper.insert(merchant) > 0;
    }

    @Override
    public boolean update(Merchant merchant) {
        return merchantMapper.updateById(merchant) > 0;
    }

    @Override
    public boolean delete(Long id) {
        Merchant merchant = merchantMapper.selectById(id);
        if (merchant == null) {
            return false;
        }
        if (merchant.getStatus() != MerchantStatus.CLOSED.getCode()) {
            throw new IllegalArgumentException("只有已关闭状态的商户才能删除");
        }
        return merchantMapper.deleteById(id) > 0;
    }

    @Override
    public boolean updateStatus(Long id, Integer status) {
        Merchant merchant = merchantMapper.selectById(id);
        if (merchant == null) {
            return false;
        }

        MerchantStatus currentStatus = MerchantStatus.fromCode(merchant.getStatus());
        MerchantStatus targetStatus = MerchantStatus.fromCode(status);

        if (!MerchantStatus.canTransition(currentStatus, targetStatus)) {
            throw new IllegalArgumentException(
                    "无法从" + currentStatus.getDescription() + "转换到" + targetStatus.getDescription());
        }

        Merchant updateMerchant = new Merchant();
        updateMerchant.setId(id);
        updateMerchant.setStatus(status);
        return merchantMapper.updateById(updateMerchant) > 0;
    }

    @Override
    public boolean audit(Long id, Integer status, Long auditorId, String remark) {
        Merchant merchant = merchantMapper.selectById(id);
        if (merchant == null) {
            return false;
        }

        MerchantStatus currentStatus = MerchantStatus.fromCode(merchant.getStatus());
        if (currentStatus != MerchantStatus.PENDING) {
            throw new IllegalArgumentException("只有待审核状态的商户才能进行审核");
        }

        MerchantStatus targetStatus = MerchantStatus.fromCode(status);
        if (!MerchantStatus.canTransition(currentStatus, targetStatus)) {
            throw new IllegalArgumentException("审核状态转换无效");
        }

        Merchant updateMerchant = new Merchant();
        updateMerchant.setId(id);
        updateMerchant.setStatus(status);
        updateMerchant.setAuditTime(LocalDateTime.now());
        updateMerchant.setAuditorId(auditorId);
        updateMerchant.setAuditRemark(remark);
        return merchantMapper.updateById(updateMerchant) > 0;
    }

    @Override
    public Map<String, Object> getStatistics() {
        Map<String, Object> result = new HashMap<>();
        List<Merchant> all = merchantMapper.selectList(null);
        long restaurant = all.stream().filter(m -> "RESTAURANT".equals(m.getType())).count();
        long hotel = all.stream().filter(m -> "HOTEL".equals(m.getType())).count();
        long charging = all.stream().filter(m -> "CHARGING".equals(m.getType())).count();
        long gasStation = all.stream().filter(m -> "GAS_STATION".equals(m.getType())).count();

        long pending = all.stream().filter(m -> m.getStatus() == MerchantStatus.PENDING.getCode()).count();
        long active = all.stream().filter(m -> m.getStatus() == MerchantStatus.ACTIVE.getCode()).count();
        long suspended = all.stream().filter(m -> m.getStatus() == MerchantStatus.SUSPENDED.getCode()).count();
        long closed = all.stream().filter(m -> m.getStatus() == MerchantStatus.CLOSED.getCode()).count();

        result.put("RESTAURANT", restaurant);
        result.put("HOTEL", hotel);
        result.put("CHARGING", charging);
        result.put("GAS_STATION", gasStation);
        result.put("total", all.size());
        result.put("pending", pending);
        result.put("active", active);
        result.put("suspended", suspended);
        result.put("closed", closed);
        return result;
    }
}
