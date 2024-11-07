package org.inhuman.smartplatform.controller;

import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.inhuman.smartplatform.pojo.Result;
import org.inhuman.smartplatform.pojo.User;
import org.inhuman.smartplatform.service.LoginService;
import org.inhuman.smartplatform.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController

public class LoginController {

    @Autowired
    private LoginService loginService;

    // 用户登录接口
    @PostMapping("/login")
    public Result logIn(@RequestBody User user) {
        log.info("log in user");

        User user1 = loginService.LogIn(user);
        if (user1 != null) {
            log.info("{}log in user success", user1);
            Map<String, Object> claims = new HashMap<>();
            claims.put("id", user1.getId());
            claims.put("userName", user1.getUserName());
            claims.put("position", user1.getPosition());

            // 生成 Access Token 和 Refresh Token
            String accessToken = JwtUtils.generateJwt(claims);
            String refreshToken = JwtUtils.generateRefreshToken(claims);

            Map<String, String> tokens = new HashMap<>();
            tokens.put("accessToken", accessToken);
            tokens.put("refreshToken", refreshToken);
            tokens.put("position", String.valueOf(user1.getPosition()));

            return Result.success(tokens);
        }
        log.info("log in user fail");
        return Result.error("用户名或密码错误");
    }

    // 刷新 Token 的接口
    @PostMapping("/refresh-token")
    public Result refreshToken(@RequestHeader("refreshToken") String refreshToken) {
        try {
            log.info("refresh token");
            Claims claims = JwtUtils.parseRefreshToken(refreshToken);
            String newAccessToken = JwtUtils.generateJwt(claims);

            // 生成新的 Refresh Token
            String newRefreshToken = JwtUtils.generateRefreshToken(claims);

            Map<String, String> tokens = new HashMap<>();
            tokens.put("accessToken", newAccessToken);
            tokens.put("refreshToken", newRefreshToken);

            return Result.success(tokens);
        } catch (Exception e) {
            log.info(String.valueOf(e));
            return Result.error("Invalid Refresh Token");
        }
    }
}
