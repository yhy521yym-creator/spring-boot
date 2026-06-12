package com.example.servicearea.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.servicearea.common.ApiResponse;
import com.example.servicearea.entity.Product;
import com.example.servicearea.service.ProductService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Validated
public class ProductController {

    private final ProductService productService;

    @GetMapping("/page")
    public ApiResponse<IPage<Product>> getPage(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Long merchantId,
            @RequestParam(required = false) Integer status) {
        return ApiResponse.ok(productService.getPage(current, size, type, merchantId, status));
    }

    @GetMapping
    public ApiResponse<IPage<Product>> getList(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Long merchantId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.ok(productService.getPage(current, size, type, merchantId, status));
    }

    @GetMapping("/{id}")
    public ApiResponse<Product> getById(@PathVariable Long id) {
        Product product = productService.getById(id);
        if (product == null) {
            return ApiResponse.fail(404, "商品不存在");
        }
        return ApiResponse.ok(product);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'OPERATOR', 'MERCHANT')")
    public ApiResponse<Boolean> save(@RequestBody @Validated ProductRequest request) {
        Product product = new Product();
        product.setMerchantId(request.getMerchantId());
        product.setName(request.getName());
        product.setType(request.getType());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setImageUrl(request.getImageUrl());
        product.setStock(request.getStock() != null ? request.getStock() : 0);
        product.setStatus(1);
        return ApiResponse.ok(productService.save(product));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'OPERATOR', 'MERCHANT')")
    public ApiResponse<Boolean> update(@PathVariable Long id, @RequestBody @Validated ProductRequest request) {
        Product product = new Product();
        product.setId(id);
        product.setMerchantId(request.getMerchantId());
        product.setName(request.getName());
        product.setType(request.getType());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setImageUrl(request.getImageUrl());
        product.setStock(request.getStock());
        return ApiResponse.ok(productService.update(product));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'OPERATOR', 'MERCHANT')")
    public ApiResponse<Boolean> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        return ApiResponse.ok(productService.updateStatus(id, status));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'OPERATOR', 'MERCHANT')")
    public ApiResponse<Boolean> delete(@PathVariable Long id) {
        return ApiResponse.ok(productService.delete(id));
    }

    @Data
    public static class ProductRequest {
        private Long merchantId;
        private String name;
        private String type;
        private String description;
        private BigDecimal price;
        private String imageUrl;
        private Integer stock;
    }
}
