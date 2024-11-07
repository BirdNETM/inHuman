package org.inhuman.smartplatform.controller;

import lombok.extern.slf4j.Slf4j;
import org.inhuman.smartplatform.pojo.HomePage;
import org.inhuman.smartplatform.pojo.Result;
import org.inhuman.smartplatform.pojo.User;
import org.inhuman.smartplatform.service.HomePageService;
import org.inhuman.smartplatform.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
public class HomePageController {

    @Autowired
    HomePageService homePageService;

    @PostMapping("/HomePage")
    public Result getHomePage(@RequestHeader("accessToken") String token){
        try {
            // 解析 JWT 令牌
            User user = JwtUtils.getUserFromClaims(JwtUtils.parseJwt(token));
            if (user == null) {
                return Result.error("用户信息无效");
            }

            // 调用 Service 层获取HomePage
            HomePage homePage = homePageService.getHomePage(user.getId());

            if (homePage == null) {
                return Result.error("未找到个人主页");
            }

            // 返回成功结果
            return Result.success(homePage);

        } catch (Exception e) {
            log.error("获取个人主页时发生错误: ", e);
            return Result.error("获取个人主页失败");
        }
    }

    @PostMapping("/HomePage-Avatar")
    public Result getHomePageAvatarAsBase64(@RequestHeader("accessToken") String token){
        try {
            // 解析 JWT 令牌
            User user = JwtUtils.getUserFromClaims(JwtUtils.parseJwt(token));
            if (user == null) {
                return Result.error("用户信息无效");
            }

            // 调用 Service 层获取HomePage
            String avatarAsBase64 = homePageService.getHomePageAvatarAsBase64(user.getId());

            // 返回成功结果
            return Result.success(avatarAsBase64);

        } catch (Exception e) {
            log.error("获取个人主页时发生错误: ", e);
            return Result.error("获取个人主页失败");
        }
    }

    @PostMapping("/HomePage-update")
    public Result updateHomePage(@RequestHeader("accessToken") String token, @RequestBody HomePage homePage){
        try {
            // 解析 JWT 令牌
            User user = JwtUtils.getUserFromClaims(JwtUtils.parseJwt(token));
            if (user == null) {
                return Result.error("用户信息无效");
            }
            // 调用 Service 层获取HomePage
            homePageService.updateHomePage(user.getId(),homePage);

            // 返回成功结果
            return Result.success();

        } catch (Exception e) {
            log.error("获取个人主页时发生错误: ", e);
            return Result.error("获取个人主页失败");
        }
    }

    @PostMapping("/HomePage-updateAvatar")
    public Result updateHomePageAvatar(@RequestHeader("accessToken") String token, @RequestBody MultipartFile file){
        try {
            // 解析 JWT 令牌
            User user = JwtUtils.getUserFromClaims(JwtUtils.parseJwt(token));
            if (user == null) {
                return Result.error("用户信息无效");
            }
            // 调用 Service 层获取HomePage
            homePageService.updateHomePageAvatar(user.getId(),file);

            // 返回成功结果
            return Result.success();

        } catch (Exception e) {
            log.error("获取个人主页时发生错误: ", e);
            return Result.error("获取个人主页失败");
        }
    }

    @PostMapping("/HomePage-other")
    public Result getHomePageOther(@RequestHeader("accessToken") String token,@RequestParam int id){
        try {
            // 解析 JWT 令牌
            User user = JwtUtils.getUserFromClaims(JwtUtils.parseJwt(token));
            if (user == null) {
                return Result.error("用户信息无效");
            }

            // 调用 Service 层获取HomePage
            HomePage homePage = homePageService.getHomePageOther(id);

            if (homePage == null) {
                return Result.error("未找到个人主页");
            }

            // 返回成功结果
            return Result.success(homePage);

        } catch (Exception e) {
            log.error("获取个人主页时发生错误: ", e);
            return Result.error("获取个人主页失败");
        }
    }
}
