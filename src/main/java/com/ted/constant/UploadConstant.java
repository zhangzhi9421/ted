package com.ted.constant;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * 上传常量
 *
 * @author Ted
 */
@Data
@Component
@PropertySource(value = {"classpath:config/upload.properties"})
@ConfigurationProperties(prefix = "upload")
public class UploadConstant {
    private String uploadDir; // 上传文件的根目录文件夹名
    private String dir; // 作废
    private String uploadPath = System.getProperty("user.dir") + "/static/"; // 上传文件的物理绝对路径
    private String typeImage;
    private String typeFile;
}
