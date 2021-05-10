package com.ted.entity;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * Log实体类
 *
 * @author Ted
 */
@Data
@Component
public class Log implements Serializable {
    private static final long serialVersionUID = -6227138642225645425L;

    private BigInteger id;
    private BigInteger uid;
    private String username;
    private String module;
    private String type;
    private String description;
    private String params;
    private String method;
    private String uri;
    private long ip;
    private long createTime;

    private String ipAddress; // ip地址
    private String createDate; // 发布时间
}
