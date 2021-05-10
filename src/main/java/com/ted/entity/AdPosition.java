package com.ted.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * AdPosition实体类
 *
 * @author Ted
 */
@Data
public class AdPosition implements Serializable {
    private static final long serialVersionUID = -165415495924064556L;

    private long positionId;
    private String title;
    private int width;
    private int height;
    private long createTime;
    @JsonIgnore
    private Timestamp updateTime;

    // 其他自定义字段
    private String createDate; // 发布时间
}
