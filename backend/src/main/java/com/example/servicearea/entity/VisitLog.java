package com.example.servicearea.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("visit_log")
public class VisitLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long serviceAreaId;

    private Long userId;

    private String ipAddress;

    private LocalDateTime visitTime;

    private LocalDateTime createTime;
}
