package org.inhuman.smartplatform.config;

import org.inhuman.smartplatform.interceptor.LoginCheckInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private LoginCheckInterceptor loginCheckInterceptor;

    // 配置登录拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginCheckInterceptor)
                .addPathPatterns("/**") // 拦截所有请求路径
                .excludePathPatterns("/login", "/refresh-token", "/public/**"); // 排除登录、刷新Token和公共路径
    }

    // 全局跨域配置
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**")
//                .allowedOrigins("http://192.168.6.78:5173")  // 指定允许的域名（可以是前端地址）
//                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")  // 允许的请求方法
//                .allowedHeaders("*")  // 允许的请求头
//                .allowCredentials(true)  // 是否允许发送 Cookie 和认证信息
//                .maxAge(3600);  // 设置预检请求的有效期（3600 秒，即 1 小时）
//    }
}
