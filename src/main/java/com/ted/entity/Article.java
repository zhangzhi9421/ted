package com.ted.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Article实体类
 *
 * @author Ted
 */
@Data
public class Article implements Serializable {
    private static final long serialVersionUID = -3266349311876262279L;

    private long aid;
    private long categoryId;
    private String articleTitle;
    private String subtitle;
    private String summary;
    private String keywords;
    private int recommend;
    private String photo;
    private String source;
    private String author;
    private long hits;
    private int status;
    private int sort;
    private String seoTitle;
    private String seoKeywords;
    private String seoDescription;
    private long createTime;
    @JsonIgnore
    private Timestamp updateTime;

    // 其他自定义字段
    private String categoryName; // 分类名称
    private StringBuilder categoryNodePath; // 分类节点路径
    private String statusLabel; // 状态标识
    private String createDate; // 发布时间
    private Content content; // 文章内容
    // 附件
    private String photoIds;
}
