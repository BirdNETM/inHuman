package org.inhuman.smartplatform.utils;

import lombok.extern.slf4j.Slf4j;
import org.inhuman.smartplatform.pojo.User;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class DocsDownloadUtils {

    public static ResponseEntity<Resource> downloadDocsByUrl(String url) {
        try {
            // 确保URL合法
            Path path = Paths.get(url);
            log.info(String.valueOf(path));

            // 将 Path 转换为 URI
            Resource resource = new UrlResource(path.toUri());
            // 检查文件是否存在
            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }

            // 假设 fileName 包含非 ASCII 字符
            String fileName = resource.getFilename() != null ? resource.getFilename() : "default_filename";
            // 对文件名进行 URL 编码，确保客户端可以正确识别非 ASCII 字符
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);

            // 设置 Content-Disposition 头，指定下载文件名
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + encodedFileName);
            // 返回文件下载响应
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);

        } catch (MalformedURLException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(444).body(null);
        }
    }

    public static void uploadDocsByUrl(MultipartFile file, String url) throws IOException {
        Path uploadPath = Paths.get(url);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // 使用文件的原始名称
        file.transferTo(new File(uploadPath.toString()));

        // 返回文件的存储路径
    }

    public static ResponseEntity<Resource> getImage(String url) throws MalformedURLException {
        Path path = Paths.get(url);
        log.info(String.valueOf(path));

        // 将 Path 转换为 URI
        Resource resource = new UrlResource(path.toUri());
        // 检查文件是否存在
        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }
        if (resource.exists()) {
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } else {
            return ResponseEntity.notFound().build();
        }

    }
}
