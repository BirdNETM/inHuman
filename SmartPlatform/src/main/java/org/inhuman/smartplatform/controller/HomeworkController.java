package org.inhuman.smartplatform.controller;


import lombok.extern.slf4j.Slf4j;
import org.inhuman.smartplatform.pojo.Homework;
import org.inhuman.smartplatform.pojo.Result;
import org.inhuman.smartplatform.pojo.User;
import org.inhuman.smartplatform.service.HomeworkService;
import org.inhuman.smartplatform.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
public class HomeworkController {

    @Autowired
    HomeworkService homeworkService;

    @PostMapping("/Homework")
    public Result getHomeworks(@RequestHeader("accessToken") String token,@RequestParam("lessonId") int lessonId) {
        try {
            // 解析 JWT 令牌
            User user = JwtUtils.getUserFromClaims(JwtUtils.parseJwt(token));
            // 调用 Service 层获取Homeworks
            List<Homework> homeworks = homeworkService.getHomeworks(user.getId(),lessonId);

            if (homeworks == null) {
                return Result.error("作业为空");
            }

            // 返回成功结果
            return Result.success(homeworks);

        } catch (Exception e) {
            log.error("获取作业时发生错误: ", e);
            return Result.error("获取作业失败");
        }
    }

    @PostMapping("/Homework-id")
    public Result getHomeworksById(@RequestHeader("accessToken") String token,@RequestParam("homeworkId") int homeworkId) {
        try {
            // 解析 JWT 令牌
            User user = JwtUtils.getUserFromClaims(JwtUtils.parseJwt(token));
            // 调用 Service 层获取Homeworks
            Homework homework = homeworkService.getHomeworksById(user.getId(),homeworkId);

            if (homework == null) {
                return Result.error("作业为空");
            }

            // 返回成功结果
            return Result.success(homework);

        } catch (Exception e) {
            log.error("获取作业时发生错误: ", e);
            return Result.error("获取作业失败");
        }
    }

    @PostMapping("/Homework-files-download")
    public ResponseEntity<Resource> downloadHomeworkFiles(@RequestHeader("accessToken") String token, @RequestParam("homeworkId") int homeworkId) {
        try {
            // 解析 JWT 令牌
            User user = JwtUtils.getUserFromClaims(JwtUtils.parseJwt(token));
            return homeworkService.downloadHomeworkFiles(user.getId(),homeworkId);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

}
