package org.inhuman.smartplatform.controller;

import lombok.extern.slf4j.Slf4j;
import org.inhuman.smartplatform.pojo.Mutual;
import org.inhuman.smartplatform.pojo.Result;
import org.inhuman.smartplatform.pojo.User;
import org.inhuman.smartplatform.service.MutualEvaluationService;
import org.inhuman.smartplatform.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Slf4j
@RestController
public class MutualEvaluationController {
    @Autowired
    private MutualEvaluationService mutualEvaluationService;

    @PostMapping("/AssignTasks")
    public Result AssignTasks(@RequestHeader("accessToken") String token,
                              @RequestParam("homeworkId") int homeworkId,
                              @RequestParam("deadLine") String deadLine){

        try {
            // 解析 JWT 令牌
            User user = JwtUtils.getUserFromClaims(JwtUtils.parseJwt(token));
            if (user == null) {
                return Result.error("用户信息无效");
            }
            // 手动解析日期时间字符串
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime parsedDeadline;
            try {
                parsedDeadline = LocalDateTime.parse(deadLine, formatter);
            } catch (DateTimeParseException e) {
                return Result.error("日期格式错误，请使用 yyyy-MM-dd HH:mm:ss 格式");
            }

            mutualEvaluationService.assignTasks(homeworkId, parsedDeadline);

            // 返回成功结果
            return Result.success();

        } catch (Exception e) {
            log.error("发生错误: ", e);
            return Result.error("您已经设置过互评啦！");
        }
    }

    @PostMapping("/ReceiveTasks")
    public Result ReceiveTasks(@RequestHeader("accessToken") String token,
                               @RequestParam("homeWorkId") int homeWorkId,
                               @RequestParam("revieweeId") int revieweeId,
                               @RequestParam("score") int score){

        try {
            // 解析 JWT 令牌
            User user = JwtUtils.getUserFromClaims(JwtUtils.parseJwt(token));
            if (user == null) {
                return Result.error("用户信息无效");
            }


            mutualEvaluationService.receiveTasks(homeWorkId, user.getId(), revieweeId, score);

            // 返回成功结果
            return Result.success();

        } catch (Exception e) {
            log.error("发生错误: ", e);
            return Result.error("失败");
        }
    }

    @PostMapping("/getSpecificMutual")
    public Result getSpecificMutual(@RequestHeader("accessToken") String token,
                               @RequestParam("homeWorkId") int homeWorkId){

        try {
            // 解析 JWT 令牌
            User user = JwtUtils.getUserFromClaims(JwtUtils.parseJwt(token));
            if (user == null) {
                return Result.error("用户信息无效");
            }


            List<Mutual> Mutuals = mutualEvaluationService.getSpecificMutual(user.getId(), homeWorkId);

            // 返回成功结果
            return Result.success(Mutuals);

        } catch (Exception e) {
            log.error("发生错误: ", e);
            return Result.error("失败");
        }
    }

    @PostMapping("/getHomeWork")
    public ResponseEntity<Resource> getHomeWork(@RequestHeader("accessToken") String token,
                                                @RequestParam("homeWorkId") int homeWorkId,
                                                @RequestParam("studentId") int studentId){
        try {
            // 解析 JWT 令牌
            User user = JwtUtils.getUserFromClaims(JwtUtils.parseJwt(token));
            return mutualEvaluationService.getHomeWork(studentId,homeWorkId);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

}
