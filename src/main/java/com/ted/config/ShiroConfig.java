package com.ted.config;

import at.pollux.thymeleaf.shiro.dialect.ShiroDialect;
import org.apache.shiro.mgt.RememberMeManager;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.Cookie;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;

/*
 * 1.Subject：代表当前正在执行操作的用户，但Subject代表的可以是人，也可以是任何第三方系统帐号。当然每个subject实例都会被绑定到SecurityManger上。
 * 2.SecurityManger：SecurityManager是Shiro核心，主要协调Shiro内部的各种安全组件，这个我们不需要太关注，只需要知道可以设置自定的Realm。
 * 3.Realm：用户数据和Shiro数据交互的桥梁，比如需要用户身份认证、权限认证。都是需要通过Realm来读取数据。
 */
@Configuration
public class ShiroConfig {
    /**
     * 这是shiro的大管家，相当于mybatis里的SqlSessionFactoryBean
     *
     * @param securityManager 安全管理器
     * @return ShiroFilterFactoryBean
     */
    @Bean(name = "shiroFilter")
    public ShiroFilterFactoryBean shiroFilter(DefaultWebSecurityManager securityManager) {
        ShiroFilterFactoryBean bean = new ShiroFilterFactoryBean();
        // 设置安全管理器
        bean.setSecurityManager(securityManager);
        // 添加Shiro内置过滤器
        /*
        anon: 无需认证就可以访问
        authc: 必须认证了才可以访问
        user: 必须拥有“记住我”功能才能用
        perms: 必须拥有某权限才能访问
        role: 拥有某个角色才能访问
         */
        LinkedHashMap<String, String> filterMap = new LinkedHashMap<>();
        // 添加过滤器
        filterMap.put("/shiro/add", "authc");
        // 登出
        filterMap.put("/shiro/logout", "logout");
        // 设置登录入口
        bean.setLoginUrl("/shiro/toLogin");
        bean.setSuccessUrl("/shiro");

        bean.setFilterChainDefinitionMap(filterMap);
        return bean;
    }

    /**
     * @param userRealm userRealm
     * @return DefaultWebSecurityManager
     */
    @Bean
    public DefaultWebSecurityManager securityManager(@Qualifier("userRealm") UserRealm userRealm) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        // 设置Realm
        securityManager.setRealm(userRealm);
        return securityManager;
    }

    /**
     * @return UserRealm
     */
    @Bean(name = "userRealm")
    public UserRealm userRealm() {
        return new UserRealm();
    }

    /**
     * session过期控制
     *
     * @return DefaultWebSessionManager
     */
    @Bean
    public DefaultWebSessionManager sessionManager() {
        DefaultWebSessionManager defaultWebSessionManager = new DefaultWebSessionManager();
        long timeout = 60L * 1000 * 60;//毫秒级别 设置session过期时间3600s
        defaultWebSessionManager.setGlobalSessionTimeout(timeout);
        return defaultWebSessionManager;
    }

    /**
     * 记住我的配置
     *
     * @return RememberMeManager
     */
    @Bean
    public RememberMeManager rememberMeManager() {
        Cookie cookie = new SimpleCookie("rememberMe");
        cookie.setHttpOnly(true); // 通过js脚本将无法读取到cookie信息
        cookie.setMaxAge(60 * 60 * 24); // cookie保存一天
        CookieRememberMeManager manager = new CookieRememberMeManager();
        manager.setCookie(cookie);
        return manager;
    }

    /**
     * 整合thymeleaf
     * 启用shiro方言，这样能在页面上使用shiro标签
     *
     * @return ShiroDialect
     */
    @Bean
    public ShiroDialect getShiroDialect() {
        return new ShiroDialect();
    }

    /**
     * 启用shiro注解 加入注解的使用，不加入这个注解不生效
     *
     * @param securityManager 安全管理器
     * @return AuthorizationAttributeSourceAdvisor
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor getAuthorizationAttributeSourceAdvisor(DefaultWebSecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(securityManager);
        return advisor;
    }
}
