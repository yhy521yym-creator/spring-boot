package com.example.servicearea.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("order_operation_log")
public class OrderOperationLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long orderId;

    private String orderNo;

    private Long userId;

    private Long merchantId;

    private Long operatorId;

    private String operatorUsername;

    private String operatorRole;

    private String oldStatus;

    private String newStatus;

    private String remark;

    private LocalDateTime createTime;
}
