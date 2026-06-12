package com.example.servicearea.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统用户表实体：sys_user
 */
@Data
@TableName("sys_user")
public class SysUser {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;

    /**
     * BCrypt 加密后的密码
     */
    private String password;

    private String phone;

    private String email;

    private String avatar;

    /**
     * 状态：0禁用 1启用
     */
    private Integer status;

    /**
     * 角色：SUPER_ADMIN/OPERATOR/MERCHANT
     */
    private String role;

    /**
     * 商户ID（商家用户关联的商户）
     */
    private Long merchantId;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
