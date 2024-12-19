package org.inhuman.smartplatform.controller;


import lombok.extern.slf4j.Slf4j;
import org.inhuman.smartplatform.pojo.Lesson;
import org.inhuman.smartplatform.pojo.Result;
import org.inhuman.smartplatform.pojo.User;
import org.inhuman.smartplatform.service.LikeService;
import org.inhuman.smartplatform.service.ShowLessonsService;
import org.inhuman.smartplatform.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController

public class LikeController {

    @Autowired
    private LikeService LikeService;


    @PostMapping("/like")
    public Result showLessons(@RequestHeader("accessToken") String token,
                              @RequestParam("type") int type,
                              @RequestParam("postingId") int postingId) {
        try {
            // 解析 JWT 令牌
            User user = JwtUtils.getUserFromClaims(JwtUtils.parseJwt(token));
            if (user == null) {
                return Result.error("用户信息无效");
            }
            LikeService.like(type, user.getId(), postingId);
            // 返回成功结果
            return Result.success();

        } catch (Exception e) {
            log.error("点赞失败: ", e);
            return Result.error("点赞失败");
        }
    }
}
