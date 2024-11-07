package org.inhuman.smartplatform.controller;

import lombok.extern.slf4j.Slf4j;
import org.inhuman.smartplatform.pojo.*;
import org.inhuman.smartplatform.service.FavoritesService;
import org.inhuman.smartplatform.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
public class FavoritesController {

    @Autowired
    FavoritesService favoritesService;

    @PostMapping("/Favorites")
    public Result getFavorites(@RequestHeader("accessToken") String token) {
        try {
            // 解析 JWT 令牌
            User user = JwtUtils.getUserFromClaims(JwtUtils.parseJwt(token));
            if (user == null) {
                return Result.error("用户信息无效");
            }

            // 调用 Service 层获取HomePage
            List<Postings> favorites = favoritesService.getFavorites(user.getId());

            if (favorites == null) {
                return Result.error("收藏夹为空");
            }

            // 返回成功结果
            return Result.success(favorites);

        } catch (Exception e) {
            log.error("获取个人主页时发生错误: ", e);
            return Result.error("获取个人主页失败");
        }
    }

    @PostMapping("/Favorites-Order-By-Type")
    public Result getFavoritesOrderByType(@RequestHeader("accessToken") String token) {
        try {
            // 解析 JWT 令牌
            User user = JwtUtils.getUserFromClaims(JwtUtils.parseJwt(token));
            if (user == null) {
                return Result.error("用户信息无效");
            }

            // 调用 Service 层获取HomePage
            List<FavoritiesType> favoritiesTypeList = favoritesService.getFavoritesOrderByType(user.getId());

            if (favoritiesTypeList == null) {
                return Result.error("收藏夹为空");
            }

            // 返回成功结果
            return Result.success(favoritiesTypeList);

        } catch (Exception e) {
            log.error("获取个人主页时发生错误: ", e);
            return Result.error("获取个人主页失败");
        }
    }

    @PostMapping("/Favorites-add")
    public Result addFavorites(@RequestHeader("accessToken") String token, @RequestParam("postingId") int postingId,@RequestParam("type") String type) {
        try {
            // 解析 JWT 令牌
            User user = JwtUtils.getUserFromClaims(JwtUtils.parseJwt(token));
            if (user == null) {
                return Result.error("用户信息无效");
            }

            // 调用 Service 层获取HomePage
            favoritesService.addFavorites(user.getId(),postingId,type);


            // 返回成功结果
            return Result.success("添加成功");

        } catch (Exception e) {
            log.error("添加favorites成功: ", e);
            return Result.error("添加favorites失败");
        }
    }

    @PostMapping("/Favorites-delete")
    public Result deleteFavorites(@RequestHeader("accessToken") String token, @RequestParam("postingId") int postingId) {
        try {
            // 解析 JWT 令牌
            User user = JwtUtils.getUserFromClaims(JwtUtils.parseJwt(token));
            if (user == null) {
                return Result.error("用户信息无效");
            }

            // 调用 Service 层获取HomePage
            favoritesService.deleteFavorites(user.getId(),postingId);


            // 返回成功结果
            return Result.success("删除成功");

        } catch (Exception e) {
            log.error("删除favorites 失败: ", e);
            return Result.error("删除favorites失败");
        }
    }

    @PostMapping("/Favorites-type")
    public Result updateFavoritesType(@RequestHeader("accessToken") String token, @RequestParam("postingId") int postingId,@RequestParam("type") String type) {
        try {
            // 解析 JWT 令牌
            User user = JwtUtils.getUserFromClaims(JwtUtils.parseJwt(token));
            if (user == null) {
                return Result.error("用户信息无效");
            }

            // 调用 Service 层获取HomePage
            favoritesService.updateFavoritesType(user.getId(),postingId,type);


            // 返回成功结果
            return Result.success("修改分类成功");

        } catch (Exception e) {
            log.error("修改分类favorites 失败: ", e);
            return Result.error("修改分类favorites失败");
        }
    }

    @PostMapping("/Favorites-add-type")
    public Result addFavoritesType(@RequestHeader("accessToken") String token, @RequestParam("type") String type) {
        try {
            // 解析 JWT 令牌
            User user = JwtUtils.getUserFromClaims(JwtUtils.parseJwt(token));
            if (user == null) {
                return Result.error("用户信息无效");
            }

            // 调用 Service 层获取HomePage
            favoritesService.addFavoritesType(user.getId(),type);


            // 返回成功结果
            return Result.success("添加成功");

        } catch (Exception e) {
            log.error("添加favorites成功: ", e);
            return Result.error("添加favorites失败");
        }
    }

    @PostMapping("/Favorites-delete-type")
    public Result deleteFavoritesType(@RequestHeader("accessToken") String token, @RequestParam("type") String type) {
        try {
            // 解析 JWT 令牌
            User user = JwtUtils.getUserFromClaims(JwtUtils.parseJwt(token));
            if (user == null) {
                return Result.error("用户信息无效");
            }

            // 调用 Service 层获取HomePage
            favoritesService.deleteFavoritesType(user.getId(),type);


            // 返回成功结果
            return Result.success("删除成功");

        } catch (Exception e) {
            log.error("删除favorites 失败: ", e);
            return Result.error("删除favorites失败");
        }
    }

    @PostMapping("/Favorites-update-type")
    public Result updateFavoritesTypeName(@RequestHeader("accessToken") String token,@RequestParam("type") String type,@RequestParam("newType") String newType) {
        try {
            // 解析 JWT 令牌
            User user = JwtUtils.getUserFromClaims(JwtUtils.parseJwt(token));
            if (user == null) {
                return Result.error("用户信息无效");
            }

            // 调用 Service 层获取HomePage
            favoritesService.updateFavoritesTypeName(user.getId(),type,newType);


            // 返回成功结果
            return Result.success("修改分类成功");

        } catch (Exception e) {
            log.error("修改分类favorites 失败: ", e);
            return Result.error("修改分类favorites失败");
        }
    }

}
