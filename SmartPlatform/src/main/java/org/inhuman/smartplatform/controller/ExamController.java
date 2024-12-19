package org.inhuman.smartplatform.controller;


import lombok.extern.slf4j.Slf4j;
import org.inhuman.smartplatform.pojo.Classroom;
import org.inhuman.smartplatform.pojo.Exam;
import org.inhuman.smartplatform.pojo.Result;
import org.inhuman.smartplatform.pojo.User;
import org.inhuman.smartplatform.service.ExamService;
import org.inhuman.smartplatform.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@Slf4j
public class ExamController {
    @Autowired
    private ExamService examService;

    @PostMapping("/Exam-get")
    public Result getExams(@RequestHeader("accessToken") String token, @RequestParam("lessonId") int lessonId){
        try {
            // 解析 JWT 令牌
            User user = JwtUtils.getUserFromClaims(JwtUtils.parseJwt(token));

            List<Exam> exams = examService.getExams(user.getId(),lessonId);

            // 返回成功结果
            return Result.success(exams);

        } catch (Exception e) {
            log.error("发生错误: ", e);
            return Result.error("失败");
        }
    }

    @PostMapping("/Exam-add")
    public Result addExam(@RequestHeader("accessToken") String token, @RequestBody Exam exam){
        try {
            // 解析 JWT 令牌
            User user = JwtUtils.getUserFromClaims(JwtUtils.parseJwt(token));
            log.info("begin service");
            List<Exam> exams = examService.addExam(user.getId(),exam);

            if(Objects.isNull(exams)){
                return Result.success();
            }else{
                return Result.error("时间冲突",exams);
            }
            // 返回成功结果


        } catch (Exception e) {
            if(Objects.equals(e.getMessage(), "no privilege")){
                return Result.error("没有权限");
            }
            log.error("发生错误: ", e);
            return Result.error("失败");
        }
    }

    @PostMapping("/Exam-delete")
    public Result deleteExam(@RequestHeader("accessToken") String token, @RequestParam("examId") int examId){
        try {
            // 解析 JWT 令牌
            User user = JwtUtils.getUserFromClaims(JwtUtils.parseJwt(token));

            examService.deleteExam(user.getId(),examId);

            // 返回成功结果
            return Result.success();

        } catch (Exception e) {
            if(Objects.equals(e.getMessage(), "no privilege")){
                return Result.error("没有权限");
            }
            log.error("发生错误: ", e);
            return Result.error("失败");
        }
    }

    @PostMapping("/Exam-update")
    public Result updateExam(@RequestHeader("accessToken") String token, @RequestBody Exam exam){
        try {
            // 解析 JWT 令牌
            User user = JwtUtils.getUserFromClaims(JwtUtils.parseJwt(token));

            List<Exam> exams = examService.updateExam(user.getId(),exam);

            if(Objects.isNull(exams)){
                return Result.success();
            }else{
                return Result.success(exams);
            }

        } catch (Exception e) {
            if(Objects.equals(e.getMessage(), "no privilege")){
                return Result.error("没有权限");
            }
            log.error("发生错误: ", e);
            return Result.error("失败");
        }
    }

    @PostMapping("/Classroom-get")
    public Result getClassrooms(@RequestHeader("accessToken") String token){
        try {
            // 解析 JWT 令牌
            User user = JwtUtils.getUserFromClaims(JwtUtils.parseJwt(token));

            List<Classroom> classrooms = examService.getClassrooms(user.getId());

            // 返回成功结果
            return Result.success(classrooms);

        } catch (Exception e) {
            log.error("发生错误: ", e);
            return Result.error("失败");
        }
    }

}
