package com.ted.entity;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.math.BigInteger;
import java.sql.Timestamp;

/**
 * Attachment实体类
 *
 * @author Ted
 */
@Data
@Component
@PropertySource(value = {"classpath:config/general.properties"})
public class Attachment  implements Serializable {
    private static final long serialVersionUID = 8210637320678532468L;

    private BigInteger fileId;
    private String fileName;
    private long fileSize;
    private String filePath;
    private String fileType;
    @Value("${statusDeleted}")
    private BigInteger tableId;
    private String tableName;
    private String tableField;
    @Value("${statusDeleted}")
    private int status;
    private long createTime;
    private Timestamp updateTime;

    private BigInteger id; // 前端显示的ID号
    private String name; // 文件名称
    private String url; // 文件路径
    private String sizeLabel; // 文件大小
    private String statusLabel; // 状态
    private String createDate; // 发布时间
}
