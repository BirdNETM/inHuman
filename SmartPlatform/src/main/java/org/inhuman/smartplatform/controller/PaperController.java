package org.inhuman.smartplatform.controller;


import lombok.extern.slf4j.Slf4j;
import org.inhuman.smartplatform.pojo.*;
import org.inhuman.smartplatform.service.PaperService;
import org.inhuman.smartplatform.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@Slf4j
public class PaperController {
    @Autowired
    private PaperService paperService;

    @PostMapping("/Exam-AssignQuestion")
    public Result AssignQuestions(@RequestHeader("accessToken") String token,
                                  @RequestParam("examId") int examId,
                                  @RequestParam("type") String type,
                                  @RequestParam("description") String description,
                                  @RequestParam("suggestedAnswer") String suggestedAnswer,
                                  @RequestParam("mark") double mark,
                                  @RequestParam(value = "choices", required = false) List<String> choices) {
        try {
            // 如果 choices 未传入，设置为默认空列表
            if (choices == null) {
                choices = new ArrayList<>();
            }

            // 解析 JWT 令牌
            User user = JwtUtils.getUserFromClaims(JwtUtils.parseJwt(token));
            if (user == null) {
                return Result.error("用户信息无效");
            }

            paperService.assignQuestion(user.getId(), examId, type, description, suggestedAnswer, mark, choices);
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


    @PostMapping("/Exam-PublishHomework")
    public Result PublishHomework(@RequestHeader("accessToken") String token,
                                  @RequestParam("examId") int examId){
        try {
            // 解析 JWT 令牌
            User user = JwtUtils.getUserFromClaims(JwtUtils.parseJwt(token));
            if (user == null) {
                return Result.error("用户信息无效");
            }

            // 返回成功结果
            return Result.success();

        } catch (Exception e) {
            log.error("发生错误: ", e);
            return Result.error("失败");
        }
    }

    @PostMapping("/Exam-ReceiveHomework")
    public Result ReceiveHomework(@RequestHeader("accessToken") String token,
                                  @RequestParam("examId") int examId,
                                  @RequestBody Map<Integer, String> answers){
        try {
            // 解析 JWT 令牌
            User user = JwtUtils.getUserFromClaims(JwtUtils.parseJwt(token));
            if (user == null) {
                return Result.error("用户信息无效");
            }

            int studentId = user.getId();
            paperService.receiveHomework(user.getId(),studentId,examId,answers);
            // 返回成功结果
            return Result.success();

        } catch (Exception e) {
            log.error("发生错误: ", e);
            return Result.error("失败");
        }
    }

    @PostMapping("/Exam-ManualScore")
    public Result ManualScore(@RequestHeader("accessToken") String token,
                                  @RequestParam("examId") int examId,
                                  @RequestParam("studentId") int studentId,
                                  @RequestBody Map<Integer, Double> marks){
        try {
            // 解析 JWT 令牌
            User user = JwtUtils.getUserFromClaims(JwtUtils.parseJwt(token));
            if (user == null) {
                return Result.error("用户信息无效");
            }

            paperService.manualScore(user.getId(),studentId,examId,marks);
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

    @PostMapping("/Exam-summary")
    public Result Summary(@RequestHeader("accessToken") String token,
                              @RequestParam("examId") int examId){
        try {
            // 解析 JWT 令牌
            User user = JwtUtils.getUserFromClaims(JwtUtils.parseJwt(token));
            if (user == null) {
                return Result.error("用户信息无效");
            }

            Summary summary = paperService.summary(user.getId(), examId);
            // 返回成功结果
            return Result.success(summary);

        } catch (Exception e) {
            log.error("发生错误: ", e);
            return Result.error("失败");
        }
    }

    @PostMapping("/Exam-getQuestions")
    public Result getQuestions(@RequestHeader("accessToken") String token,
                          @RequestParam("examId") int examId){
        try {
            // 解析 JWT 令牌
            User user = JwtUtils.getUserFromClaims(JwtUtils.parseJwt(token));
            if (user == null) {
                return Result.error("用户信息无效");
            }

            List<Question> questions = paperService.getQuestions(user.getId(),examId);
            // 返回成功结果
            return Result.success(questions);

        } catch (Exception e) {
            log.error("发生错误: ", e);
            return Result.error("失败");
        }
    }

    @PostMapping("/Exam-getExamSubmits")
    public Result getExamSubmit(@RequestHeader("accessToken") String token,
                               @RequestParam("examId") int examId,
                                @RequestParam("studentId") int studentId){
        try {
            // 解析 JWT 令牌
            User user = JwtUtils.getUserFromClaims(JwtUtils.parseJwt(token));
            if (user == null) {
                return Result.error("用户信息无效");
            }

            List<Answer> questions = paperService.getExamSubmits(user.getId(),examId, studentId);
            // 返回成功结果
            return Result.success(questions);

        } catch (Exception e) {
            log.error("发生错误: ", e);
            return Result.error("失败");
        }
    }

    @PostMapping("/Exam-getQuestionById")
    public Result getQuestionById(@RequestHeader("accessToken") String token,
                                @RequestParam("questionId") int questionId){
        try {
            // 解析 JWT 令牌
            User user = JwtUtils.getUserFromClaims(JwtUtils.parseJwt(token));
            if (user == null) {
                return Result.error("用户信息无效");
            }

            Question questions = paperService.getQuestionById(user.getId(),questionId);
            // 返回成功结果
            return Result.success(questions);

        } catch (Exception e) {
            log.error("发生错误: ", e);
            return Result.error("失败");
        }
    }

    @PostMapping("/Exam-getMarkById")
    public Result getMarkById(@RequestHeader("accessToken") String token,
                              @RequestParam("examId") int examId){
        try {
            // 解析 JWT 令牌
            User user = JwtUtils.getUserFromClaims(JwtUtils.parseJwt(token));
            if (user == null) {
                return Result.error("用户信息无效");
            }


            int id = user.getId();
            double mark = paperService.getMarkById(id, examId);
            // 返回成功结果
            return Result.success(mark);

        } catch (Exception e) {
            log.error("发生错误: ", e);
            return Result.error("失败");
        }
    }

    @PostMapping("/Exam-getAnswersByQuestionId")
        public Result getAnswersByQuestionId(@RequestHeader("accessToken") String token,
                              @RequestParam("questionId") int questionId){
        try {
            // 解析 JWT 令牌
            User user = JwtUtils.getUserFromClaims(JwtUtils.parseJwt(token));
            if (user == null) {
                return Result.error("用户信息无效");
            }


            int id = user.getId();

            // 返回成功结果
            return Result.success(paperService.getAnswersByQuestionId(id, questionId));

        } catch (Exception e) {
            log.error("发生错误: ", e);
            return Result.error("失败");
        }
    }
}
