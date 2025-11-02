package com.sky.interceptor;

import com.sky.constant.JwtClaimsConstant;
import com.sky.properties.JwtProperties;
import com.sky.utils.JwtUtil;
import com.sky.utils.UserContext;                 // ✅ 用你业务在读的上下文
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

@Component
@Slf4j
public class JwtTokenUserInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtProperties jwtProperties;

    private static final Set<String> WHITE_PREFIX = Set.of(
            "/user/login", "/user/register", "/user/ping",
            "/error", "/doc.html", "/swagger-ui", "/v3/api-docs", "/swagger-resources", "/webjars"
    );

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) return true;

        final String uri = request.getRequestURI();
        if (WHITE_PREFIX.stream().anyMatch(uri::startsWith)) return true;

        String token = extractToken(request);
        if (token == null) return unauthorized(response, "缺少token");

        try {
            Claims claims = JwtUtil.parseJWT(jwtProperties.getUserSecretKey(), token);
            Object uidObj = claims.get(JwtClaimsConstant.USER_ID); // "userId"
            if (uidObj == null) return unauthorized(response, "token缺少userId");

            long userId = ((Number) uidObj).longValue();
            UserContext.setUserId(userId);                         // ✅ 放到 UserContext
            log.info("JWT用户拦截通过: uri={}, userId={}", uri, userId);
            return true;
        } catch (Exception e) {
            log.warn("JWT解析失败: {}", e.getMessage());
            return unauthorized(response, "token无效或已过期");
        }
    }

    private String extractToken(HttpServletRequest request) {
        // 1) 优先 Authorization: Bearer xxx
        String authz = request.getHeader("Authorization");
        if (authz != null && authz.startsWith("Bearer ")) {
            return authz.substring(7);
        }
        // 2) 兼容自定义头（application.yml: sky.jwt.user-token-name=authentication）
        String headerName = jwtProperties.getUserTokenName();
        String tk = request.getHeader(headerName);
        return (tk == null || tk.isBlank()) ? null : tk;
    }

    private boolean unauthorized(HttpServletResponse resp, String msg) throws IOException {
        resp.setStatus(401);
        resp.setContentType("application/json;charset=UTF-8");
        resp.getWriter().write("{\"code\":0,\"msg\":\"UNAUTHORIZED: " + msg + "\"}");
        return false;
    }
}
