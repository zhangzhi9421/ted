package com.ted.controller;

import com.ted.annotation.AuthRequired;
import com.ted.constant.LanguageConstant;
import com.ted.constant.UploadConstant;
import com.ted.converter.DateConverter;
import com.ted.entity.Attachment;
import com.ted.service.AttachmentService;
import com.ted.util.JsonUtil;
import com.ted.util.UploadUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;

@Slf4j
@RestController
@RequestMapping(value = "/upload")
public class UploadController extends BaseController {
    private final UploadUtil uploadUtil;
    private AttachmentService attachmentService;
    private JsonUtil<ArrayList<HashMap<String, Object>>> jsonUtil;
    private LanguageConstant languageConstant;
    private UploadConstant uploadConstant;
    private DateConverter dateConverter;

    @Autowired
    public void setDateConverter(DateConverter dateConverter) {
        this.dateConverter = dateConverter;
    }

    @Autowired
    public void setUploadConstant(UploadConstant uploadConstant) {
        this.uploadConstant = uploadConstant;
    }

    @Autowired
    public void setLanguageConstant(LanguageConstant languageConstant) {
        this.languageConstant = languageConstant;
    }

    @Autowired(required = false)
    public void setJsonUtil(JsonUtil<ArrayList<HashMap<String, Object>>> jsonUtil) {
        this.jsonUtil = jsonUtil;
    }

    @Autowired
    public void setAttachmentService(@Qualifier("AttachmentService") AttachmentService attachmentService) {
        this.attachmentService = attachmentService;
    }

    @Autowired
    public UploadController(UploadUtil uploadUtil) {
        this.uploadUtil = uploadUtil;
    }

    /**
     * 上传文件
     *
     * @param files 文件
     * @param module 上传模块
     * @return 返回数据
     */
    @AuthRequired
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/", method = RequestMethod.POST)
    public String upload(@RequestParam(value = "file") MultipartFile[] files, @RequestParam(value = "module", required = false, defaultValue = "default") String module) {
        int sizes = files.length;

        if (sizes <= 0) {
            return jsonUtil.error(languageConstant.getDataNull(), null);
        }
        // 域名
        String basePath = "";
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
        }
        ArrayList<HashMap<String, Object>> list = new ArrayList<>();
        HashMap<String, Object> map = new HashMap<>();
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                HashMap<String, Object> clone = (HashMap<String, Object>) map.clone();
                long fileSize = file.getSize(); // 文件大小，字节
                String fileName = file.getOriginalFilename(); // 文件名
                log.warn("fileName====" + fileName);
                if (StringUtils.isNotBlank(fileName)) {
                    String fileType = fileName.substring(fileName.lastIndexOf(".") + 1); // 文件后缀
                    log.warn("fileType====" + fileType);
                    log.warn("fileSize====" + fileSize);
                    // 上传文件
                    String filePath = uploadUtil.upload(file, module);
                    // 上传成功
                    if (StringUtils.isNotBlank(filePath)) {
                        Attachment attachment = new Attachment();
                        // 插入附件表
                        attachment.setFileName(fileName);
                        attachment.setFileSize(fileSize);
                        attachment.setFilePath(filePath);
                        attachment.setFileType(fileType);
                        attachment.setCreateTime(dateConverter.timeSecond(dateConverter.timestampFormat()));
                        log.error("attachment=" + attachment);
                        int insertAttachment = attachmentService.insertAttachment(attachment);
                        if (insertAttachment > 0) {
                            clone.put("id", attachment.getFileId());
                            clone.put("name", fileName);
                            clone.put("path", filePath);
                            clone.put("url", basePath + uploadConstant.getUploadDir() + filePath);
                            list.add(clone);
                        }
                    }
                }
            }
        }
        return jsonUtil.success(list);
    }

    /**
     * 上传文件
     * 富文本编辑器
     *
     * @param files 文件
     * @param module 上传模块
     * @return 返回数据
     */
    @AuthRequired
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/editor", method = RequestMethod.POST)
    public String uploadEditor(@RequestParam(value = "file") MultipartFile[] files, @RequestParam(value = "module", required = false, defaultValue = "default") String module) {
        int sizes = files.length;

        if (sizes <= 0) {
            return jsonUtil.error(languageConstant.getDataNull(), null);
        }
        // 域名
        String basePath = "";
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
        }
        ArrayList<HashMap<String, Object>> list = new ArrayList<>();
        HashMap<String, Object> map = new HashMap<>();
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                HashMap<String, Object> clone = (HashMap<String, Object>) map.clone();
                long fileSize = file.getSize(); // 文件大小，字节
                String fileName = file.getOriginalFilename(); // 文件名
                log.warn("fileName====" + fileName);
                if (StringUtils.isNotBlank(fileName)) {
                    String fileType = fileName.substring(fileName.lastIndexOf(".") + 1); // 文件后缀
                    log.warn("fileType====" + fileType);
                    log.warn("fileSize====" + fileSize);
                    // 上传文件
                    String filePath = uploadUtil.upload(file, module);
                    // 上传成功
                    clone.put("name", fileName);
                    clone.put("path", filePath);
                    clone.put("url", basePath + uploadConstant.getUploadDir() + filePath);
                    list.add(clone);
                }
            }
        }
        return jsonUtil.success(list);
    }
}
