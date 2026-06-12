package com.example.servicearea.controller;

import com.example.servicearea.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {

    private static final Set<String> ALLOWED_TYPES = Set.of("avatar", "merchant", "product", "image");

    @Value("${app.upload.base-path:./uploads}")
    private String uploadBasePath;

    @PostMapping("/upload")
    public ApiResponse<Map<String, String>> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "type", defaultValue = "image") String type) throws IOException {
        if (file == null || file.isEmpty()) {
            return ApiResponse.fail(400, "请选择文件");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return ApiResponse.fail(400, "仅支持图片文件");
        }
        if (file.getSize() > 2 * 1024 * 1024) {
            return ApiResponse.fail(400, "图片大小不能超过 2MB");
        }

        String folder = normalizeType(type);
        String ext = resolveExtension(file.getOriginalFilename(), contentType);
        String filename = UUID.randomUUID().toString().replace("-", "") + ext;

        Path dir = Paths.get(uploadBasePath, folder).toAbsolutePath().normalize();
        Files.createDirectories(dir);
        Path target = dir.resolve(filename);
        file.transferTo(target.toFile());

        String url = "/api/uploads/" + folder + "/" + filename;
        return ApiResponse.ok(Map.of("url", url));
    }

    /**
     * 删除已上传的图片（仅允许删除 uploads 目录下的文件）
     */
    @DeleteMapping
    public ApiResponse<Void> delete(@RequestParam("url") String url) {
        if (!StringUtils.hasText(url)) {
            return ApiResponse.fail(400, "缺少图片地址");
        }
        Path file = resolveUploadPath(url);
        if (file == null) {
            return ApiResponse.fail(400, "非法的图片路径");
        }
        try {
            if (Files.exists(file)) {
                Files.delete(file);
            }
            return ApiResponse.ok();
        } catch (IOException e) {
            return ApiResponse.fail(500, "删除文件失败");
        }
    }

  private Path resolveUploadPath(String url) {
        String path = url.trim().replace('\\', '/');
        String prefix = "/api/uploads/";
        if (path.startsWith(prefix)) {
            path = path.substring(prefix.length());
        } else if (path.startsWith("/uploads/")) {
            path = path.substring("/uploads/".length());
        } else if (path.startsWith("uploads/")) {
            path = path.substring("uploads/".length());
        } else {
            return null;
        }
        if (path.contains("..")) {
            return null;
        }
        Path base = Paths.get(uploadBasePath).toAbsolutePath().normalize();
        Path resolved = base.resolve(path).normalize();
        if (!resolved.startsWith(base)) {
            return null;
        }
        return resolved;
    }

    private String normalizeType(String type) {
        if (!StringUtils.hasText(type) || !ALLOWED_TYPES.contains(type)) {
            return "images";
        }
        if ("image".equals(type)) {
            return "images";
        }
        return type;
    }

    private String resolveExtension(String originalName, String contentType) {
        if (StringUtils.hasText(originalName) && originalName.contains(".")) {
            return originalName.substring(originalName.lastIndexOf('.'));
        }
        if ("image/png".equals(contentType)) {
            return ".png";
        }
        if ("image/gif".equals(contentType)) {
            return ".gif";
        }
        if ("image/webp".equals(contentType)) {
            return ".webp";
        }
        return ".jpg";
    }
}
