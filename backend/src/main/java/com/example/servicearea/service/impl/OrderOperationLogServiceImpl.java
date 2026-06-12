package com.example.servicearea.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.servicearea.entity.Order;
import com.example.servicearea.entity.OrderOperationLog;
import com.example.servicearea.entity.SysUser;
import com.example.servicearea.mapper.OrderMapper;
import com.example.servicearea.mapper.OrderOperationLogMapper;
import com.example.servicearea.security.DataScopeHelper;
import com.example.servicearea.security.SecurityUtils;
import com.example.servicearea.security.UserPrincipal;
import com.example.servicearea.service.OrderOperationLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderOperationLogServiceImpl implements OrderOperationLogService {

    private final OrderOperationLogMapper logMapper;
    private final OrderMapper orderMapper;
    private final DataScopeHelper dataScopeHelper;

    @Override
    public void recordStatusChange(Order order, String oldStatus, String newStatus, String remark) {
        UserPrincipal principal = SecurityUtils.getPrincipal();
        if (principal == null || order == null) {
            return;
        }
        OrderOperationLog log = new OrderOperationLog();
        log.setOrderId(order.getId());
        log.setOrderNo(order.getOrderNo());
        log.setUserId(order.getUserId());
        log.setMerchantId(order.getMerchantId());
        log.setOperatorId(principal.getId());
        log.setOperatorUsername(principal.getUsername());
        log.setOperatorRole(principal.getRole());
        log.setOldStatus(oldStatus);
        log.setNewStatus(newStatus);
        log.setRemark(remark);
        log.setCreateTime(LocalDateTime.now());
        logMapper.insert(log);
    }

    @Override
    public List<OrderOperationLog> listByOrderId(Long orderId) {
        assertCanAccessOrder(orderId);
        LambdaQueryWrapper<OrderOperationLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderOperationLog::getOrderId, orderId);
        wrapper.orderByDesc(OrderOperationLog::getCreateTime);
        return logMapper.selectList(wrapper);
    }

    @Override
    public IPage<OrderOperationLog> getPage(int current, int size, Long orderId, String orderNo) {
        Page<OrderOperationLog> page = new Page<>(current, size);
        LambdaQueryWrapper<OrderOperationLog> wrapper = new LambdaQueryWrapper<>();
        if (orderId != null) {
            assertCanAccessOrder(orderId);
            wrapper.eq(OrderOperationLog::getOrderId, orderId);
        }
        if (StringUtils.hasText(orderNo)) {
            wrapper.like(OrderOperationLog::getOrderNo, orderNo);
        }
        applyScope(wrapper);
        wrapper.orderByDesc(OrderOperationLog::getCreateTime);
        return logMapper.selectPage(page, wrapper);
    }

    private void applyScope(LambdaQueryWrapper<OrderOperationLog> wrapper) {
        if (SecurityUtils.isPlatformAdmin()) {
            return;
        }
        SysUser current = dataScopeHelper.requireCurrentUser();
        if (SecurityUtils.isEndUser()) {
            wrapper.eq(OrderOperationLog::getUserId, current.getId());
            return;
        }
        if (SecurityUtils.isMerchant() && current.getMerchantId() != null) {
            wrapper.eq(OrderOperationLog::getMerchantId, current.getMerchantId());
        }
    }

    private void assertCanAccessOrder(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            SecurityUtils.deny("订单不存在");
        }
        if (SecurityUtils.isPlatformAdmin()) {
            return;
        }
        SysUser current = dataScopeHelper.requireCurrentUser();
        if (SecurityUtils.isEndUser() && current.getId().equals(order.getUserId())) {
            return;
        }
        if (SecurityUtils.isMerchant()
                && current.getMerchantId() != null
                && current.getMerchantId().equals(order.getMerchantId())) {
            return;
        }
        SecurityUtils.deny("无权查看该订单日志");
    }
}
