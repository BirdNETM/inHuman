package org.inhuman.smartplatform.controller;

import lombok.extern.slf4j.Slf4j;
import org.inhuman.smartplatform.pojo.Notice;
import org.inhuman.smartplatform.pojo.Postings;
import org.inhuman.smartplatform.pojo.Result;
import org.inhuman.smartplatform.pojo.User;
import org.inhuman.smartplatform.service.PostingsService;
import org.inhuman.smartplatform.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.List;
@Slf4j
@RestController
public class PostingsController {

    @Autowired
    PostingsService postingsService;

    @PostMapping("Postings")
    public Result getPostings(@RequestHeader("accessToken") String token, @RequestParam("lessonId") int lessonId){
        try {
            // 解析 JWT 令牌
            User user = JwtUtils.getUserFromClaims(JwtUtils.parseJwt(token));
            if (user == null) {
                return Result.error("用户信息无效");
            }

            List<Postings> postings =  postingsService.getPostings(user.getId(),lessonId);

            // 返回成功结果
            return Result.success(postings);

        } catch (Exception e) {
            log.error("发生错误: ", e);
            return Result.error("失败");
        }
    }

    @PostMapping("Postings-create-content")
    public Result setPosting(@RequestHeader("accessToken") String token, @RequestParam("lessonId") int lessonId, @RequestBody Postings postings) {
        try {
            // 解析 JWT 令牌
            User user = JwtUtils.getUserFromClaims(JwtUtils.parseJwt(token));
            if (user == null) {
                return Result.error("用户信息无效");
            }

            int id = postingsService.setPosting(user.getId(),lessonId,postings);

            // 返回成功结果
            return Result.success(id);

        } catch (Exception e) {
            log.error("发生错误: ", e);
            return Result.error("失败");
        }
    }

    @PostMapping("Postings-insert-picture")
    public Result insertPostingPicture(@RequestHeader("accessToken") String token, @RequestParam("postingId") int postingId, @RequestParam("pictureId") int pictureId, @RequestBody MultipartFile file) {
        try {
            // 解析 JWT 令牌
            User user = JwtUtils.getUserFromClaims(JwtUtils.parseJwt(token));
            if (user == null) {
                return Result.error("用户信息无效");
            }

            postingsService.insertPostingPicture(user.getId(),postingId,pictureId,file);

            // 返回成功结果
            return Result.success();

        } catch (Exception e) {
            log.error("发生错误: ", e);
            return Result.error("失败");
        }
    }

    @PostMapping("Postings-detail")
    public Result getPostingDetailById(@RequestHeader("accessToken") String token, @RequestParam("postingId") int postingId) {
        try {
            // 解析 JWT 令牌
            User user = JwtUtils.getUserFromClaims(JwtUtils.parseJwt(token));
            if (user == null) {
                return Result.error("用户信息无效");
            }

            Postings postings = postingsService.getPostingDetailById(user.getId(),postingId);

            // 返回成功结果
            return Result.success(postings);

        } catch (Exception e) {
            log.error("发生错误: ", e);
            return Result.error("失败");
        }
    }

    @PostMapping("Postings-pictures")
    public ResponseEntity<Resource> getPostingPicturesById(@RequestHeader("accessToken") String token, @RequestParam("postingId") int postingId, @RequestParam("pictureId") int pictureId) {
        try {
            // 解析 JWT 令牌
            User user = JwtUtils.getUserFromClaims(JwtUtils.parseJwt(token));
            return postingsService.getPostingPicturesById(user.getId(),postingId,pictureId);
        } catch (Exception e) {
            log.error("发生错误: ", e);
            return ResponseEntity.internalServerError().build();
        }
    }

}
