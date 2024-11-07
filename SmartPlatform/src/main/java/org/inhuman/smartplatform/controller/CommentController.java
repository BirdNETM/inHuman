package org.inhuman.smartplatform.controller;

import lombok.extern.slf4j.Slf4j;
import org.inhuman.smartplatform.pojo.Comment;
import org.inhuman.smartplatform.pojo.Result;
import org.inhuman.smartplatform.pojo.User;
import org.inhuman.smartplatform.service.CommentService;
import org.inhuman.smartplatform.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping("/Comments-get")
    public Result getComments(@RequestHeader("accessToken") String token, @RequestParam("postingId") int postingId){
        try {
            // 解析 JWT 令牌
            User user = JwtUtils.getUserFromClaims(JwtUtils.parseJwt(token));

            List<Comment> comments = commentService.getComments(user.getId(),postingId);

            // 返回成功结果
            return Result.success(comments);

        } catch (Exception e) {
            log.error("发生错误: ", e);
            return Result.error("失败");
        }
    }

    @PostMapping("/Comments-set")
    public Result setComments(@RequestHeader("accessToken") String token, @RequestBody Comment comment){
        try {
            // 解析 JWT 令牌
            User user = JwtUtils.getUserFromClaims(JwtUtils.parseJwt(token));

            commentService.setComments(user.getId(),comment);

            // 返回成功结果
            return Result.success();

        } catch (Exception e) {
            log.error("发生错误: ", e);
            return Result.error("失败");
        }
    }




















}
