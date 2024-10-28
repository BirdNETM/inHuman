package org.inhuman.smartplatform.interceptor;

import com.alibaba.fastjson.JSONObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.inhuman.smartplatform.pojo.Result;
import org.inhuman.smartplatform.utils.JwtUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@Component
public class LoginCheckInterceptor implements HandlerInterceptor {

    @Override   // 目标资源运行前运行，true->放行
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        log.info("uri: {}", uri);

        String jwt = request.getHeader("accessToken");

        if (!StringUtils.hasLength(jwt)) {  // 使用正确的 StringUtils
            log.info("jwt is empty");
            log.info(jwt);
            Result error = Result.error("NOT_LOGIN");
            String notLogin = JSONObject.toJSONString(error);  // 使用 FastJSON 库进行序列化
            response.setCharacterEncoding("UTF-8");  // 解决中文乱码问题
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(notLogin);
            return false;
        }

        try {
            log.info(jwt);
            JwtUtils.parseJwt(jwt);

        } catch (Exception e) {
            log.info("jwt parse error");
            Result error = Result.error("NOT_LOGIN");
            String notLogin = JSONObject.toJSONString(error);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(notLogin);
            return false;  // 确保解析错误时阻止请求继续执行
        }

        log.info("jwt parse success");
        return true;
    }

    @Override   // 目标资源运行后运行
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        System.out.println("postHandle");
    }

    @Override   // 视图渲染后运行
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        System.out.println("afterCompletion");
    }
}
