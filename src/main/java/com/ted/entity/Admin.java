package com.ted.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;
import java.math.BigInteger;
import java.sql.Timestamp;

/**
 * Admin实体类
 *
 * @author Ted
 */
@Data
public class Admin implements Serializable {
    private static final long serialVersionUID = 8458507738732742926L;

    private BigInteger uid;
    private String username;
    private String password;
    private String nickname;
    private String realName;
    private String profile;
    private int gender;
    private String phone;
    private int status;
    @JsonIgnore
    private String token;
    @JsonIgnore
    private long validTime;
    private long createTime;
    @JsonIgnore
    private Timestamp updateTime;

    private String statusLabel;
    private String genderLabel;
    private String createDate;
    private String confirmPassword;
    private String profileIds;
}
