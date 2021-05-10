package com.ted.interceptor;

import com.ted.annotation.AuthRequired;
import com.ted.entity.Admin;
import com.ted.service.AdminService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Enumeration;

@Slf4j
@Component
@PropertySource(value = {"classpath:config/general.properties"})
public class AuthInterceptor implements HandlerInterceptor {
    @Value("${tokenHeader}")
    private String tokenHeader;

    private AdminService adminService;

    @Autowired
    public void setAdminService(@Qualifier("AdminService") AdminService adminService) {
        this.adminService = adminService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod)) {
            return true; // 没有实例化的方法直接通过
        }

        // 拦截处理代码
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        // 判断接口是否需要认证
        AuthRequired authorizationRequired = method.getAnnotation(AuthRequired.class);
        if (!ObjectUtils.isEmpty(authorizationRequired)) {
            // 这个是需要拦截的方法
            log.info("...必须授权才能访问...");

            // 请求头
            log.warn("----------------请求头.start.....");
            Enumeration<String> enums = request.getHeaderNames();
            while (enums.hasMoreElements()) {
                String name = enums.nextElement();
                log.warn(name + ": {}", request.getHeader(name));
            }
            log.warn("----------------请求头.end.....");

            String token = request.getHeader(tokenHeader);
            if (StringUtils.isBlank(token)) {
                token = request.getParameter("token");
            }
            log.error("token---------------------" + token);

            // 根据token获取用户信息
            // 写判断是否登录的逻辑...
            Admin admin = adminService.getAdminByToken(token);
            // 用户不存在
            if (ObjectUtils.isEmpty(admin)) {
                log.error("========================================要登录才能访问======================================");
                return false;
            }
            // 验证是否禁用
            if (admin.getStatus() <= 0) {
                log.error("========================================账户已禁用======================================");
                return false;
            }
            adminService.updateValid(admin);
        } else {
            // 这个是不需要拦截的方法
            log.warn("...不需要授权就可以访问...");
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) {
        //log.info("--------------处理请求完成后视图渲染之前的处理操作---------------");
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) {
        //log.info("---------------视图渲染之后的操作-------------------------");
    }
}
