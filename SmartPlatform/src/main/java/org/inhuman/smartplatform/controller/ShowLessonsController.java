package org.inhuman.smartplatform.controller;


import lombok.extern.slf4j.Slf4j;
import org.inhuman.smartplatform.pojo.Doc;
import org.inhuman.smartplatform.pojo.Lesson;
import org.inhuman.smartplatform.pojo.Result;
import org.inhuman.smartplatform.pojo.User;
import org.inhuman.smartplatform.service.ShowLessonsService;
import org.inhuman.smartplatform.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

@Slf4j
@RestController

public class ShowLessonsController {

    @Autowired
    private ShowLessonsService showLessonsService;

    @PostMapping("/lessons")
    public Result showLessons(@RequestHeader("accessToken") String token) {
        try {
            // 解析 JWT 令牌
            User user = JwtUtils.getUserFromClaims(JwtUtils.parseJwt(token));
            if (user == null) {
                return Result.error("用户信息无效");
            }

            // 调用 Service 层获取课程列表
            List<Lesson> lessons = showLessonsService.findLessonsByUserId(user.getId());

            if (lessons.isEmpty()) {
                return Result.error("未找到课程");
            }

            // 返回成功结果
            return Result.success(lessons);

        } catch (Exception e) {
            log.error("获取课程信息时发生错误: ", e);
            return Result.error("获取课程信息失败");
        }
    }

    @PostMapping("/lessons-search")
    public Result searchLesson(@RequestHeader("accessToken") String token, @RequestParam("searchKey") String searchKey) {
        try {
            // 解析 JWT 令牌
            User user = JwtUtils.getUserFromClaims(JwtUtils.parseJwt(token));
            if (user == null) {
                return Result.error("用户信息无效");
            }

            // 调用 Service 层获取课程列表
            List<Lesson> lessons = showLessonsService.findLessonsByUserIdAndSearchKey(user.getId(),searchKey);

            if (lessons.isEmpty()) {
                return Result.error("未找到课程");
            }

            // 返回成功结果
            return Result.success(lessons);

        } catch (Exception e) {
            log.error("获取课程信息时发生错误: ", e);
            return Result.error("获取课程信息失败");
        }
    }

    @PostMapping("/lessons-detail")
    public Result showLessonDetail(@RequestHeader("accessToken") String token, @RequestParam("lessonId") int lessonId) {
        try {
            // 解析 JWT 令牌
            User user = JwtUtils.getUserFromClaims(JwtUtils.parseJwt(token));
            if (user == null) {
                return Result.error("用户信息无效");
            }

            // 调用 Service 层获取课程列表
            Lesson lesson = showLessonsService.showLessonDetail(user.getId(),lessonId);

            if (lesson == null) {
                return Result.error("未找到课程");
            }

            // 返回成功结果
            return Result.success(lesson);

        } catch (Exception e) {
            log.error("获取课程信息时发生错误: ", e);
            return Result.error("获取课程信息失败");
        }
    }

    @PostMapping("/lessons-docs")
    public Result getLessonsDocs(@RequestHeader("accessToken") String token, @RequestParam("lessonId") int lessonId,@RequestParam("fatherId") int fatherId) {
        try {
            // 解析 JWT 令牌
            User user = JwtUtils.getUserFromClaims(JwtUtils.parseJwt(token));
            if (user == null) {
                return Result.error("用户信息无效");
            }

            // 调用 Service 层获取课程列表
            List<Doc> doc = showLessonsService.getLessonsDocs(user.getId(),lessonId,fatherId);

            if (doc == null) {
                return Result.error("未找到课程");
            }

            // 返回成功结果
            return Result.success(doc);

        } catch (Exception e) {
            log.error("获取课程文件时发生错误: ", e);
            return Result.error("获取课程文件失败");
        }
    }

