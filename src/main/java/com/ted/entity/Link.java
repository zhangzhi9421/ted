package com.ted.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Link实体类
 *
 * @author Ted
 */
@Data
public class Link implements Serializable {
    private static final long serialVersionUID = -1543352289016221597L;

    private long linkId;
    private long categoryId;
    private String linkTitle;
    private String uri;
    private String photo;
    private long hits;
    private int status;
    private int sort;
    private long createTime;
    @JsonIgnore
    private Timestamp updateTime;

    // 其他自定义字段
    private String categoryName; // 分类名称
    private StringBuilder categoryNodePath; // 分类节点路径
    private String statusLabel; // 状态标识
    private String createDate; // 发布时间
    // 附件
    private String photoIds;
}
