package com.example.servicearea.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 解析服务区 facilities 字段（支持 JSON 数组或逗号分隔，与前端 ServiceAreaManageView 一致）
 */
public final class FacilitiesParser {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private FacilitiesParser() {
    }

    public static List<String> parse(String facilitiesStr) {
        if (facilitiesStr == null || facilitiesStr.isBlank()) {
            return Collections.emptyList();
        }
        String trimmed = facilitiesStr.trim();
        if (trimmed.startsWith("[")) {
            try {
                List<String> list = MAPPER.readValue(trimmed, new TypeReference<List<String>>() {});
                if (list != null) {
                    return list.stream()
                            .filter(s -> s != null && !s.isBlank())
                            .map(String::trim)
                            .toList();
                }
            } catch (Exception ignored) {
                // 回退为逗号分隔
            }
        }
        List<String> result = new ArrayList<>();
        for (String part : trimmed.split(",")) {
            String item = part.trim();
            if (!item.isEmpty()) {
                result.add(item);
            }
        }
        return result;
    }
}
