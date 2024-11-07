package org.inhuman.smartplatform.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.inhuman.smartplatform.pojo.User;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

public class JwtUtils {


    private static final byte[] secretKeyBytes = "NotHumanAtAllNotHumanAtAllNotHumanAtAllNotHumanAtAll".getBytes(StandardCharsets.UTF_8);
    private static final byte[] ResecretKeyBytes = "InHumanInHumanInHumanInHumanInHumanInHumanInHuman".getBytes(StandardCharsets.UTF_8);

    private static final Long accessTokenExpire =  600 * 60 * 1000L;  // Access Token 过期时间 15 分钟
    private static final Long refreshTokenExpire = 7 * 24 * 60 * 60 * 1000L; // Refresh Token 过期时间 7 天


    public static String generateJwt(Map<String, Object> claims) {

        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpire)) // 设置过期时间
                .signWith(Keys.hmacShaKeyFor(secretKeyBytes), SignatureAlgorithm.HS256) // 使用正确的签名密钥和算法
                .compact();
    }

    public static String generateRefreshToken(Map<String, Object> claims) {

        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpire)) // 设置过期时间
                .signWith(Keys.hmacShaKeyFor(ResecretKeyBytes), SignatureAlgorithm.HS256) // 使用正确的签名密钥和算法
                .compact();
    }

    public static Claims parseJwt(String jwt){

        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secretKeyBytes)) // 使用与生成时相同的密钥
                .build()
                .parseClaimsJws(jwt)
                .getBody();
    }

    // 解析 Refresh Token
    public static Claims parseRefreshToken(String jwt) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(ResecretKeyBytes))
                .build()
                .parseClaimsJws(jwt)
                .getBody();
    }

    public static User getUserFromClaims(Claims claims){
        if(claims == null){
            return null;
        }else{
            User user = new User();
            user.setId(claims.get("id", Integer.class));
            user.setUserName(claims.get("userName", String.class));
            user.setPosition(claims.get("position", Integer.class));
            return user;
        }
    }
}
