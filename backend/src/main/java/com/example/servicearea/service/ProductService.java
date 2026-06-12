package com.example.servicearea.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.servicearea.entity.Product;
import java.util.List;

public interface ProductService {
    IPage<Product> getPage(int current, int size, String type, Long merchantId, Integer status);
    List<Product> getList(String type, Long merchantId, Integer status, Long serviceAreaId);
    Product getById(Long id);
    boolean save(Product product);
    boolean update(Product product);
    boolean delete(Long id);
    boolean updateStatus(Long id, Integer status);
}
