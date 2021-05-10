package com.ted.entity;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Category实体类
 *
 * @author Ted
 */
@Data
@PropertySource(value = {"classpath:config/general.properties"})
public class Category implements Serializable {
    private static final long serialVersionUID = -76890140437730269L;

    private long categoryId;
    private String categoryName;
    private long parentId;
    private String photo;
    private String description;
    @Value("${maxInt}")
    private int sort;
    private long createTime;
    private Timestamp updateTime;

    // 子类集合
    private List<Category> children = new ArrayList<>();
    private String createDate; // 发布时间
    private String photoIds; // 附件
    private int deep; // 层级
    private String prefix; // 前缀，用于列表显示分层
}
