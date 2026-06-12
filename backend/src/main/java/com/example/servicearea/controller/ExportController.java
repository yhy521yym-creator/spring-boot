package com.example.servicearea.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.example.servicearea.entity.ServiceArea;
import com.example.servicearea.service.ServiceAreaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 导出控制器
 */
@Slf4j
@RestController
@RequestMapping("/export")
@RequiredArgsConstructor
public class ExportController {

    private final ServiceAreaService serviceAreaService;

    /**
     * 导出服务区列表为Excel
     */
    @GetMapping("/service-areas")
    public void exportServiceAreas(
            HttpServletResponse response,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String address) throws IOException {
        // 设置响应头
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        String fileName = URLEncoder.encode("服务区列表", StandardCharsets.UTF_8.name()).replaceAll("\\+", "%");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

        // 查询数据
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<ServiceArea> page = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(
                1, 1000);
        com.baomidou.mybatisplus.core.metadata.IPage<ServiceArea> result = serviceAreaService.queryPage(page, name,
                address);
        List<ServiceArea> serviceAreas = result.getRecords();

        // 构建样式策略
        HorizontalCellStyleStrategy styleStrategy = buildStyleStrategy();

        // 导出Excel
        EasyExcel.write(response.getOutputStream(), ServiceArea.class)
                .sheet("服务区列表")
                .registerWriteHandler(styleStrategy)
                .doWrite(serviceAreas);
    }

    /**
     * 构建Excel样式策略
     */
    private HorizontalCellStyleStrategy buildStyleStrategy() {
        // 头样式
        WriteCellStyle headWriteCellStyle = new WriteCellStyle();
        headWriteCellStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        WriteFont headWriteFont = new WriteFont();
        headWriteFont.setFontHeightInPoints((short) 12);
        headWriteFont.setBold(true);
        headWriteCellStyle.setWriteFont(headWriteFont);
        headWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);

        // 内容样式
        WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
        contentWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.LEFT);
        contentWriteCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        return new HorizontalCellStyleStrategy(headWriteCellStyle, contentWriteCellStyle);
    }
}
