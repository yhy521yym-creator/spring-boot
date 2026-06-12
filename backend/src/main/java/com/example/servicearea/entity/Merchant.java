package com.example.servicearea.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("merchant")
public class Merchant {

    @TableId(type = IdType.AUTO)
    private Long id;

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

    private Integer status;

    @TableField("audit_time")
    private LocalDateTime auditTime;

    @TableField("auditor_id")
    private Long auditorId;

    @TableField("audit_remark")
    private String auditRemark;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
