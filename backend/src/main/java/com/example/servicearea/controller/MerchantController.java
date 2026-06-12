package com.example.servicearea.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.servicearea.common.ApiResponse;
import com.example.servicearea.entity.Merchant;
import com.example.servicearea.entity.MerchantStatus;
import com.example.servicearea.entity.SysUser;
import com.example.servicearea.service.MerchantService;
import com.example.servicearea.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import com.example.servicearea.security.UserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/merchants")
@RequiredArgsConstructor
@Validated
public class MerchantController {

    private final MerchantService merchantService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/page")
    public ApiResponse<IPage<Merchant>> getPage(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Long serviceAreaId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        return ApiResponse.ok(merchantService.getPage(current, size, type, serviceAreaId, keyword, status));
    }

    @GetMapping
    public ApiResponse<List<Merchant>> getList(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Long serviceAreaId) {
        return ApiResponse.ok(merchantService.getList(type, serviceAreaId));
    }

    @GetMapping("/{id}")
    public ApiResponse<Merchant> getById(@PathVariable Long id) {
        Merchant merchant = merchantService.getById(id);
        if (merchant == null) {
            return ApiResponse.fail(404, "商户不存在");
        }
        return ApiResponse.ok(merchant);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('merchant:add') or hasAuthority('*')")
    public ApiResponse<Boolean> save(@RequestBody @Validated MerchantRequest request) {
        Merchant merchant = new Merchant();
        merchant.setServiceAreaId(request.getServiceAreaId());
        merchant.setName(request.getName());
        merchant.setType(request.getType());
        merchant.setDescription(request.getDescription());
        merchant.setImageUrl(request.getImageUrl());
        merchant.setAddress(request.getAddress());
        merchant.setContactName(request.getContactName());
        merchant.setContactPhone(request.getContactPhone());
        merchant.setBusinessHours(request.getBusinessHours());
        merchant.setCapacity(request.getCapacity());
        merchant.setStatus(MerchantStatus.PENDING.getCode());
        return ApiResponse.ok(merchantService.save(merchant));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('merchant:edit') or hasAuthority('*')")
    public ApiResponse<Boolean> update(@PathVariable Long id, @RequestBody @Validated MerchantRequest request) {
        Merchant merchant = new Merchant();
        merchant.setId(id);
        merchant.setServiceAreaId(request.getServiceAreaId());
        merchant.setName(request.getName());
        merchant.setType(request.getType());
        merchant.setDescription(request.getDescription());
        merchant.setImageUrl(request.getImageUrl());
        merchant.setAddress(request.getAddress());
        merchant.setContactName(request.getContactName());
        merchant.setContactPhone(request.getContactPhone());
        merchant.setBusinessHours(request.getBusinessHours());
        merchant.setCapacity(request.getCapacity());
        return ApiResponse.ok(merchantService.update(merchant));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('merchant:delete') or hasAuthority('*')")
    public ApiResponse<Boolean> delete(@PathVariable Long id) {
        return ApiResponse.ok(merchantService.delete(id));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('merchant:edit') or hasAuthority('*')")
    public ApiResponse<Boolean> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        try {
            return ApiResponse.ok(merchantService.updateStatus(id, status));
        } catch (IllegalArgumentException e) {
            return ApiResponse.fail(400, e.getMessage());
        }
    }

    @PutMapping("/{id}/audit")
    @PreAuthorize("hasAuthority('merchant:audit') or hasAuthority('*')")
    public ApiResponse<Boolean> audit(@PathVariable Long id, @RequestBody AuditRequest request) {
        try {
            Long auditorId = getCurrentUserId();
            return ApiResponse.ok(merchantService.audit(id, request.getStatus(), auditorId, request.getRemark()));
        } catch (IllegalArgumentException e) {
            return ApiResponse.fail(400, e.getMessage());
        }
    }

    @GetMapping("/statistics")
    public ApiResponse<Map<String, Object>> getStatistics() {
        return ApiResponse.ok(merchantService.getStatistics());
    }

    /**
     * 查询店铺已绑定的商户登录账号
     */
    @GetMapping("/{id:\\d+}/accounts")
    @PreAuthorize("hasAuthority('merchant:view') or hasAuthority('*')")
    public ApiResponse<List<SysUser>> listAccounts(@PathVariable Long id) {
        if (merchantService.getById(id) == null) {
            return ApiResponse.fail(404, "商户不存在");
        }
        List<SysUser> users = userService.findMerchantUsersByMerchantId(id);
        users.forEach(u -> u.setPassword(null));
        return ApiResponse.ok(users);
    }

    /**
     * 为店铺创建商户登录账号并绑定 merchant_id
     */
    @PostMapping("/{id:\\d+}/account")
    @PreAuthorize("hasAuthority('merchant:account') or hasAuthority('*')")
    public ApiResponse<SysUser> createAccount(@PathVariable Long id,
                                            @Validated @RequestBody MerchantAccountRequest request) {
        if (merchantService.getById(id) == null) {
            return ApiResponse.fail(404, "商户不存在");
        }
        if (userService.existsByUsername(request.getUsername())) {
            return ApiResponse.fail(400, "用户名已存在");
        }

        SysUser user = new SysUser();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setRole("MERCHANT");
        user.setMerchantId(id);
        user.setStatus(request.getStatus() != null ? request.getStatus() : 1);

        SysUser created = userService.create(user);
        created.setPassword(null);
        return ApiResponse.ok(created);
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal) {
            return ((UserPrincipal) authentication.getPrincipal()).getId();
        }
        return null;
    }

    @Data
    public static class MerchantRequest {
        private Long serviceAreaId;
        private String name;
        private String type;
        private String description;
        private String imageUrl;
        private String address;
        private String contactName;
        private String contactPhone;
        private String businessHours;
        private Integer capacity;
    }

    @Data
    public static class AuditRequest {
        private Integer status;
        private String remark;
    }

    @Data
    public static class MerchantAccountRequest {
        @NotBlank(message = "用户名不能为空")
        private String username;

        @NotBlank(message = "密码不能为空")
        private String password;

        @Email(message = "邮箱格式不正确")
        private String email;

        private String phone;

        private Integer status;
    }
}
