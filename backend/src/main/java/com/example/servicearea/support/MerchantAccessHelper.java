package com.example.servicearea.support;

import com.example.servicearea.entity.Merchant;
import com.example.servicearea.entity.MerchantStatus;
import com.example.servicearea.entity.SysUser;
import com.example.servicearea.mapper.MerchantMapper;
import com.example.servicearea.security.SecurityUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 商户营业状态与登录、下单可见性校验
 */
@Component
@RequiredArgsConstructor
public class MerchantAccessHelper {

    private final MerchantMapper merchantMapper;

    public void assertMerchantUserCanLogin(SysUser user) {
        if (user == null || !"MERCHANT".equals(user.getRole())) {
            return;
        }
        if (user.getMerchantId() == null) {
            throw new IllegalStateException("商户账号未绑定店铺，请联系管理员");
        }
        assertMerchantAllowsLogin(user.getMerchantId());
    }

    public void assertMerchantAllowsLogin(Long merchantId) {
        Merchant merchant = requireMerchant(merchantId);
        MerchantStatus status = MerchantStatus.fromCode(merchant.getStatus());
        switch (status) {
            case ACTIVE -> {
            }
            case PENDING -> throw new IllegalStateException("店铺待审核，暂无法登录");
            case SUSPENDED -> throw new IllegalStateException("店铺已暂停营业，暂无法登录");
            case CLOSED -> throw new IllegalStateException("店铺已关闭，无法登录");
            default -> throw new IllegalStateException("店铺状态异常，无法登录");
        }
    }

    public void assertMerchantAllowsBooking(Long merchantId) {
        Merchant merchant = requireMerchant(merchantId);
        MerchantStatus status = MerchantStatus.fromCode(merchant.getStatus());
        if (status != MerchantStatus.ACTIVE) {
            throw new IllegalStateException(
                    "商户「" + merchant.getName() + "」当前" + status.getDescription() + "，暂不可预约");
        }
    }

    public List<Long> listBookableMerchantIds() {
        return merchantMapper.selectList(
                        new LambdaQueryWrapper<Merchant>().eq(Merchant::getStatus, MerchantStatus.ACTIVE.getCode()))
                .stream()
                .map(Merchant::getId)
                .collect(Collectors.toList());
    }

    public void applyBookableMerchantFilterForEndUser(LambdaQueryWrapper<com.example.servicearea.entity.Product> wrapper) {
        if (!SecurityUtils.isEndUser()) {
            return;
        }
        List<Long> ids = listBookableMerchantIds();
        if (ids.isEmpty()) {
            wrapper.eq(com.example.servicearea.entity.Product::getId, -1L);
        } else {
            wrapper.in(com.example.servicearea.entity.Product::getMerchantId, ids);
        }
    }

    private Merchant requireMerchant(Long merchantId) {
        Merchant merchant = merchantMapper.selectById(merchantId);
        if (merchant == null) {
            throw new IllegalStateException("商户不存在");
        }
        return merchant;
    }
}
