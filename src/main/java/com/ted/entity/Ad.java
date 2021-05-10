package com.ted.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Ad实体类
 *
 * @author Ted
 */
@Data
public class Ad implements Serializable {
    private static final long serialVersionUID = 2928942623238633473L;

    private long adId;
    private long positionId;
    private String title;
    private String photo;
    private String description;
    private String uri;
    private long startTime;
    private long endTime;
    private long sort;
    private int status;
    private long createTime;
    @JsonIgnore
    private Timestamp updateTime;

    // 其他自定义字段
    private AdPosition adPosition; // 所属位置
    private String statusLabel; // 状态标识
    private String createDate; // 发布时间
    private String startDate; // 开始时间
    private String endDate; // 结束时间
    private int isValid; // 是否有效
    private String isValidLabel; // 是否有效标识
    // 附件
    private String photoIds;
}
