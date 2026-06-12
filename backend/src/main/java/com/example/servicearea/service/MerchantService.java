package com.example.servicearea.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.servicearea.entity.Merchant;
import java.util.List;
import java.util.Map;

public interface MerchantService {
    IPage<Merchant> getPage(int current, int size, String type, Long serviceAreaId, String keyword, Integer status);
    List<Merchant> getList(String type, Long serviceAreaId);
    Merchant getById(Long id);
    boolean save(Merchant merchant);
    boolean update(Merchant merchant);
    boolean delete(Long id);
    boolean updateStatus(Long id, Integer status);
    boolean audit(Long id, Integer status, Long auditorId, String remark);
    Map<String, Object> getStatistics();
}
