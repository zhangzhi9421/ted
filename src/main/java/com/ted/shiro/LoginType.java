package com.ted.shiro;

/**
 * 登录模式
 */
public enum LoginType {
    /**
     * 密码登录
     */
    PASSWORD(0),
    /**
     * 免密登录
     */
    WITHOUT_PASSWORD(1);

    /**
     * 状态值
     */
    private final int code;

    /**
     * @param code code
     */
    LoginType(int code) {
        this.code = code;
    }

    /**
     * @return code
     */
    public int getCode () {
        return code;
    }
}