    @PostMapping("/lessons-docs-download")
    public ResponseEntity<Resource> downloadDocs(@RequestHeader("accessToken") String token, @RequestParam("docsId") int docsId) {
        try {
            // 解析 JWT 令牌
            User user = JwtUtils.getUserFromClaims(JwtUtils.parseJwt(token));
            log.info("begin file test");
            return showLessonsService.downloadDocs(user.getId(),docsId);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("lessons-outline-update")
    public Result updateLessonOutline(@RequestHeader("accessToken") String token, @RequestParam("outline") String outline, @RequestParam("lessonId") int lessonId) {
        try {
            // 解析 JWT 令牌
            User user = JwtUtils.getUserFromClaims(JwtUtils.parseJwt(token));
            if (user == null) {
                return Result.error("用户信息无效");
            }

            // 调用 Service 层获取课程列表
            showLessonsService.updateLessonOutline(user.getId(),lessonId,outline);

            return Result.success();

        } catch (Exception e) {
            if(Objects.equals(e.getMessage(), "no privilege")){
                return Result.error("没有权限");
            }
            log.error("获取课程信息时发生错误: ", e);
            return Result.error("获取课程信息失败");
        }
    }

    @PostMapping("lessons-docs-delete")
    public Result deleteLessonDocs(@RequestHeader("accessToken") String token, @RequestParam("docsId") int docsId) {
        try {
            // 解析 JWT 令牌
            User user = JwtUtils.getUserFromClaims(JwtUtils.parseJwt(token));
            if (user == null) {
                return Result.error("用户信息无效");
            }

            // 调用 Service 层获取课程列表
            showLessonsService.deleteLessonDocs(user.getId(),docsId);

            return Result.success();

        } catch (Exception e) {
            if(Objects.equals(e.getMessage(), "no privilege")){
                return Result.error("没有权限");
            }
            log.error("获取课程信息时发生错误: ", e);
            return Result.error("获取课程信息失败");
        }
    }

    @PostMapping("lessons-docs-license-update")
    public Result updateDownloadLicense(@RequestHeader("accessToken") String token, @RequestParam("docsId") int docsId, @RequestParam("license") int license) {
        try {
            // 解析 JWT 令牌
            User user = JwtUtils.getUserFromClaims(JwtUtils.parseJwt(token));
            if (user == null) {
                return Result.error("用户信息无效");
            }

            // 调用 Service 层获取课程列表
            showLessonsService.updateDownloadLicense(user.getId(),docsId,license);

            return Result.success();

        } catch (Exception e) {
            if(Objects.equals(e.getMessage(), "no privilege")){
                return Result.error("没有权限");
            }
            log.error("获取课程信息时发生错误: ", e);
            return Result.error("获取课程信息失败");
        }
    }

    @PostMapping("lessons-docs-insert")
        public Result insertDocs(
            @RequestHeader("accessToken") String token,
            @RequestParam("lessonId") int lessonId,
            @RequestParam("docFatherId") int docFatherId,
            @RequestParam("downloadLicense") int downloadLicense,
            @RequestPart("file") MultipartFile file,
            @RequestParam(value = "docName", required = false) String docName) {
        try {
            // Parse JWT token to get user info
            User user = JwtUtils.getUserFromClaims(JwtUtils.parseJwt(token));
            if (user == null) {
                return Result.error("用户信息无效");
            }
            showLessonsService.insertDocs(user.getId(),lessonId, docFatherId, downloadLicense, file, docName);
            return Result.success();
        } catch (Exception e) {
            if(Objects.equals(e.getMessage(), "no privilege")){
                return Result.error("没有权限");
            }
            log.error("插入课程文档信息时发生错误: ", e);
            return Result.error("插入课程文档信息失败");
        }
    }

    @PostMapping("/lessons-directory-insert")
    public Result insertDirectory(
            @RequestHeader("accessToken") String token,
            @RequestParam("lessonId") int lessonId,
            @RequestParam("docFatherId") int docFatherId,
            @RequestParam("downloadLicense") int downloadLicense,
            @RequestParam(value = "docName", required = false) String docName) {
        try {
            // Parse JWT token to get user info
            User user = JwtUtils.getUserFromClaims(JwtUtils.parseJwt(token));
            if (user == null) {
                return Result.error("用户信息无效");
            }
            showLessonsService.insertDirectory(user.getId(),lessonId, docFatherId, downloadLicense, docName);
            return Result.success();
        } catch (Exception e) {
            if(Objects.equals(e.getMessage(), "no privilege")){
                return Result.error("没有权限");
            }
            log.error("插入课程文档信息时发生错误: ", e);
            return Result.error("插入课程文档信息失败");
        }
    }
}

