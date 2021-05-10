package com.ted.shiro;

import org.apache.shiro.authc.UsernamePasswordToken;

public class UserToken extends UsernamePasswordToken {
    private static final long serialVersionUID = -3784837733767288566L;
    private LoginType type;

    UserToken(){
        super();
    }

    public UserToken(String username, String password, LoginType type, boolean rememberMe, String host) {
        super(username, password, rememberMe, host);
        this.type = type;
    }

    public LoginType getType() {
        return type;
    }

    public void setType(LoginType type) {
        this.type = type;
    }

    /**
     * 账号密码登录
     * @param username 用户名
     * @param password 密码
     */
    public UserToken(String username, String password) {
        super(username, password, false, null);
        this.type = LoginType.PASSWORD;
    }

    /**
     * 免密登录
     * @param username 用户名
     */
    public UserToken(String username) {
        super(username, "", false, null);
        this.type = LoginType.WITHOUT_PASSWORD;
    }
}
