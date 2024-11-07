package org.inhuman.smartplatform.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TransmitFileUtils {

    public static String storeFile(int id,MultipartFile file) throws IOException {
        // 确保目录存在
        String uploadDir = "D://InHumanFile/pictures/";
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // 使用文件的原始名称
        Path filePath = uploadPath.resolve(String.valueOf(id) + ".jpg");
        file.transferTo(new File(filePath.toString()));

        // 返回文件的存储路径
        return filePath.toString();
    }


}
