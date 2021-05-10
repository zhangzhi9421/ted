package com.ted.util;

import com.ted.constant.UploadConstant;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * 上传工具类
 *
 * @author Ted
 */
@Data
@Slf4j
@Component
@PropertySource(value = "classpath:config/upload.properties")
public class UploadUtil {
    // 接收文件对象
    private MultipartFile file;
    // 储存目录（日期）
    private String dir;
    // 上传模块
    private String module = "default";

    private UploadConstant uploadConstant;

    @Autowired
    public void setUploadConstant(UploadConstant uploadConstant) {
        this.uploadConstant = uploadConstant;
    }

    public UploadUtil() {
    }

    /**
     * 构造方法
     *
     * @param file 文件对象
     */
    public UploadUtil(MultipartFile file, String module) {
        this.file = file;
        this.module = module;
    }

    /**
     * 上传
     *
     * @param file   文件对象
     * @param module 模块
     * @return 返回结果
     */
    public String upload(MultipartFile file, String module) {
        this.file = file;
        this.module = module;
        return save();
    }

    /**
     * 上传
     *
     * @param file 文件对象
     * @return 返回结果
     */
    public String upload(MultipartFile file) {
        this.file = file;
        return save();
    }

    /**
     * 上传
     *
     * @return 返回结果
     */
    public String upload() {
        return save();
    }

    /**
     * 执行
     *
     * @return 返回结果
     */
    public String save() {
        // 日期目录
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        this.dir = format.format(date);

        String resultPath = null;
        try {
            // 文件原始名称
            final String originalFilename = this.file.getOriginalFilename();
            if (StringUtils.isNotBlank(originalFilename)) {
                // 工程静态资源根目录
                log.error("根目录--------------------------" + ResourceUtils.getURL("classpath:").getPath());
                log.error("绝对路径--------------------------" + System.getProperty("user.dir"));
                String resourcePath = uploadConstant.getUploadPath() + uploadConstant.getUploadDir() + "/";//ResourceUtils.getURL("classpath:").getPath();
                log.error("resourcePath--------------" + resourcePath);
                // 文件后缀
                String fileSuffix = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
                // TODO 检测后缀名
                // 生成UUID（唯一不重复的文件名）
                String uuid = UUID.randomUUID().toString().replace("-", "");
                // 构造新文件名
                String fileName = uuid + "." + fileSuffix;
                // 完整的文件储存目录
                String modulePath = StringUtils.isNotBlank(this.module) ? (this.module + "/") : "default/";
                log.warn("modulePath---------------" + modulePath);
                String dirPath = StringUtils.isNotBlank(this.dir) ? (this.dir) : "";
                log.warn("dirPath---------------" + dirPath);
                //String basePath = resourcePath + uploadConstant.getDir() + modulePath + dirPath;
                String basePath = resourcePath + modulePath + dirPath;
                log.warn("basePath---------------" + basePath);
                // 如果不存在目录，则创建
                File fileDir = new File(basePath);
                if (!fileDir.exists()) {
                    boolean mkdirs = fileDir.mkdirs();
                    log.error("mkdir:---------------------------------------" + mkdirs);
                }
                //System.out.println("新文件绝对路径：" + fileDir.getAbsolutePath());
                // 真实目录
                //String realPath = basePath.replace('/', '\\').substring(1, basePath.length());
                /* 1.transferTo方式
                // 获取文件对象
                File file = new File(basePath, fileName);
                // 完成文件的上传
                this.file.transferTo(file);
                */
                // 完成文件的上传
                File newFile = new File(fileDir.getAbsolutePath() + File.separator + fileName);
                //System.out.println("新文件完整路径：" + newFile.getAbsolutePath());
                // 上传图片到 -> “绝对路径”
                FileUtils.copyInputStreamToFile(this.file.getInputStream(), newFile);
                // 返回
                resultPath = "/" + modulePath + dirPath + "/" + fileName;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultPath;
    }
}
