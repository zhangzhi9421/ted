package com.ted.config;

import com.ted.constant.UploadConstant;
import com.ted.interceptor.AuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 授权拦截器
 *
 * @author Ted
 */
@Configuration
@PropertySource(value = "classpath:config/upload.properties")
public class AuthConfig implements WebMvcConfigurer {
    private AuthInterceptor authInterceptor;
    private UploadConstant uploadConstant;

    @Autowired
    public void setUploadConstant(UploadConstant uploadConstant) {
        this.uploadConstant = uploadConstant;
    }

    @Autowired
    public void setAuthInterceptor(AuthInterceptor authInterceptor) {
        this.authInterceptor = authInterceptor;
    }

    // 这个方法是用来配置静态资源的，比如html，js，css，等等
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/", "file:" + uploadConstant.getUploadPath());
    }

    // 这个方法用来注册拦截器，我们自己写好的拦截器需要通过这里添加注册才能生效
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // addPathPatterns("/**") 表示拦截所有的请求，
        // excludePathPatterns("/login", "/register") 表示除了登陆与注册之外，因为登陆注册不需要登陆也可以访问
        //registry.addInterceptor(authInterceptor).addPathPatterns("/**").excludePathPatterns("/login", "/register");
        registry.addInterceptor(authInterceptor).addPathPatterns("/**").excludePathPatterns("/static/**");
    }
}
