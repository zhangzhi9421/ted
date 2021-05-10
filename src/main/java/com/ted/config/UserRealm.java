package com.ted.config;

import com.ted.shiro.UserToken;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.security.crypto.bcrypt.BCrypt;

/**
 * 自定义Realm，控制用户授权、认证
 */
public class UserRealm extends AuthorizingRealm {

    // 授权
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        System.out.println("执行了【授权】---------------------------------");
        return null;
    }

    // 认证
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        System.out.println("执行了【认证】===================================");
        // 获取登录信息
        UserToken userToken = (UserToken) authenticationToken;
        System.out.println("userToken----" + userToken.toString());
        String username = userToken.getUsername();
        String password = new String(userToken.getPassword());
        System.out.println("username----" + username);
        System.out.println("password----" + password);
        System.out.println("type----" + userToken.getType().getCode());

        // 加密
        String hashPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        System.out.println("加密后：==========================" + hashPassword);

        // 获取用户对象

        return null;
    }
}
