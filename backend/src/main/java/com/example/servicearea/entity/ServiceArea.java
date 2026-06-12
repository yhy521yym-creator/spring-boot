package com.example.servicearea.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 服务区信息表实体：service_area
 */
@Data
@TableName("service_area")
public class ServiceArea {

    @TableId(type = IdType.AUTO)
    @ExcelProperty("ID")
    private Long id;

    /**
     * 服务区名称
     */
    @ExcelProperty("服务区名称")
    private String name;

    /**
     * 详细地址
     */
    @ExcelProperty("详细地址")
    private String address;

    /**
     * 区域/城市
     */
    @ExcelProperty("区域")
    private String region;

    /**
     * 经度
     */
    @ExcelProperty("经度")
    private BigDecimal longitude;

    /**
     * 纬度
     */
    @ExcelProperty("纬度")
    private BigDecimal latitude;

    /**
     * 设施列表，JSON格式
     */
    @ExcelProperty("设施")
    private String facilities;

    /**
     * 服务区描述
     */
    @ExcelProperty("描述")
    private String description;

    /**
     * 状态：0-停用，1-正常
     */
    @ExcelProperty("状态")
    private Integer status;

    /**
     * 创建时间
     */
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @ExcelProperty("更新时间")
    private LocalDateTime updateTime;

    /**
     * 距离（附近查询时使用，非数据库字段）
     */
    @TableField(exist = false)
    private Double distance;
}
