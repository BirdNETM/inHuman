package org.inhuman.smartplatform.controller;

import lombok.extern.slf4j.Slf4j;
import org.inhuman.smartplatform.pojo.Comment;
import org.inhuman.smartplatform.pojo.Result;
import org.inhuman.smartplatform.pojo.Tutor;
import org.inhuman.smartplatform.pojo.User;
import org.inhuman.smartplatform.service.TutorService;
import org.inhuman.smartplatform.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
public class TutorController {
    @Autowired
    private TutorService tutorService;

    @PostMapping("/Tutor-get")
    public Result getTutors(@RequestHeader("accessToken") String token, @RequestParam("lessonId") int lessonId){
        try {
            // 解析 JWT 令牌
            User user = JwtUtils.getUserFromClaims(JwtUtils.parseJwt(token));

            List<Tutor> tutors = tutorService.getTutors(user.getId(),lessonId);

            // 返回成功结果
            return Result.success(tutors);

        } catch (Exception e) {
            log.error("发生错误: ", e);
            return Result.error("失败");
        }
    }

    @PostMapping("/Tutor-add")
    public Result addTutor(@RequestHeader("accessToken") String token, @RequestParam("studentCode") String studentCode, @RequestParam("lessonId") int lessonId){
        try {
            // 解析 JWT 令牌
            User user = JwtUtils.getUserFromClaims(JwtUtils.parseJwt(token));

            tutorService.addTutor(user.getId(),studentCode,lessonId);

            // 返回成功结果
            return Result.success();

        } catch (Exception e) {
            log.error("发生错误: ", e);
            return Result.error("失败");
        }
    }

    @PostMapping("/Tutor-delete")
    public Result deleteTutor(@RequestHeader("accessToken") String token, @RequestParam("tutorId") int tutorId, @RequestParam("lessonId") int lessonId){
        try {
            // 解析 JWT 令牌
            User user = JwtUtils.getUserFromClaims(JwtUtils.parseJwt(token));

            tutorService.deleteTutor(user.getId(),tutorId,lessonId);

            // 返回成功结果
            return Result.success();

        } catch (Exception e) {
            log.error("发生错误: ", e);
            return Result.error("失败");
        }
    }

    @PostMapping("/Tutor-update")
    public Result updateTutor(@RequestHeader("accessToken") String token, @RequestBody Tutor tutor){
        log.info(tutor.toString());
        try {
            // 解析 JWT 令牌
            User user = JwtUtils.getUserFromClaims(JwtUtils.parseJwt(token));

            tutorService.updateTutor(user.getId(),tutor);

            // 返回成功结果
            return Result.success();

        } catch (Exception e) {
            log.error("发生错误: ", e);
            return Result.error("失败");
        }
    }

    
}
