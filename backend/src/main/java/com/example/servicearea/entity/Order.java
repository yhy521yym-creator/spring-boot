package com.example.servicearea.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@TableName("`order`")
public class Order {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String orderNo;

    private Long userId;

    private Long merchantId;

    private Long productId;

    private Integer quantity;

    private BigDecimal totalPrice;

    private String status;

    /** UNPAID / PAID */
    @TableField("pay_status")
    @JsonProperty("payStatus")
    private String payStatus;

    @TableField("pay_time")
    @JsonProperty("payTime")
    private LocalDateTime payTime;

    /** WECHAT / ALIPAY / SIMULATE */
    @TableField("pay_method")
    @JsonProperty("payMethod")
    private String payMethod;

    private LocalDate reservationDate;

    private LocalTime reservationTime;

    private String contactName;

    private String contactPhone;

    private String remark;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
