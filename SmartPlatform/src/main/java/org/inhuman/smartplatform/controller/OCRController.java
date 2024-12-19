package org.inhuman.smartplatform.controller;

import lombok.extern.slf4j.Slf4j;
import org.inhuman.smartplatform.pojo.Result;
import org.inhuman.smartplatform.pojo.User;
import org.inhuman.smartplatform.service.OCRService;
import lombok.AllArgsConstructor;
import net.sourceforge.tess4j.TesseractException;
import org.inhuman.smartplatform.utils.JwtUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@AllArgsConstructor
@Slf4j
public class OCRController {
    private final OCRService ocrService;

    @PostMapping(value = "/recognize", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String recognizeImage(@RequestHeader("accessToken") String token,
                                 @RequestParam("file") MultipartFile file)
            throws TesseractException, IOException {

        // 解析 JWT 令牌
        User user = JwtUtils.getUserFromClaims(JwtUtils.parseJwt(token));
        if (user == null) {
            return null;
        }

        // 调用OcrService中的方法进行文字识别
        return ocrService.recognizeText(file);
    }
}