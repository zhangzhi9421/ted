package com.ted.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 跨域配置
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    /**
     * 跨域请求配置
     *
     * @param registry 跨域注册器
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        WebMvcConfigurer.super.addCorsMappings(registry);
        registry.addMapping("/**") // 对接口配置跨域设置
                .allowedHeaders("*") // 允许任何头
                .allowedMethods("*") // 允许方法（post、get等）
                .allowedOrigins("*") // 允许任何域名使用
                .allowCredentials(false);
    }
}
