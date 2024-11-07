package org.inhuman.smartplatform.controller;


import lombok.extern.slf4j.Slf4j;
import org.inhuman.smartplatform.pojo.Notice;
import org.inhuman.smartplatform.pojo.Result;
import org.inhuman.smartplatform.pojo.User;
import org.inhuman.smartplatform.service.NoticeService;
import org.inhuman.smartplatform.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    @PostMapping("/Notices")
    public Result getNotices(@RequestHeader("accessToken") String token) {
        try {
            // 解析 JWT 令牌
            User user = JwtUtils.getUserFromClaims(JwtUtils.parseJwt(token));
            if (user == null) {
                return Result.error("用户信息无效");
            }

            // 调用 Service 层获取Notice列表
            List<Notice> notices = noticeService.getNoticeByUserId(user.getId());

            if (notices.isEmpty()) {
                return Result.error("未找到通知");
            }

            // 返回成功结果
            return Result.success(notices);

        } catch (Exception e) {
            log.error("获取通知信息时发生错误: ", e);
            return Result.error("获取通知信息失败");
        }
    }

    @PostMapping("/Notices-search")
    public Result searchNotices(@RequestHeader("accessToken") String token, @RequestParam("searchKey") String searchKey) {
        try {
            // 解析 JWT 令牌
            User user = JwtUtils.getUserFromClaims(JwtUtils.parseJwt(token));
            log.info(user.toString());

            // 调用 Service 层获取Notice列表
            List<Notice> notices = noticeService.searchNotices(user.getId(),searchKey);

            if (notices.isEmpty()) {
                return Result.error("未找到通知");
            }

            // 返回成功结果
            return Result.success(notices);

        } catch (Exception e) {
            log.error("搜索通知信息时发生错误: ", e);
            return Result.error("获取通知信息失败");
        }
    }

    @PostMapping("/Notices-detail")
    public Result getNoticeByNoticeId(@RequestHeader("accessToken") String token, @RequestParam("noticeId") int noticeId) {
        try {
            // 解析 JWT 令牌
            User user = JwtUtils.getUserFromClaims(JwtUtils.parseJwt(token));
            if (user == null) {
                return Result.error("用户信息无效");
            }

            // 调用 Service 层获取Notice列表
            List<Notice> notices = noticeService.getNoticeByNoticeId(user.getId(),noticeId);

            if (notices.isEmpty()) {
                return Result.error("未找到通知");
            }

            // 返回成功结果
            return Result.success(notices);

        } catch (Exception e) {
            log.error("获取通知详细信息时发生错误: ", e);
            return Result.error("获取通知信息失败");
        }
    }

    @PostMapping("/Notice-new")
    public Result getNoticeNew(@RequestHeader("accessToken") String token) {
        try {
            // 解析 JWT 令牌
            User user = JwtUtils.getUserFromClaims(JwtUtils.parseJwt(token));
            if (user == null) {
                return Result.error("用户信息无效");
            }

            // 调用 Service 层获取Notice列表
            List<Notice> notices = noticeService.getNoticeNew(user.getId());

            if (notices.isEmpty()) {
                log.info("没有未读通知");
                return Result.error("没有未读通知");
            }

            // 返回成功结果
            return Result.success(notices);

        } catch (Exception e) {
            log.error("获取通知详细信息时发生错误: ", e);
            return Result.error("获取通知信息失败");
        }
    }


}
