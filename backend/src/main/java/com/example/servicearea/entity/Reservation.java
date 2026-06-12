package com.example.servicearea.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@TableName("reservation")
public class Reservation {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long merchantId;

    private Long userId;

    /** 关联订单，预约受理确认时同步订单状态 */
    private Long orderId;

    private String userName;

    private String userPhone;

    private LocalDate reservationDate;

    private LocalTime reservationTime;

    private Integer duration;

    private Integer quantity;

    private String status;

    private String remark;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
